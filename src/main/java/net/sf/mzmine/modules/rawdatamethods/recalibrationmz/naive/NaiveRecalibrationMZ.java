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

package net.sf.mzmine.modules.rawdatamethods.recalibrationmz.naive;

import javax.annotation.Nonnull;
import net.sf.mzmine.datamodel.DataPoint;
import net.sf.mzmine.datamodel.Scan;
import net.sf.mzmine.datamodel.impl.SimpleDataPoint;
import net.sf.mzmine.datamodel.impl.SimpleScan;
import net.sf.mzmine.modules.rawdatamethods.recalibrationmz.RecalibrationMZMethod;
import net.sf.mzmine.parameters.ParameterSet;

public class NaiveRecalibrationMZ implements RecalibrationMZMethod {

  // User parameters
  private double mzDiff;
  private String mzDiffType;

  public Scan getScan(Scan oldScan, ParameterSet parameters) {
    this.mzDiff = parameters.getParameter(NaiveRecalibrationMZParameters.mzDiff).getValue();
    this.mzDiffType = parameters.getParameter(NaiveRecalibrationMZParameters.mzDiffType).getValue();
    final SimpleScan newScan = new SimpleScan(oldScan);
    DataPoint[] oldDPs = oldScan.getDataPoints();
    DataPoint[] newDPs = new DataPoint[oldScan.getNumberOfDataPoints()];
    // Loop through every data point
    for (int j = 0; j < oldScan.getNumberOfDataPoints(); j++) {
      if (mzDiffType.equals("absolute")) {
        newDPs[j] = new SimpleDataPoint(oldDPs[j].getMZ() + mzDiff, oldDPs[j].getIntensity());
      }
      if (mzDiffType.equals("relative ppm")) {
        double newMZ = (mzDiff / 1000000) * oldDPs[j].getMZ() + oldDPs[j].getMZ();
        newDPs[j] = new SimpleDataPoint(newMZ, oldDPs[j].getIntensity());
      }
    }
    newScan.setDataPoints(newDPs);
    return newScan;
  }

  public @Nonnull String getName() {
    return "Naive";
  }

  @Override
  public @Nonnull Class<? extends ParameterSet> getParameterSetClass() {
    return NaiveRecalibrationMZParameters.class;
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
