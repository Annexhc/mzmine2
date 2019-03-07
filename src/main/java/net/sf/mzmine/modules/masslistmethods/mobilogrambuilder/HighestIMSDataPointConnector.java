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

package net.sf.mzmine.modules.masslistmethods.mobilogrambuilder;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import com.google.common.collect.Range;
import net.sf.mzmine.datamodel.IMSDataPoint;
import net.sf.mzmine.datamodel.RawDataFile;
import net.sf.mzmine.parameters.parametertypes.tolerances.MZTolerance;
import net.sf.mzmine.util.IMSDataPointSorter;
import net.sf.mzmine.util.SortingDirection;
import net.sf.mzmine.util.SortingProperty;

public class HighestIMSDataPointConnector {

  private final MZTolerance mzTolerance;
  private final double minimumTimeSpan, minimumHeight;
  private final RawDataFile dataFile;
  private final int allScanNumbers[];

  // Mapping of last data point m/z --> mobilogram
  private Set<Mobilogram> buildingMobilograms;

  public HighestIMSDataPointConnector(RawDataFile dataFile, int allScanNumbers[],
      double minimumTimeSpan, double minimumHeight, MZTolerance mzTolerance) {

    this.mzTolerance = mzTolerance;
    this.minimumHeight = minimumHeight;
    this.minimumTimeSpan = minimumTimeSpan;
    this.dataFile = dataFile;
    this.allScanNumbers = allScanNumbers;

    // We use LinkedHashSet to maintain a reproducible ordering. If we use
    // plain HashSet, the resulting peak list row IDs will have different
    // order every time the method is invoked.
    buildingMobilograms = new LinkedHashSet<Mobilogram>();

  }

  public void addScan(int scanNumber, IMSDataPoint mzValues[]) {

    // Sort m/z peaks by descending intensity
    Arrays.sort(mzValues,
        new IMSDataPointSorter(SortingProperty.Intensity, SortingDirection.Descending));

    // Set of already connected mobilograms in each iteration
    Set<Mobilogram> connectedmobilograms = new LinkedHashSet<Mobilogram>();

    // TODO: these two nested cycles should be optimized for speed
    for (IMSDataPoint mzMobilogram : mzValues) {

      // Search for best mobilogram, which has highest last data point
      Mobilogram bestMobilogram = null;

      for (Mobilogram testMobilogram : buildingMobilograms) {

        IMSDataPoint lastMzMobilogram = testMobilogram.getLastMzMobilogram();
        Range<Double> toleranceRange = mzTolerance.getToleranceRange(lastMzMobilogram.getMZ());
        if (toleranceRange.contains(mzMobilogram.getMZ())) {
          if ((bestMobilogram == null) || (testMobilogram.getLastMzMobilogram()
              .getIntensity() > bestMobilogram.getLastMzMobilogram().getIntensity())) {
            bestMobilogram = testMobilogram;
          }
        }

      }

      // If we found best mobilogram, check if it is already connected.
      // In such case, we may discard this mass and continue. If we
      // haven't found a mobilogram, we may create a new one.
      if (bestMobilogram != null) {
        if (connectedmobilograms.contains(bestMobilogram)) {
          continue;
        }
      } else {
        bestMobilogram = new Mobilogram(dataFile, allScanNumbers);
      }

      // Add this mzMobilogram to the mobilogram
      bestMobilogram.addMzMobilogram(scanNumber, mzMobilogram);

      // Move the mobilogram to the set of connected mobilograms
      connectedmobilograms.add(bestMobilogram);

    }

    // Process those mobilograms which were not connected to any m/z peak
    for (Mobilogram testMobilogram : buildingMobilograms) {

      // Skip those which were connected
      if (connectedmobilograms.contains(testMobilogram)) {
        continue;
      }

      // Check if we just finished a long-enough segment
      if (testMobilogram.getBuildingSegmentLength() >= minimumTimeSpan) {
        testMobilogram.commitBuildingSegment();

        // Move the mobilogram to the set of connected mobilograms
        connectedmobilograms.add(testMobilogram);
        continue;
      }

      // Check if we have any committed segments in the mobilogram
      if (testMobilogram.getNumberOfCommittedSegments() > 0) {
        testMobilogram.removeBuildingSegment();

        // Move the mobilogram to the set of connected mobilograms
        connectedmobilograms.add(testMobilogram);
        continue;
      }

    }

    // All remaining mobilograms in buildingmobilograms are discarded
    // and buildingmobilograms is replaced with connectedmobilograms
    buildingMobilograms = connectedmobilograms;

  }

  public Mobilogram[] finishMobilograms() {

    // Iterate through current mobilograms and remove those which do not
    // contain any committed segment nor long-enough building segment

    Iterator<Mobilogram> chromIterator = buildingMobilograms.iterator();
    while (chromIterator.hasNext()) {

      Mobilogram mobilogram = chromIterator.next();

      if (mobilogram.getBuildingSegmentLength() >= minimumTimeSpan) {
        mobilogram.commitBuildingSegment();
        mobilogram.finishMobilogram();
      } else {
        if (mobilogram.getNumberOfCommittedSegments() == 0) {
          chromIterator.remove();
          continue;
        } else {
          mobilogram.removeBuildingSegment();
          mobilogram.finishMobilogram();
        }
      }

      // Remove mobilograms smaller then minimum height
      if (mobilogram.getHeight() < minimumHeight)
        chromIterator.remove();

    }

    // All remaining mobilograms are good, so we can return them
    Mobilogram[] mobilograms = buildingMobilograms.toArray(new Mobilogram[0]);
    return mobilograms;
  }

}
