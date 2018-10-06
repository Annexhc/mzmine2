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

import org.jfree.data.xy.AbstractXYZDataset;
import com.google.common.collect.Range;
import net.sf.mzmine.datamodel.DataPoint;
import net.sf.mzmine.datamodel.RawDataFile;
import net.sf.mzmine.datamodel.Scan;
import net.sf.mzmine.parameters.ParameterSet;

/**
 * XYZDataset for IMS plots
 * 
 * @author Ansgar Korf (ansgar.korf@uni-muenster.de)
 */
class ImsVisualizerXYZDataset extends AbstractXYZDataset {

  private static final long serialVersionUID = 1L;

  private RawDataFile dataFiles[];
  private Scan scans[];
  private Range<Double> mzRange;
  private double[] xValues;
  private int[] yValues;
  private double[] zValues;

  public ImsVisualizerXYZDataset(ParameterSet parameters) {

    dataFiles = parameters.getParameter(ImsVisualizerParameters.dataFiles).getValue()
        .getMatchingRawDataFiles();

    scans = parameters.getParameter(ImsVisualizerParameters.scanSelection).getValue()
        .getMatchingScans(dataFiles[0]);

    mzRange = parameters.getParameter(ImsVisualizerParameters.mzRange).getValue();

    // Calc xValues retention time
    xValues = new double[scans.length];
    for (int i = 0; i < scans.length; i++) {
      xValues[i] = scans[i].getRetentionTime();
    }

    // Calc yValues Drift time (bins)
    yValues = new int[scans.length];
    int numberOfBins = getNumberOfBins(scans);
    int binNumber = 1;
    for (int i = 0; i < scans.length; i++) {
      yValues[i] = binNumber;
      if (binNumber <= numberOfBins) {
        binNumber++;
      }
      if (binNumber > numberOfBins) {
        binNumber = 1;
      }
    }

    // Calc zValues
    zValues = new double[scans.length];
    for (int i = 0; i < scans.length; i++) {
      DataPoint dataPoints[] = scans[i].getDataPoints();
      for (int j = 0; j < dataPoints.length; j++) {
        if (mzRange.contains(dataPoints[j].getMZ())) {
          zValues[i] = zValues[i] + dataPoints[j].getIntensity();
        }
      }
    }
  }


  private int getNumberOfBins(Scan scans[]) {
    int numberOfBins = 0;
    double scanRetentionTime = scans[0].getRetentionTime();
    for (int i = 0; i < scans.length; i++) {
      if (scanRetentionTime == scans[i].getRetentionTime()) {
        numberOfBins++;
      } else {
        break;
      }
    }
    return numberOfBins;
  }


  @Override
  public int getItemCount(int series) {
    return scans.length;
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

  @Override
  public int getSeriesCount() {
    return 1;
  }

  public Comparable<?> getRowKey(int item) {
    return scans[item].toString();
  }

  @Override
  public Comparable getSeriesKey(int series) {
    return getRowKey(series);
  }

}
