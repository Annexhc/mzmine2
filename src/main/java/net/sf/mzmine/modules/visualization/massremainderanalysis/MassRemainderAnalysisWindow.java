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

package net.sf.mzmine.modules.visualization.massremainderanalysis;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import net.sf.mzmine.datamodel.PeakList;
import net.sf.mzmine.datamodel.PeakListRow;
import net.sf.mzmine.desktop.impl.WindowsMenu;
import net.sf.mzmine.modules.visualization.massremainderanalysis.chartutils.XYBlockPixelSizeRenderer;
import net.sf.mzmine.parameters.ParameterSet;
import net.sf.mzmine.util.FormulaUtils;

/**
 * Window for mass remainder analysis
 * 
 * @author Ansgar Korf (ansgar.korf@uni-muenster.de)
 */
public class MassRemainderAnalysisWindow extends JFrame implements ActionListener {

  private static final long serialVersionUID = 1L;
  private MassRemainderAnalysisToolBar toolBar;
  private JFreeChart chart;
  private PeakListRow selectedRows[];
  private String xAxisMolecularUnit;
  private String yAxisMolecularUnit;
  private String zAxisMolecularUnit;
  private boolean useCustomeXAxisMolecularUnit;
  private boolean useCustomeZAxisMolecularUnit;
  private int yAxisCharge;
  private int xAxisCharge;
  private int zAxisCharge;
  private int yAxisDivisor;
  private int xAxisDivisor;
  private int zAxisDivisor;

  public MassRemainderAnalysisWindow(JFreeChart chart, ParameterSet parameters) {

    PeakList peakList = parameters.getParameter(MassRemainderAnalysisParameters.peakList).getValue()
        .getMatchingPeakLists()[0];

    this.selectedRows = parameters.getParameter(MassRemainderAnalysisParameters.selectedRows)
        .getMatchingRows(peakList);

    this.yAxisMolecularUnit =
        parameters.getParameter(MassRemainderAnalysisParameters.yAxisMolecularUnit).getValue();

    this.useCustomeXAxisMolecularUnit = parameters
        .getParameter(MassRemainderAnalysisParameters.xAxisCustomMolecularUnit).getValue();

    if (useCustomeXAxisMolecularUnit == true) {
      this.xAxisMolecularUnit =
          parameters.getParameter(MassRemainderAnalysisParameters.xAxisCustomMolecularUnit)
              .getEmbeddedParameter().getValue();
    } else {
      this.xAxisMolecularUnit = null;
    }

    this.useCustomeZAxisMolecularUnit = parameters
        .getParameter(MassRemainderAnalysisParameters.zAxisCustomMolecularUnit).getValue();

    if (useCustomeZAxisMolecularUnit == true) {
      this.zAxisMolecularUnit =
          parameters.getParameter(MassRemainderAnalysisParameters.zAxisCustomMolecularUnit)
              .getEmbeddedParameter().getValue();
    } else {
      this.zAxisMolecularUnit =
          parameters.getParameter(MassRemainderAnalysisParameters.zAxisValues).getValue();
    }

    this.chart = chart;

    this.yAxisCharge = 1;

    this.xAxisCharge = 1;

    this.zAxisCharge = 1;

    this.yAxisDivisor = 1;

    this.xAxisDivisor = 1;

    this.zAxisDivisor = 1;

    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setBackground(Color.white);

    // Add toolbar
    toolBar =
        new MassRemainderAnalysisToolBar(this, xAxisCharge, yAxisCharge, zAxisCharge, xAxisDivisor,
            yAxisDivisor, zAxisDivisor, useCustomeXAxisMolecularUnit, useCustomeZAxisMolecularUnit);
    add(toolBar, BorderLayout.EAST);

    // Add the Windows menu
    JMenuBar menuBar = new JMenuBar();
    menuBar.add(new WindowsMenu());
    setJMenuBar(menuBar);

    pack();
  }

