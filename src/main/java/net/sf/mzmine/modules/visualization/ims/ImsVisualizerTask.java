/*
 * Copyright 2006-2018 The MZmine 2 Development Team
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

package net.sf.mzmine.modules.visualization.ims;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.util.Arrays;
import java.util.logging.Logger;
import javax.swing.JFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.LookupPaintScale;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYBlockRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.PaintScaleLegend;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYZDataset;
import com.google.common.collect.Range;
import net.sf.mzmine.chartbasics.gui.swing.EChartPanel;
import net.sf.mzmine.datamodel.RawDataFile;
import net.sf.mzmine.datamodel.Scan;
import net.sf.mzmine.modules.visualization.kendrickmassplot.chartutils.XYBlockPixelSizePaintScales;
import net.sf.mzmine.parameters.ParameterSet;
import net.sf.mzmine.taskcontrol.AbstractTask;
import net.sf.mzmine.taskcontrol.TaskStatus;

/**
 * Task to create IMS visualization
 * 
 * @author Ansgar Korf (ansgar.korf@uni-muenster.de)
 */
public class ImsVisualizerTask extends AbstractTask {

  static final Font legendFont = new Font("SansSerif", Font.PLAIN, 12);
  static final Font titleFont = new Font("SansSerif", Font.PLAIN, 12);

  private Logger logger = Logger.getLogger(this.getClass().getName());

  private XYZDataset datasetIMS;
  private XYDataset datasetXIC;
  private JFreeChart chart;
  private RawDataFile dataFiles[];
  private Scan scans[];
  private Range<Double> mzRange;
  private ParameterSet parameterSet;
  private int totalSteps = 3, appliedSteps = 0;

  public ImsVisualizerTask(ParameterSet parameters) {

    dataFiles = parameters.getParameter(ImsVisualizerParameters.dataFiles).getValue()
        .getMatchingRawDataFiles();

    scans = parameters.getParameter(ImsVisualizerParameters.scanSelection).getValue()
        .getMatchingScans(dataFiles[0]);

    mzRange = parameters.getParameter(ImsVisualizerParameters.mzRange).getValue();

    parameterSet = parameters;
  }

  @Override
  public String getTaskDescription() {
    return "Create IMS visualization of " + dataFiles[0];
  }

  @Override
  public double getFinishedPercentage() {
    return totalSteps == 0 ? 0 : (double) appliedSteps / totalSteps;
  }

  @Override
  public void run() {
    setStatus(TaskStatus.PROCESSING);
    logger.info("IMS visualization of " + dataFiles[0]);
    // Task canceled?
    if (isCanceled())
      return;

    chart = createPlot();
    chart.setBackgroundPaint(Color.white);

    // Create IMS plot Window
    ImsVisualizerWindow frame = new ImsVisualizerWindow(chart);

    // create chart JPanel
    EChartPanel chartPanel = new EChartPanel(chart, true, true, true, true, false);
    frame.add(chartPanel, BorderLayout.CENTER);

    // set title properties
    TextTitle chartTitle = chart.getTitle();
    chartTitle.setMargin(5, 0, 0, 0);
    chartTitle.setFont(titleFont);
    LegendTitle legend = chart.getLegend();
    legend.setVisible(false);
    frame.setTitle("IMS of " + dataFiles[0] + "m/z Range " + mzRange);
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.setBackground(Color.white);
    frame.setVisible(true);
    frame.pack();

    setStatus(TaskStatus.FINISHED);
    logger.info("Finished IMS visualization of" + dataFiles[0]);
  }

