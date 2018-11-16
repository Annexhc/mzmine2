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

import org.jfree.data.xy.AbstractXYZDataset;
import net.sf.mzmine.datamodel.IonizationType;
import net.sf.mzmine.datamodel.PeakList;
import net.sf.mzmine.datamodel.PeakListRow;
import net.sf.mzmine.parameters.ParameterSet;
import net.sf.mzmine.util.FormulaUtils;

/**
 * XYZDataset for mass remainder analysis
 * 
 * @author Ansgar Korf (ansgar.korf@uni-muenster.de)
 */
class MassRemainderAnalysisXYZDataset extends AbstractXYZDataset {

  private static final long serialVersionUID = 1L;

  private PeakListRow selectedRows[];
  private String xAxisMolecularUnit;
  private String yAxisMolecularUnit;
  private String zAxisMolecularUnit;
  private String xAxisValues;
  private String zAxisValues;
  private Object yAxisChargeSelection;
  private String massOfChargeCarrier;
  private boolean useMassOfChargeCarrier;
  private double[] xValues;
  private double[] yValues;
  private double[] zValues;
  private ParameterSet parameters;

  public MassRemainderAnalysisXYZDataset(ParameterSet parameters) {

    PeakList peakList = parameters.getParameter(MassRemainderAnalysisParameters.peakList).getValue()
        .getMatchingPeakLists()[0];

    this.parameters = parameters;

    this.selectedRows = parameters.getParameter(MassRemainderAnalysisParameters.selectedRows)
        .getMatchingRows(peakList);

    this.yAxisMolecularUnit =
        parameters.getParameter(MassRemainderAnalysisParameters.yAxisMolecularUnit).getValue();

    this.yAxisChargeSelection =
        parameters.getParameter(MassRemainderAnalysisParameters.yAxisCharge).getValue();

    this.massOfChargeCarrier =
        parameters.getParameter(MassRemainderAnalysisParameters.massOfChargeCarrier)
            .getEmbeddedParameter().getValue();

    this.useMassOfChargeCarrier =
        parameters.getParameter(MassRemainderAnalysisParameters.massOfChargeCarrier).getValue();

    this.xAxisValues =
        parameters.getParameter(MassRemainderAnalysisParameters.xAxisValues).getValue();

    if (parameters.getParameter(MassRemainderAnalysisParameters.xAxisCustomMolecularUnit)
        .getValue() == true) {
      this.xAxisMolecularUnit =
          parameters.getParameter(MassRemainderAnalysisParameters.xAxisCustomMolecularUnit)
              .getEmbeddedParameter().getValue();
    } else {
      this.xAxisMolecularUnit = null;
    }

    if (parameters.getParameter(MassRemainderAnalysisParameters.zAxisCustomMolecularUnit)
        .getValue() == true) {
      this.zAxisMolecularUnit =
          parameters.getParameter(MassRemainderAnalysisParameters.zAxisCustomMolecularUnit)
              .getEmbeddedParameter().getValue();
    } else {
      this.zAxisMolecularUnit = null;
    }

    this.zAxisValues =
        parameters.getParameter(MassRemainderAnalysisParameters.zAxisValues).getValue();

    // Calc xValues
    xValues = new double[selectedRows.length];
    if (parameters.getParameter(MassRemainderAnalysisParameters.xAxisCustomMolecularUnit)
        .getValue() == true) {
      for (int i = 0; i < selectedRows.length; i++) {
        // get charge
        int charge = 1;
        if (yAxisChargeSelection == "auto") {
          if (selectedRows[i].getRowCharge() != 0) {
            charge = selectedRows[i].getRowCharge();
          }
        } else {
          charge = Integer.parseInt(yAxisChargeSelection.toString());
        }
        // use charge carrier mass
        double exactMassOfChargeCarrier = 0;
        if (useMassOfChargeCarrier == true) {
          // get charge carrier mass
          exactMassOfChargeCarrier = FormulaUtils.calculateExactMass(massOfChargeCarrier);
        }
        xValues[i] = (selectedRows[i].getAverageMZ() - exactMassOfChargeCarrier) * charge - Math
            .ceil((selectedRows[i].getAverageMZ() - exactMassOfChargeCarrier) * charge
                / FormulaUtils.calculateExactMass(xAxisMolecularUnit))
            * FormulaUtils.calculateExactMass(xAxisMolecularUnit);
      }
    } else if (xAxisValues.contains("(m/z-")) {
      for (int i = 0; i < selectedRows.length; i++) {
        // get charge
        int charge = 1;
        if (yAxisChargeSelection == "auto") {
          if (selectedRows[i].getRowCharge() != 0) {
            charge = selectedRows[i].getRowCharge();
          }
        } else {
          charge = Integer.parseInt(yAxisChargeSelection.toString());
        }
        // use charge carrier mass
        double exactMassOfChargeCarrier = 0;
        if (useMassOfChargeCarrier == true) {
          // get charge carrier mass
          // get charge of charge carrier
          if (massOfChargeCarrier.contains("+")) {
            exactMassOfChargeCarrier = FormulaUtils.calculateExactMass(
                FormulaUtils.ionizeFormula(massOfChargeCarrier, IonizationType.POSITIVE, 1));
          } else if (massOfChargeCarrier.contains("-")) {
            exactMassOfChargeCarrier = FormulaUtils.calculateExactMass(
                FormulaUtils.ionizeFormula(massOfChargeCarrier, IonizationType.NEGATIVE, 1));
          } else {
            exactMassOfChargeCarrier = FormulaUtils.calculateExactMass(massOfChargeCarrier);
          }
        }
        xValues[i] = charge * (selectedRows[i].getAverageMZ() - exactMassOfChargeCarrier);
      }
    } else if (xAxisValues.contains("m/z*z")) {
      for (int i = 0; i < selectedRows.length; i++) {
        // get charge
        int charge = 1;
        if (yAxisChargeSelection == "auto") {
          if (selectedRows[i].getRowCharge() != 0) {
            charge = selectedRows[i].getRowCharge();
          }
        } else {
          charge = Integer.parseInt(yAxisChargeSelection.toString());
        }
        xValues[i] = charge * selectedRows[i].getAverageMZ();
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
      int charge = 1;
      if (yAxisChargeSelection == "auto") {
        if (selectedRows[i].getRowCharge() != 0) {
          charge = selectedRows[i].getRowCharge();
        }
      } else {
        charge = Integer.parseInt(yAxisChargeSelection.toString());
      }
      // use charge carrier mass
      double exactMassOfChargeCarrier = 0;
      if (useMassOfChargeCarrier == true) {
        // get charge carrier mass
        // get charge of charge carrier
        if (massOfChargeCarrier.contains("+")) {
          exactMassOfChargeCarrier = FormulaUtils.calculateExactMass(
              FormulaUtils.ionizeFormula(massOfChargeCarrier, IonizationType.POSITIVE, 1));
        } else if (massOfChargeCarrier.contains("-")) {
          exactMassOfChargeCarrier = FormulaUtils.calculateExactMass(
              FormulaUtils.ionizeFormula(massOfChargeCarrier, IonizationType.NEGATIVE, 1));
        } else {
          exactMassOfChargeCarrier = FormulaUtils.calculateExactMass(massOfChargeCarrier);
        }
      }
      yValues[i] = (selectedRows[i].getAverageMZ() - exactMassOfChargeCarrier) * charge - (Math
          .floor(((selectedRows[i].getAverageMZ() - exactMassOfChargeCarrier) * charge)
              / FormulaUtils.calculateExactMass(yAxisMolecularUnit))
          * FormulaUtils.calculateExactMass(yAxisMolecularUnit));
    }

    // Calc zValues
    zValues = new double[selectedRows.length];
    if (parameters.getParameter(MassRemainderAnalysisParameters.zAxisCustomMolecularUnit)
        .getValue() == true)

    {
      // Calc yValues
      zValues = new double[selectedRows.length];
      for (int i = 0; i < selectedRows.length; i++) {
        zValues[i] = zValues[i] = selectedRows[i].getAverageMZ() - Math.ceil(
            selectedRows[i].getAverageMZ() / FormulaUtils.calculateExactMass(zAxisMolecularUnit))
            * FormulaUtils.calculateExactMass(zAxisMolecularUnit);
      }
    } else
      for (int i = 0; i < selectedRows.length; i++) {
        // plot selected feature characteristic as z Axis
        if (zAxisValues.equals("Retention time")) {
          zValues[i] = selectedRows[i].getAverageRT();
        } else if (zAxisValues.equals("Intensity")) {
          zValues[i] = selectedRows[i].getAverageHeight();
        } else if (zAxisValues.equals("Area")) {
          zValues[i] = selectedRows[i].getAverageArea();
        } else if (zAxisValues.equals("Tailing factor")) {
          zValues[i] = selectedRows[i].getBestPeak().getTailingFactor();
        } else if (zAxisValues.equals("Asymmetry factor")) {
          zValues[i] = selectedRows[i].getBestPeak().getAsymmetryFactor();
        } else if (zAxisValues.equals("FWHM")) {
          zValues[i] = selectedRows[i].getBestPeak().getFWHM();
        } else if (zAxisValues.equals("m/z")) {
          zValues[i] = selectedRows[i].getBestPeak().getMZ();
        }
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
  public Number getZ(int series, int item) {
    return zValues[item];
  }


  public void setxValues(double[] values) {
    xValues = values;
  }

  public void setyValues(double[] values) {
    yValues = values;
  }

  public void setzValues(double[] values) {
    zValues = values;
  }


  @Override
  public int getSeriesCount() {
    return 1;
  }

  public Comparable<?> getRowKey(int row) {
    return selectedRows[row].toString();
  }

  @Override
  public Comparable getSeriesKey(int series) {
    return getRowKey(series);
  }

}