  @Override
  public void actionPerformed(ActionEvent event) {

    String command = event.getActionCommand();

    if (command.equals("TOGGLE_BLOCK_SIZE")) {

      XYPlot plot = chart.getXYPlot();
      XYBlockPixelSizeRenderer renderer = (XYBlockPixelSizeRenderer) plot.getRenderer();
      int height = (int) renderer.getBlockHeightPixel();

      if (height == 1) {
        height++;
      } else if (height == 5) {
        height = 1;
      } else if (height < 5 && height != 1) {
        height++;
      }
      renderer.setBlockHeightPixel(height);
      renderer.setBlockWidthPixel(height);

    }

    if (command.equals("TOGGLE_BACK_COLOR")) {

      XYPlot plot = chart.getXYPlot();
      if (plot.getBackgroundPaint() == Color.WHITE) {
        plot.setBackgroundPaint(Color.BLACK);
      } else {
        plot.setBackgroundPaint(Color.WHITE);
      }

    }

    if (command.equals("TOGGLE_GRID")) {

      XYPlot plot = chart.getXYPlot();
      if (plot.getDomainGridlinePaint() == Color.BLACK) {
        plot.setDomainGridlinePaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.WHITE);
      } else {
        plot.setDomainGridlinePaint(Color.BLACK);
        plot.setRangeGridlinePaint(Color.BLACK);
      }

    }

    if (command.equals("TOGGLE_ANNOTATIONS")) {

      XYPlot plot = chart.getXYPlot();
      XYBlockPixelSizeRenderer renderer = (XYBlockPixelSizeRenderer) plot.getRenderer();
      Boolean itemNameVisible = renderer.getDefaultItemLabelsVisible();
      if (itemNameVisible == false) {
        renderer.setDefaultItemLabelsVisible(true);
      } else {
        renderer.setDefaultItemLabelsVisible(false);
      }
      if (plot.getBackgroundPaint() == Color.BLACK) {
        renderer.setDefaultItemLabelPaint(Color.WHITE);
      } else {
        renderer.setDefaultItemLabelPaint(Color.BLACK);
      }
    }

    // y axis commands

    if (command.equals("CHANGE_CHARGE_UP_Y")) {
      yAxisCharge = yAxisCharge + 1;
      XYPlot plot = chart.getXYPlot();
      chargeOrDivisorChanged(plot);
    }

    if (command.equals("CHANGE_CHARGE_DOWN_Y")) {
      if (yAxisCharge > 1) {
        yAxisCharge = yAxisCharge - 1;
      } else
        yAxisCharge = 1;
      XYPlot plot = chart.getXYPlot();
      chargeOrDivisorChanged(plot);
    }

    if (command.equals("CHANGE_DIVISOR_UP_Y")) {
      int minDivisor = getMinimumRecommendedDivisor(yAxisMolecularUnit);
      int maxDivisor = getMaximumRecommendedDivisor(yAxisMolecularUnit);
      if (yAxisDivisor == 1) {
        yAxisDivisor = minDivisor;
      } else if (yAxisDivisor >= minDivisor && yAxisDivisor < maxDivisor) {
        yAxisDivisor++;
      }
      XYPlot plot = chart.getXYPlot();
      chargeOrDivisorChanged(plot);
    }

    if (command.equals("CHANGE_DIVISOR_DOWN_Y")) {
      int minDivisor = getMinimumRecommendedDivisor(yAxisMolecularUnit);
      int maxDivisor = getMaximumRecommendedDivisor(yAxisMolecularUnit);
      if (yAxisDivisor > minDivisor && yAxisDivisor <= maxDivisor) {
        yAxisDivisor = yAxisDivisor - 1;
      } else if (yAxisDivisor == minDivisor) {
        yAxisDivisor = 1;
      }
      XYPlot plot = chart.getXYPlot();
      chargeOrDivisorChanged(plot);
    }

    // x axis commands
    if (command.equals("CHANGE_CHARGE_UP_X")) {
      xAxisCharge = xAxisCharge + 1;
      XYPlot plot = chart.getXYPlot();
      chargeOrDivisorChanged(plot);
    }

    if (command.equals("CHANGE_CHARGE_DOWN_X")) {
      if (xAxisCharge > 1) {
        xAxisCharge = xAxisCharge - 1;
      } else
        xAxisCharge = 1;
      XYPlot plot = chart.getXYPlot();
      chargeOrDivisorChanged(plot);
    }

    if (command.equals("CHANGE_DIVISOR_UP_X")) {
      int minDivisor = getMinimumRecommendedDivisor(xAxisMolecularUnit);
      int maxDivisor = getMaximumRecommendedDivisor(xAxisMolecularUnit);
      if (xAxisDivisor == 1) {
        xAxisDivisor = minDivisor;
      } else if (xAxisDivisor >= minDivisor && xAxisDivisor < maxDivisor) {
        xAxisDivisor++;
      }
      XYPlot plot = chart.getXYPlot();
      chargeOrDivisorChanged(plot);
    }

