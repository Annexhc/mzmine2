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

package net.sf.mzmine.modules.rawdatamethods.recalibrationmz.lockmass;

import java.util.ArrayList;
import javax.annotation.Nonnull;
import net.sf.mzmine.datamodel.DataPoint;
import net.sf.mzmine.datamodel.Scan;
import net.sf.mzmine.datamodel.impl.SimpleDataPoint;
import net.sf.mzmine.datamodel.impl.SimpleScan;
import net.sf.mzmine.modules.rawdatamethods.peakpicking.massdetection.exactmass.ExactMassDetector;
import net.sf.mzmine.modules.rawdatamethods.peakpicking.massdetection.exactmass.ExactMassDetectorParameters;
import net.sf.mzmine.modules.rawdatamethods.recalibrationmz.RecalibrationMZMethod;
import net.sf.mzmine.modules.rawdatamethods.recalibrationmz.lockmass.regression.PolyTrendLine;
import net.sf.mzmine.modules.rawdatamethods.recalibrationmz.lockmass.regression.TrendLine;
import net.sf.mzmine.parameters.ParameterSet;
import net.sf.mzmine.parameters.parametertypes.tolerances.MZTolerance;

public class LockMassRecalibrationMZ implements RecalibrationMZMethod {

  // User parameters
  private LockMass[] lockMasses;
  private MZTolerance mzTolerance;
  private double noiseLevel;
  private double[][] xyValues;
  private double[][] xyValuesTrendline;
  private double[][] xyValuesDeviationPerScan;
  private double[][] xyValuesDeviationPerScanTrendline;

  public Scan getScan(Scan oldScan, ParameterSet parameters) {
    this.lockMasses =
        parameters.getParameter(LockMassRecalibrationMZParameters.lockMass).getChoices();
    this.mzTolerance =
        parameters.getParameter(LockMassRecalibrationMZParameters.mzTolerance).getValue();
    this.noiseLevel =
        parameters.getParameter(LockMassRecalibrationMZParameters.noiseLevel).getValue();

    // do regression all m/z vs deviation
    ArrayList<Double> accurateLockMasses = getAccurateLockMasses(oldScan);
    TrendLine trendline = new PolyTrendLine(1);
    int lowestMZ = (int) Math.round((oldScan.getDataPointMZRange().lowerEndpoint()));
    int highestMZ = (int) Math.round((oldScan.getDataPointMZRange().upperEndpoint()));
    xyValues = new double[2][lockMasses.length];
    xyValuesTrendline = new double[2][highestMZ - lowestMZ];
    for (int i = 0; i < accurateLockMasses.size(); i++) {
      // accurate lock masses as x Values
      xyValues[0][i] = accurateLockMasses.get(i);
      // calc deviation for y Values
      xyValues[1][i] =
          ((accurateLockMasses.get(i) - lockMasses[i].getLockMass()) / lockMasses[i].getLockMass())
              * 1000000;
    }
    trendline.setValues(xyValues[1], xyValues[0]);
    // calc values for regression
    for (int i = 0; i < xyValuesTrendline[1].length; i++) {
      // nominal masses as x Values
      xyValuesTrendline[0][i] = i + lowestMZ;
      // predict y Values
      xyValuesTrendline[1][i] = trendline.predict(i + lowestMZ);
    }

    // do regression number of scan vs deviation
    TrendLine trendlineDeviationPerScan = new PolyTrendLine(1);
    xyValuesDeviationPerScan = new double[2][accurateLockMasses.size()];
    xyValuesDeviationPerScanTrendline = new double[2][accurateLockMasses.size()];
    for (int i = 0; i < accurateLockMasses.size(); i++) {
      // accurate lock masses as x Values
      xyValuesDeviationPerScan[0][i] = oldScan.getScanNumber();
      // calc deviation for y Values
      xyValuesDeviationPerScan[1][i] =
          ((accurateLockMasses.get(i) - lockMasses[i].getLockMass()) / lockMasses[i].getLockMass())
              * 1000000;
    }
    // trendlineDeviationPerScan.setValues(xyValuesDeviationPerScan[1],
    // xyValuesDeviationPerScan[0]);

    // calc values for regression
    // for (int i = 0; i < xyValuesDeviationPerScanTrendline[1].length; i++) {
    // // nominal masses as x Values
    // xyValuesDeviationPerScanTrendline[0][i] = oldScan.getScanNumber();
    // // predict y Values
    // xyValuesDeviationPerScanTrendline[1][i] = trendlineDeviationPerScan.predict(i);
    // }


    final SimpleScan newScan = new SimpleScan(oldScan);
    DataPoint[] oldDPs = oldScan.getDataPoints();
    DataPoint[] newDPs = new DataPoint[oldScan.getNumberOfDataPoints()];

    // Loop through every data point
    for (int j = 0; j < oldScan.getNumberOfDataPoints(); j++) {
      for (int n = 0; n < accurateLockMasses.size(); n++) {
        double mzDiff = 0;
        double mzDiffRelative = 0;
        // search for lockmass in ppm window
        mzDiff = lockMasses[n].getLockMass() - accurateLockMasses.get(n);
        mzDiffRelative = (mzDiff / lockMasses[0].getLockMass()) * 1000000;
        newDPs[j] =
            new SimpleDataPoint((mzDiffRelative / 1000000) * oldDPs[j].getMZ() + oldDPs[j].getMZ(),
                oldDPs[j].getIntensity());
      }
    }
    newScan.setDataPoints(newDPs);
    return newScan;
  }

  private ArrayList<Double> getAccurateLockMasses(Scan oldScan) {
    ArrayList<Double> accurateLockMass = new ArrayList<Double>();
    ExactMassDetector exactMassDetector = new ExactMassDetector();
    ExactMassDetectorParameters exactMassDetectorParameters = new ExactMassDetectorParameters();
    exactMassDetectorParameters.noiseLevel.setValue(noiseLevel);
    DataPoint[] massList = exactMassDetector.getMassValues(oldScan, exactMassDetectorParameters);
    // search for lockmass in m/z range
    for (int j = 0; j < lockMasses.length; j++) {
      for (int i = 0; i < massList.length; i++) {
        if (mzTolerance.checkWithinTolerance(massList[i].getMZ(), lockMasses[j].getLockMass())) {
          accurateLockMass.add(massList[i].getMZ());
          break;
        }
      }
    }
    return accurateLockMass;
  }


  public double[][] getTrendlineXYValues() {
    return xyValuesTrendline;
  }

  public double[][] getMassXYValues() {
    return xyValues;
  }

  public double[][] getXYValuesDeviationPerScan() {
    return xyValuesDeviationPerScan;
  }

  public double[][] getXYValuesDeviationPerScanTrendline() {
    return xyValuesDeviationPerScanTrendline;
  }

  public @Nonnull String getName() {
    return "Lock mass";
  }

  @Override
  public @Nonnull Class<? extends ParameterSet> getParameterSetClass() {
    return LockMassRecalibrationMZParameters.class;
  }

}
