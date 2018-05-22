/*
 * Copyright 2006-2015 The MZmine 2 Development Team
 * 
 * This file is part of MZmine 2.
 * 
 * MZmine 2 is free software; you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * MZmine 2 is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with MZmine 2; if not,
 * write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301
 * USA
 */

package net.sf.mzmine.modules.peaklistmethods.featurecorrelation;

import java.awt.Color;
import java.util.logging.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import com.google.common.collect.Range;
import net.sf.mzmine.datamodel.PeakList;
import net.sf.mzmine.datamodel.PeakList.PeakListAppliedMethod;
import net.sf.mzmine.datamodel.PeakListRow;
import net.sf.mzmine.datamodel.RawDataFile;
import net.sf.mzmine.datamodel.impl.SimplePeakList;
import net.sf.mzmine.datamodel.impl.SimplePeakListAppliedMethod;
import net.sf.mzmine.desktop.Desktop;
import net.sf.mzmine.desktop.impl.HeadLessDesktop;
import net.sf.mzmine.main.MZmineCore;
import net.sf.mzmine.modules.peaklistmethods.identification.fragmentsearch.FragmentIdentity;
import net.sf.mzmine.parameters.ParameterSet;
import net.sf.mzmine.parameters.parametertypes.tolerances.MZTolerance;
import net.sf.mzmine.parameters.parametertypes.tolerances.RTTolerance;
import net.sf.mzmine.taskcontrol.AbstractTask;
import net.sf.mzmine.taskcontrol.TaskStatus;
import net.sf.mzmine.util.regression.PolyTrendLine;
import net.sf.mzmine.util.regression.TrendLine;

public class FeatureCorrelationTask extends AbstractTask {

  private Logger logger = Logger.getLogger(this.getClass().getName());

  private int finishedRows, totalRows;
  private PeakList peakList;

  private RTTolerance rtTolerance;
  private MZTolerance mzTolerance;
  private PeakListRow selectedPeaks[];
  private double minCoefficientOfDetermination;

  private ParameterSet parameters;

  /**
   * @param parameters
   * @param peakList
   */
  public FeatureCorrelationTask(ParameterSet parameters, PeakList peakList) {

    this.peakList = peakList;
    this.parameters = parameters;

    selectedPeaks = parameters.getParameter(FeatureCorrelationParameters.peakSelection)
        .getMatchingRows(peakList);
    minCoefficientOfDetermination = parameters
        .getParameter(FeatureCorrelationParameters.minCoefficientOfDetermination).getValue();
  }

  /**
   * @see net.sf.mzmine.taskcontrol.Task#getFinishedPercentage()
   */
  public double getFinishedPercentage() {
    if (totalRows == 0)
      return 0;
    return ((double) finishedRows) / totalRows * selectedPeaks.length;
  }

  /**
   * @see net.sf.mzmine.taskcontrol.Task#getTaskDescription()
   */
  public String getTaskDescription() {
    return "Correlating features in " + peakList;
  }

