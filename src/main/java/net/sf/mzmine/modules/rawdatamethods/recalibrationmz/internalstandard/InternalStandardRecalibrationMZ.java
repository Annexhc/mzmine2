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

package net.sf.mzmine.modules.rawdatamethods.recalibrationmz.internalstandard;

import javax.annotation.Nonnull;
import net.sf.mzmine.datamodel.DataPoint;
import net.sf.mzmine.datamodel.Scan;
import net.sf.mzmine.datamodel.impl.SimpleDataPoint;
import net.sf.mzmine.datamodel.impl.SimpleScan;
import net.sf.mzmine.modules.rawdatamethods.peakpicking.massdetection.exactmass.ExactMassDetector;
import net.sf.mzmine.modules.rawdatamethods.peakpicking.massdetection.exactmass.ExactMassDetectorParameters;
import net.sf.mzmine.modules.rawdatamethods.recalibrationmz.RecalibrationMZMethod;
import net.sf.mzmine.modules.rawdatamethods.recalibrationmz.lockmass.LockMass;
import net.sf.mzmine.modules.rawdatamethods.recalibrationmz.lockmass.LockMassRecalibrationMZParameters;
import net.sf.mzmine.parameters.ParameterSet;
import net.sf.mzmine.parameters.parametertypes.tolerances.MZTolerance;

public class InternalStandardRecalibrationMZ implements RecalibrationMZMethod {

  // User parameters
  private LockMass[] lockMass;
  private MZTolerance mzTolerance;
  private double noiseLevel;

  public Scan getScan(Scan oldScan, ParameterSet parameters) {
    this.lockMass =
        parameters.getParameter(LockMassRecalibrationMZParameters.lockMass).getChoices();
    this.mzTolerance =
        parameters.getParameter(LockMassRecalibrationMZParameters.mzTolerance).getValue();
    this.noiseLevel =
        parameters.getParameter(LockMassRecalibrationMZParameters.noiseLevel).getValue();
    final SimpleScan newScan = new SimpleScan(oldScan);
    DataPoint[] oldDPs = oldScan.getDataPoints();
    DataPoint[] newDPs = new DataPoint[oldScan.getNumberOfDataPoints()];
    double accurateLockMass = getAccurateLockMass(oldScan);
    // Loop through every data point
    for (int j = 0; j < oldScan.getNumberOfDataPoints(); j++) {
      double mzDiff = 0;
      // search for lockmass in ppm window
      mzDiff = lockMass[0].getLockMass() - accurateLockMass;
      newDPs[j] = new SimpleDataPoint(oldDPs[j].getMZ() + mzDiff, oldDPs[j].getIntensity());
    }
    newScan.setDataPoints(newDPs);
    return newScan;
  }

  private double getAccurateLockMass(Scan oldScan) {
    double accurateLockMass = 0;
    ExactMassDetector exactMassDetector = new ExactMassDetector();
    ExactMassDetectorParameters exactMassDetectorParameters = new ExactMassDetectorParameters();
    exactMassDetectorParameters.noiseLevel.setValue(noiseLevel);
    DataPoint[] massList = exactMassDetector.getMassValues(oldScan, exactMassDetectorParameters);

    // search for lockmass in m/z range
    for (int i = 0; i < massList.length; i++) {
      if (mzTolerance.checkWithinTolerance(massList[i].getMZ(), lockMass[0].getLockMass())) {
        accurateLockMass = massList[i].getMZ();
        break;
      }
    }
    return accurateLockMass;
  }

  public @Nonnull String getName() {
    return "Internal standard";
  }

  @Override
  public @Nonnull Class<? extends ParameterSet> getParameterSetClass() {
    return InternalStandardRecalibrationMZParameters.class;
  }

  @Override
  public double[][] getTrendlineXYValues() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public double[][] getMassXYValues() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public double[][] getXYValuesDeviationPerScan() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public double[][] getXYValuesDeviationPerScanTrendline() {
    // TODO Auto-generated method stub
    return null;
  }

}
