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

import org.jfree.data.xy.AbstractXYDataset;
import net.sf.mzmine.datamodel.PeakList;
import net.sf.mzmine.datamodel.PeakListRow;
import net.sf.mzmine.parameters.ParameterSet;
import net.sf.mzmine.util.FormulaUtils;

/**
 * XYDataset for mass remainder analysis
 * 
 * @author Ansgar Korf (ansgar.korf@uni-muenster.de)
 */
class MassRemainderAnalysisXYDataset extends AbstractXYDataset {

  private static final long serialVersionUID = 1L;

  private PeakListRow selectedRows[];
  private String xAxisMolecularUnit;
  private String yAxisMolecularUnit;
  private int yAxisChargeSelection;
  private double[] xValues;
  private double[] yValues;
  private ParameterSet parameters;

  public MassRemainderAnalysisXYDataset(ParameterSet parameters) {

    PeakList peakList = parameters.getParameter(MassRemainderAnalysisParameters.peakList).getValue()
        .getMatchingPeakLists()[0];

    this.parameters = parameters;

    this.selectedRows = parameters.getParameter(MassRemainderAnalysisParameters.selectedRows)
        .getMatchingRows(peakList);

    this.yAxisMolecularUnit =
        parameters.getParameter(MassRemainderAnalysisParameters.yAxisMolecularUnit).getValue();

    this.yAxisChargeSelection =
        parameters.getParameter(MassRemainderAnalysisParameters.yAxisCharge).getValue();
    if (parameters.getParameter(MassRemainderAnalysisParameters.xAxisCustomMolecularUnit)
        .getValue() == true) {
      this.xAxisMolecularUnit =
          parameters.getParameter(MassRemainderAnalysisParameters.xAxisCustomMolecularUnit)
              .getEmbeddedParameter().getValue();
    } else {
      this.xAxisMolecularUnit = null;
    }

    // Calc xValues
    xValues = new double[selectedRows.length];
    if (parameters.getParameter(MassRemainderAnalysisParameters.xAxisCustomMolecularUnit)
        .getValue() == true) {
      for (int i = 0; i < selectedRows.length; i++) {
        // get charge
        int charge = yAxisChargeSelection;
        xValues[i] = selectedRows[i].getAverageMZ() * charge - (Math
            .floor((selectedRows[i].getAverageMZ() * charge)
                / FormulaUtils.calculateExactMass(xAxisMolecularUnit))
            * FormulaUtils.calculateExactMass(xAxisMolecularUnit));
      }
    } else {
      for (int i = 0; i < selectedRows.length; i++) {
        xValues[i] = selectedRows[i].getAverageMZ();
      }
    }


    // Calc yValues
    yValues = new double[selectedRows.length];
    for (int i = 0; i < selectedRows.length; i++) {
      // get charge
      int charge = yAxisChargeSelection;
      yValues[i] = selectedRows[i].getAverageMZ() * charge - (Math
          .floor((selectedRows[i].getAverageMZ() * charge)
              / FormulaUtils.calculateExactMass(yAxisMolecularUnit))
          * FormulaUtils.calculateExactMass(yAxisMolecularUnit));
    }
  }

  public ParameterSet getParameters() {
    return parameters;
  }

  public void setParameters(ParameterSet parameters) {
    this.parameters = parameters;
  }

  @Override
  public int getItemCount(int series) {
    return selectedRows.length;
  }

  @Override
  public Number getX(int series, int item) {
    return xValues[item];
  }

  @Override
  public Number getY(int series, int item) {
    return yValues[item];
  }

  @Override
  public int getSeriesCount() {
    return 1;
  }

  public Comparable<?> getRowKey(int row) {
    return selectedRows[row].toString();
  }

  @Override
  public Comparable<?> getSeriesKey(int series) {
    return getRowKey(series);
  }

  public double[] getxValues() {
    return xValues;
  }

  public double[] getyValues() {
    return yValues;
  }

  public void setxValues(double[] values) {
    xValues = values;
  }

  public void setyValues(double[] values) {
    yValues = values;
  }

}