    if (command.equals("CHANGE_DIVISOR_DOWN_X")) {
      int minDivisor = getMinimumRecommendedDivisor(xAxisMolecularUnit);
      int maxDivisor = getMaximumRecommendedDivisor(xAxisMolecularUnit);
      if (xAxisDivisor > minDivisor && xAxisDivisor <= maxDivisor) {
        xAxisDivisor = xAxisDivisor - 1;
      } else if (xAxisDivisor == minDivisor) {
        xAxisDivisor = 1;
      }
      XYPlot plot = chart.getXYPlot();
      chargeOrDivisorChanged(plot);
    }

    // z axis commands
    if (command.equals("CHANGE_CHARGE_UP_Z")) {
      zAxisCharge = zAxisCharge + 1;
      XYPlot plot = chart.getXYPlot();
      chargeOrDivisorChanged(plot);
    }

    if (command.equals("CHANGE_CHARGE_DOWN_Z")) {
      if (zAxisCharge > 1) {
        zAxisCharge = zAxisCharge - 1;
      } else
        zAxisCharge = 1;
      XYPlot plot = chart.getXYPlot();
      chargeOrDivisorChanged(plot);
    }

    if (command.equals("CHANGE_DIVISOR_UP_Z")) {
      int minDivisor = getMinimumRecommendedDivisor(zAxisMolecularUnit);
      int maxDivisor = getMaximumRecommendedDivisor(zAxisMolecularUnit);
      if (zAxisDivisor == 1) {
        zAxisDivisor = minDivisor;
      } else if (zAxisDivisor >= minDivisor && zAxisDivisor < maxDivisor) {
        zAxisDivisor++;
      }
      XYPlot plot = chart.getXYPlot();
      chargeOrDivisorChanged(plot);
    }