  /**
   * @see java.lang.Runnable#run()
   */
  public void run() {

    setStatus(TaskStatus.PROCESSING);

    logger.info("Starting correlation of features in " + peakList);


    // Get data file information
    final RawDataFile dataFile = peakList.getRawDataFile(0);

    PeakListRow allRows[] = peakList.getRows();
    XYSeriesCollection seriesCollection = new XYSeriesCollection();
    // Compare each row with each selected peak
    for (int i = 0; i < selectedPeaks.length; i++) {
      Range<Double> rtRange = selectedPeaks[i].getBestPeak().getRawDataPointsRTRange();
      int[] scanNumbersForCorrelationComparison = selectedPeaks[i].getBestPeak().getScanNumbers();
      // Create new peak list
      final PeakList correlatedFeatures =
          new SimplePeakList(peakList + " correlated features to feature" + i + 1, dataFile);
      // Load previous applied methods.
      for (final PeakListAppliedMethod method : peakList.getAppliedMethods()) {
        correlatedFeatures.addDescriptionOfAppliedTask(method);
      }
      for (int j = 0; j < allRows.length; j++) {
        Range<Double> rtRangeCompare = allRows[j].getBestPeak().getRawDataPointsRTRange();
        XYSeries correlationSeries = new XYSeries("Correlation series " + i + " and " + j);
        double[] xValues = new double[scanNumbersForCorrelationComparison.length];
        double[] yValues = new double[scanNumbersForCorrelationComparison.length];
        if (rtRange == rtRangeCompare) {
          for (int k =
              scanNumbersForCorrelationComparison[0]; k < scanNumbersForCorrelationComparison.length
                  - scanNumbersForCorrelationComparison[0]; k++) {
            try {
              xValues[k] = selectedPeaks[i].getBestPeak().getDataPoint(k).getIntensity();
              yValues[k] = allRows[j].getBestPeak().getDataPoint(k).getIntensity();
            } catch (Exception e) {
              System.out.println("Intensity error");
            }

          }
          if (checkCorrelation(xValues, yValues)) {
            correlatedFeatures.addRow(allRows[j]);
            // write data to series
            for (int l = 0; l < yValues.length; l++) {
              correlationSeries.add(xValues[l], yValues[l]);
            }
            seriesCollection.addSeries(correlationSeries);
          }
        }
        finishedRows++;
      }
      MZmineCore.getProjectManager().getCurrentProject().addPeakList(correlatedFeatures);

    }

    // show correlation plot
    System.out.println("Start doing plot");

    JFreeChart chart = getTrendlineChart(seriesCollection);
    ChartFrame frame = new ChartFrame("Fit", chart);
    frame.setVisible(true);
    frame.setSize(450, 500);

    // Add task description to peakList
    ((SimplePeakList) peakList).addDescriptionOfAppliedTask(
        new SimplePeakListAppliedMethod("Identification of fragments", parameters));

    // Repaint the window to reflect the change in the peak list
    Desktop desktop = MZmineCore.getDesktop();
    if (!(desktop instanceof HeadLessDesktop))
      desktop.getMainWindow().repaint();

    setStatus(TaskStatus.FINISHED);

    logger.info("Finished feature correlation in " + peakList);

  }

  private boolean checkCorrelation(double[] xValues, double[] yValues) {
    TrendLine trendline = new PolyTrendLine(1);
    trendline.getRSquared(yValues, xValues);
    if (trendline.getRSquared(yValues, xValues) >= minCoefficientOfDetermination) {
      return true;
    } else {
      return false;
    }
  }

  private JFreeChart getTrendlineChart(XYDataset dataSet) {
    JFreeChart chart = ChartFactory.createXYLineChart("", // title
        "Intensity 1", // x-axis label
        "Intensity 2", // y-axis label
        dataSet, // data set
        PlotOrientation.VERTICAL, // orientation
        true, // create legend?
        true, // generate tooltips?
        false // generate URLs?
    );
    XYPlot plot = (XYPlot) chart.getPlot();
    ChartPanel chartPanel = new ChartPanel(chart);
    chart.setBackgroundPaint(Color.white);
    chartPanel.setChart(chart);

    XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
    // Disable maximum size (we don't want scaling).
    chartPanel.setMaximumDrawWidth(Integer.MAX_VALUE);
    chartPanel.setMaximumDrawHeight(Integer.MAX_VALUE);

    // Set the plot properties.
    plot = chart.getXYPlot();
    plot.setBackgroundPaint(Color.white);
    // for (int i = 0; i < plot.getSeriesCount(); i = i + 2) {
    // plot.setRenderer(renderer);
    // plot.getRenderer().setSeriesPaint(i + 1, Color.black);
    // renderer.setSeriesLinesVisible(i + 1, false);
    // renderer.setSeriesLinesVisible(i, true);
    // renderer.setSeriesShapesVisible(i, false);
    // }

    for (int i = 0; i < plot.getSeriesCount(); i++) {
      plot.setRenderer(renderer);
      renderer.setSeriesLinesVisible(i, false);
    }

    plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);

    // Set grid properties.
    plot.setDomainGridlinePaint(Color.white);
    plot.setRangeGridlinePaint(Color.white);
    plot.setOutlineVisible(false);

    return chart;
  }



  /**
   * Add new identity to the fragment row
   * 
   * @param mainRow
   * @param fragmentRow
   */
  private void addFragmentInfo(PeakListRow mainRow, PeakListRow fragmentRow) {
    FragmentIdentity newIdentity = new FragmentIdentity(mainRow);
    fragmentRow.addPeakIdentity(newIdentity, false);

    // Notify the GUI about the change in the project
    MZmineCore.getProjectManager().getCurrentProject().notifyObjectChanged(fragmentRow, false);

  }

}