  /**
   * IMS plot
   */
  private JFreeChart createPlot() {

    logger.info("Creating new IMS chart instance");
    appliedSteps++;


    // load dataseta for IMS and XIC
    datasetIMS = new ImsVisualizerXYZDataset(parameterSet);
    datasetXIC = new ImsVisualizerXYDataset(parameterSet);

    // copy and sort z-Values for min and max of the paint scale
    double[] copyZValues = new double[datasetIMS.getItemCount(0)];
    for (int i = 0; i < datasetIMS.getItemCount(0); i++) {
      copyZValues[i] = datasetIMS.getZValue(0, i);
    }
    Arrays.sort(copyZValues);
    // get index in accordance to percentile windows
    int minScaleIndex = 0;
    int maxScaleIndex = copyZValues.length - 1;
    double min = copyZValues[minScaleIndex];
    double max = copyZValues[maxScaleIndex];

    LookupPaintScale scale = null;
    scale = new LookupPaintScale(min, max, new Color(244, 66, 223));
    Paint[] contourColors =
        XYBlockPixelSizePaintScales.getPaintColors("percentile", Range.closed(min, max), "IMS");
    double[] scaleValues = new double[contourColors.length];
    double delta = (max - min) / (contourColors.length - 1);
    double value = min;

    // only show data if there is a drift time dimension
    if (datasetIMS.getItemCount(0) == datasetXIC.getItemCount(0)) {
      scale.add(min, Color.black);
      scale.add(max, Color.black);
    } else {
      for (int i = 0; i < contourColors.length; i++) {
        scale.add(value, contourColors[i]);
        scaleValues[i] = value;
        value = value + delta;
      }
    }

    // set axis
    NumberAxis domain = new NumberAxis("Retention time (min)");
    // parent plot
    CombinedDomainXYPlot plot = new CombinedDomainXYPlot(domain);
    plot.setGap(5.0);

    // copy and sort x-Values for min and max of the domain axis
    double[] copyXValues = new double[datasetXIC.getItemCount(0)];
    for (int i = 0; i < datasetXIC.getItemCount(0); i++) {
      copyXValues[i] = datasetXIC.getXValue(0, i);
    }
    // set renderer
    XYBlockRenderer rendererIMS = new XYBlockRenderer();
    // double retentionTimeWidthInSec = copyXValues[1] / 60 - copyXValues[0] / 60;
    double retentionTimeWidthInSec = copyXValues[1] - copyXValues[0];
    // rendererIMS.setBlockWidth(retentionTimeWidthInSec + retentionTimeWidthInSec * 0.3);
    rendererIMS.setBlockWidth(retentionTimeWidthInSec);
    rendererIMS.setBlockHeight(1);
    appliedSteps++;

    // Set paint scale
    rendererIMS.setPaintScale(scale);

    // copy and sort y-Values for min and max of the paint scale
    double[] copyYValues = new double[datasetIMS.getItemCount(0)];
    for (int i = 0; i < datasetIMS.getItemCount(0); i++) {
      copyYValues[i] = datasetIMS.getYValue(0, i);
    }

    NumberAxis rangeIMS = new NumberAxis("Drift Time (bins)");
    NumberAxis rangeXIC = new NumberAxis("Intensity");

    XYPlot subplotIMS = new XYPlot(datasetIMS, null, rangeIMS, rendererIMS);
    subplotIMS.setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
    domain.setRange(new org.jfree.data.Range(copyXValues[0], copyXValues[copyXValues.length - 1]));
    try {
      rangeIMS
          .setRange(new org.jfree.data.Range(copyYValues[0], copyYValues[copyYValues.length - 1]));
    } catch (Exception e) {
      rangeIMS.setRange(new org.jfree.data.Range(0, 1));
    }

    subplotIMS.setRenderer(rendererIMS);
    subplotIMS.setBackgroundPaint(Color.black);
    subplotIMS.setRangeGridlinePaint(Color.black);
    subplotIMS.setDomainGridlinePaint(Color.black);
    subplotIMS.setAxisOffset(new RectangleInsets(5, 5, 5, 5));
    subplotIMS.setOutlinePaint(Color.black);

    // Legend
    NumberAxis scaleAxis = new NumberAxis("Intensity");
    scaleAxis.setRange(min, max);
    scaleAxis.setAxisLinePaint(Color.white);
    scaleAxis.setTickMarkPaint(Color.white);
    PaintScaleLegend legend = new PaintScaleLegend(scale, scaleAxis);

    legend.setStripOutlineVisible(false);
    legend.setAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
    legend.setAxisOffset(5.0);
    legend.setMargin(new RectangleInsets(5, 5, 5, 5));
    legend.setFrame(new BlockBorder(Color.white));
    legend.setPadding(new RectangleInsets(10, 10, 10, 10));
    legend.setStripWidth(10);
    legend.setPosition(RectangleEdge.LEFT);
    legend.getAxis().setLabelFont(legendFont);
    legend.getAxis().setTickLabelFont(legendFont);


    final XYItemRenderer rendererXIC = new StandardXYItemRenderer();
    rendererXIC.setSeriesPaint(0, Color.black);
    final XYPlot subplotXIC = new XYPlot(datasetXIC, null, rangeXIC, rendererXIC);
    subplotXIC.setBackgroundPaint(Color.white);
    subplotXIC.setRangeGridlinePaint(Color.white);
    subplotXIC.setDomainGridlinePaint(Color.white);
    subplotXIC.setAxisOffset(new RectangleInsets(5, 5, 5, 5));
    subplotXIC.setOutlinePaint(Color.black);

    plot.add(subplotXIC);
    plot.add(subplotIMS);

    chart = new JFreeChart("IMS of " + dataFiles[0] + "m/z Range " + mzRange,
        JFreeChart.DEFAULT_TITLE_FONT, plot, true);

    chart.addSubtitle(legend);
    appliedSteps++;
    return chart;
  }

}