    if (command.equals("CHANGE_DIVISOR_DOWN_Z")) {
      int minDivisor = getMinimumRecommendedDivisor(zAxisMolecularUnit);
      int maxDivisor = getMaximumRecommendedDivisor(zAxisMolecularUnit);
      if (zAxisDivisor > minDivisor && zAxisDivisor <= maxDivisor) {
        zAxisDivisor = zAxisDivisor - 1;
      } else if (zAxisDivisor == minDivisor) {
        zAxisDivisor = 1;
      }
      XYPlot plot = chart.getXYPlot();
      chargeOrDivisorChanged(plot);
    }
  }

  private void chargeOrDivisorChanged(XYPlot plot) {

    if (plot.getDataset() instanceof MassRemainderAnalysisXYDataset) {
      MassRemainderAnalysisXYDataset dataset = (MassRemainderAnalysisXYDataset) plot.getDataset();
      double[] xValues = new double[dataset.getItemCount(0)];
      // Calc xValues
      xValues = new double[selectedRows.length];
      if (useCustomeXAxisMolecularUnit == true) {
        for (int i = 0; i < selectedRows.length; i++) {
          // get charge
          int charge = xAxisCharge;
          xValues[i] = selectedRows[i].getAverageMZ() * charge * xAxisDivisor - (Math
              .floor((selectedRows[i].getAverageMZ() * charge * xAxisDivisor)
                  / FormulaUtils.calculateExactMass(xAxisMolecularUnit))
              * FormulaUtils.calculateExactMass(xAxisMolecularUnit));
        }
      } else {
        for (int i = 0; i < selectedRows.length; i++) {
          xValues[i] = selectedRows[i].getAverageMZ();
        }
      }
      // Calc yValues
      double[] yValues = new double[dataset.getItemCount(0)];
      yValues = new double[selectedRows.length];
      for (int i = 0; i < selectedRows.length; i++) {
        // get charge
        int charge = yAxisCharge;
        yValues[i] = selectedRows[i].getAverageMZ() * charge * yAxisDivisor - (Math
            .floor((selectedRows[i].getAverageMZ() * charge * yAxisDivisor)
                / FormulaUtils.calculateExactMass(yAxisMolecularUnit))
            * FormulaUtils.calculateExactMass(yAxisMolecularUnit));
      }
      dataset.setyValues(yValues);
      dataset.setxValues(xValues);
      chart.fireChartChanged();
      validate();
    } else if (plot.getDataset() instanceof MassRemainderAnalysisXYZDataset) {
      MassRemainderAnalysisXYZDataset dataset = (MassRemainderAnalysisXYZDataset) plot.getDataset();
      double[] xValues = new double[dataset.getItemCount(0)];

      // Calc xValues
      xValues = new double[selectedRows.length];
      if (useCustomeXAxisMolecularUnit == true) {
        for (int i = 0; i < selectedRows.length; i++) {
          // get charge
          int charge = xAxisCharge;
          xValues[i] = selectedRows[i].getAverageMZ() * charge * xAxisDivisor - (Math
              .floor((selectedRows[i].getAverageMZ() * charge * xAxisDivisor)
                  / FormulaUtils.calculateExactMass(xAxisMolecularUnit))
              * FormulaUtils.calculateExactMass(xAxisMolecularUnit));
        }
      } else {
        for (int i = 0; i < selectedRows.length; i++) {
          xValues[i] = selectedRows[i].getAverageMZ();
        }
      }

      // Calc yValues
      double[] yValues = new double[selectedRows.length];
      for (int i = 0; i < selectedRows.length; i++) {
        // get charge
        int charge = yAxisCharge;
        yValues[i] = selectedRows[i].getAverageMZ() * charge * yAxisDivisor - (Math
            .floor((selectedRows[i].getAverageMZ() * charge * yAxisDivisor)
                / FormulaUtils.calculateExactMass(yAxisMolecularUnit))
            * FormulaUtils.calculateExactMass(yAxisMolecularUnit));
      }

      // Calc zValues
      double[] zValues = new double[selectedRows.length];
      if (useCustomeZAxisMolecularUnit == true) {
        for (int i = 0; i < selectedRows.length; i++) {
          // get charge
          int charge = zAxisCharge;
          zValues[i] = selectedRows[i].getAverageMZ() * charge * zAxisDivisor - (Math
              .floor((selectedRows[i].getAverageMZ() * charge * zAxisDivisor)
                  / FormulaUtils.calculateExactMass(zAxisMolecularUnit))
              * FormulaUtils.calculateExactMass(zAxisMolecularUnit));
        }
      } else
        for (int i = 0; i < selectedRows.length; i++) {
          // plot selected feature characteristic as z Axis
          if (zAxisMolecularUnit.equals("Retention time")) {
            zValues[i] = selectedRows[i].getAverageRT();
          } else if (zAxisMolecularUnit.equals("Intensity")) {
            zValues[i] = selectedRows[i].getAverageHeight();
          } else if (zAxisMolecularUnit.equals("Area")) {
            zValues[i] = selectedRows[i].getAverageArea();
          } else if (zAxisMolecularUnit.equals("Tailing factor")) {
            zValues[i] = selectedRows[i].getBestPeak().getTailingFactor();
          } else if (zAxisMolecularUnit.equals("Asymmetry factor")) {
            zValues[i] = selectedRows[i].getBestPeak().getAsymmetryFactor();
          } else if (zAxisMolecularUnit.equals("FWHM")) {
            zValues[i] = selectedRows[i].getBestPeak().getFWHM();
          } else if (zAxisMolecularUnit.equals("m/z")) {
            zValues[i] = selectedRows[i].getBestPeak().getMZ();
          }
        }
      dataset.setyValues(yValues);
      dataset.setxValues(xValues);
      dataset.setzValues(zValues);
      chart.fireChartChanged();
      validate();
    }

    // update toolbar
    this.remove(toolBar);
    toolBar =
        new MassRemainderAnalysisToolBar(this, xAxisCharge, yAxisCharge, zAxisCharge, xAxisDivisor,
            yAxisDivisor, zAxisDivisor, useCustomeXAxisMolecularUnit, useCustomeZAxisMolecularUnit);
    this.add(toolBar, BorderLayout.EAST);
    this.revalidate();
  }

  /*
   * Method to calculate the recommended minimum of a divisor for Kendrick mass defect analysis
   */
  private int getMinimumRecommendedDivisor(String formula) {
    double exactMass = FormulaUtils.calculateExactMass(formula);
    return (int) Math.round((2.0 / 3.0) * exactMass);
  }

  /*
   * Method to calculate the recommended maximum of a divisor for Kendrick mass defect analysis
   */
  private int getMaximumRecommendedDivisor(String formula) {
    double exactMass = FormulaUtils.calculateExactMass(formula);
    return (int) Math.round(2.0 * exactMass);
  }

}
