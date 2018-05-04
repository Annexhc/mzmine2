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

package net.sf.mzmine.modules.rawdatamethods.recalibrationmz;

import java.awt.Color;
import java.io.IOException;
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
import net.sf.mzmine.datamodel.MZmineProject;
import net.sf.mzmine.datamodel.RawDataFile;
import net.sf.mzmine.datamodel.RawDataFileWriter;
import net.sf.mzmine.datamodel.Scan;
import net.sf.mzmine.main.MZmineCore;
import net.sf.mzmine.modules.MZmineProcessingStep;
import net.sf.mzmine.parameters.ParameterSet;
import net.sf.mzmine.parameters.parametertypes.selectors.ScanSelection;
import net.sf.mzmine.taskcontrol.AbstractTask;
import net.sf.mzmine.taskcontrol.TaskStatus;

public class RecalibrationMZTask extends AbstractTask {

  private Logger logger = Logger.getLogger(this.getClass().getName());

  private final RawDataFile dataFile;
  private final MZmineProject project;
  private RawDataFile newRawDataFile;

  // scan counter
  private int processedScans = 0, totalScans = 0;
  private ScanSelection scanSelection;

  // Recalibration method
  private MZmineProcessingStep<RecalibrationMZMethod> recalibrationMZMethod;

  private String suffix;
  private boolean removeOld;

  /**
   * @param dataFile
   * @param parameters
   */
  public RecalibrationMZTask(MZmineProject project, RawDataFile dataFile, ParameterSet parameters) {

    this.dataFile = dataFile;
    this.project = project;

    this.recalibrationMZMethod =
        parameters.getParameter(RecalibrationMZParameters.recalibrationMZMethod).getValue();

    this.scanSelection =
        parameters.getParameter(RecalibrationMZParameters.scanSelection).getValue();

    this.suffix = parameters.getParameter(RecalibrationMZParameters.suffix).getValue();

    this.removeOld = parameters.getParameter(RecalibrationMZParameters.removeOld).getValue();
  }

  /**
   * @see net.sf.mzmine.taskcontrol.Task#getTaskDescription()
   */
  public String getTaskDescription() {
    return "Recalibrating m/z values in " + dataFile;
  }

  /**
   * @see net.sf.mzmine.taskcontrol.Task#getFinishedPercentage()
   */
  public double getFinishedPercentage() {
    if (totalScans == 0)
      return 0;
    else
      return (double) processedScans / totalScans;
  }

  public RawDataFile getDataFile() {
    return dataFile;
  }

  /**
   * @see Runnable#run()
   */
  public void run() {

    setStatus(TaskStatus.PROCESSING);

    logger.info("Started m/z recalibration on " + dataFile);

    final Scan scans[] = scanSelection.getMatchingScans(dataFile);
    totalScans = scans.length;

    RecalibrationMZMethod method = recalibrationMZMethod.getModule();

    RawDataFileWriter newRawDataFileWriter = null;
    try {
      newRawDataFileWriter = MZmineCore.createNewFile(dataFile.getName() + suffix);
    } catch (IOException e) {
      logger.info("Error, could not create file" + "\n" + e.getMessage());
      e.printStackTrace();
    }

    // To do array length for trendline m/z range
    XYSeriesCollection seriesCollection = new XYSeriesCollection();
    XYSeries massSeries = new XYSeries("m/z values scan ");
    double[] xValues = new double[totalScans];
    double[] yValues = new double[totalScans];
    // Loop through all scans
    for (int i = 0; i < totalScans; i++) {
      // m/z vs deviation
      // Scan scan = method.getScan(scans[i], recalibrationMZMethod.getParameterSet());
      // double[][] trendlineXYValues = method.getTrendlineXYValues();
      // double[][] massXYValues = method.getMassXYValues();
      // XYSeries trendlineSeries = new XYSeries("Trendline scan " + i);
      // XYSeries massSeries = new XYSeries("m/z values scan " + i);
      // for (int j = 0; j < trendlineXYValues[0].length; j++) {
      // trendlineSeries.add(trendlineXYValues[0][j], trendlineXYValues[1][j]);
      // }
      // for (int j = 0; j < massXYValues[0].length; j++) {
      // massSeries.add(massXYValues[0][j], massXYValues[1][j]);
      // }
      // seriesCollection.addSeries(trendlineSeries);
      // seriesCollection.addSeries(massSeries);

      // scans vs deviation
      Scan scan = method.getScan(scans[i], recalibrationMZMethod.getParameterSet());
      double[][] trendlineXYValues = method.getXYValuesDeviationPerScanTrendline();
      double[][] massXYValues = method.getXYValuesDeviationPerScan();
      XYSeries trendlineSeries = new XYSeries("Trendline scan ");
      // for (int j = 0; j < trendlineXYValues[0].length; j++) {
      // trendlineSeries.add(trendlineXYValues[0][j], trendlineXYValues[1][j]);
      // }
      for (int j = 0; j < massXYValues[0].length; j++) {
        massSeries.add(massXYValues[0][j], massXYValues[1][j]);
      }

      try {
        newRawDataFileWriter.addScan(scan);
        processedScans++;
      } catch (IOException e) {
        logger.info("Error, could not add scan " + scan.getScanNumber() + "\n" + e.getMessage());
        e.printStackTrace();
      }
    }
    // seriesCollection.addSeries(trendlineSeries);
    seriesCollection.addSeries(massSeries);
    System.out.println("Start doing plot");
    JFreeChart chart = getTrendlineChart(seriesCollection);
    ChartFrame frame = new ChartFrame("Fit", chart);
    frame.setVisible(true);
    frame.setSize(450, 500);
    System.out.println("Finished doing plot");
    if (!isCanceled()) {

      // Finalize writing
      try {
        newRawDataFile = newRawDataFileWriter.finishWriting();
      } catch (IOException e) {
        logger.info("Error, could not finish writing raw file" + "\n" + e.getMessage());
        e.printStackTrace();
      }

      // Add the newly created file to the project
      project.addFile(newRawDataFile);

      // Remove the original data file if requested
      if (removeOld == true) {
        project.removeFile(dataFile);
      }

      setStatus(TaskStatus.FINISHED);

      logger.info("Finished m/z recalibration on " + dataFile);
    }
  }

  private JFreeChart getTrendlineChart(XYDataset dataSet) {
    JFreeChart chart = ChartFactory.createXYLineChart("", // title
        "m/z", // x-axis label
        "deviation ppm", // y-axis label
        dataSet, // data set
        PlotOrientation.VERTICAL, // orientation
        false, // create legend?
        false, // generate tooltips?
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
}
