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
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.Vector;
import javax.annotation.Nonnull;
import com.google.common.collect.Range;
import com.google.common.primitives.Ints;
import net.sf.mzmine.datamodel.IMSDataPoint;
import net.sf.mzmine.datamodel.IMSFeature;
import net.sf.mzmine.datamodel.IsotopePattern;
import net.sf.mzmine.datamodel.RawDataFile;
import net.sf.mzmine.datamodel.Scan;
import net.sf.mzmine.datamodel.impl.SimpleMobilogramInformation;
import net.sf.mzmine.main.MZmineCore;
import net.sf.mzmine.util.MathUtils;
import net.sf.mzmine.util.ScanUtils;

/**
 * Mobilogram implementing ChromatographicMobilogram.
 */
public class Mobilogram implements IMSFeature {

  private SimpleMobilogramInformation peakInfo;

  // Data file of this mobilogram
  private RawDataFile dataFile;

  // Data points of the mobilogram (map of scan number -> m/z peak)
  private Hashtable<Integer, IMSDataPoint> dataPointsMap;

  // mobilogram m/z, RT, height, area, ccs,
  private double mz, rt, height, area, mobility, ccs;
  private Double fwhm = null, tf = null, af = null;

  // Top intensity scan, fragment scan
  private int representativeScan = -1, fragmentScan = -1;

  // All MS2 fragment scan numbers
  private int[] allMS2FragmentScanNumbers = new int[] {};

  // Ranges of raw data points
  private Range<Double> rawIMSDataPointsIntensityRange, rawIMSDataPointsMZRange,
      rawIMSDataPointsRTRange;

  // A set of scan numbers of a segment which is currently being connected
  private Vector<Integer> buildingSegment;

  // Keep track of last added data point
  private IMSDataPoint lastMzMobilogram;

  // Number of connected segments, which have been committed by
  // commitBuildingSegment()
  private int numOfCommittedSegments = 0;

  // Isotope pattern. Null by default but can be set later by deisotoping
  // method.
  private IsotopePattern isotopePattern;
  private int charge = 0;

  private double mzSum = 0;
  private int mzN = 0;
  private double mobilitySum = 0;
  private double mobilityN = 0;

  private final int scanNumbers[];

  @Override
  public void outputChromToFile() {
    System.out.println("does nothing");
  }

  /**
   * Initializes this mobilogram
   */
  public Mobilogram(RawDataFile dataFile, int scanNumbers[]) {
    this.dataFile = dataFile;
    this.scanNumbers = scanNumbers;

    rawIMSDataPointsRTRange = dataFile.getDataRTRange(1);

    dataPointsMap = new Hashtable<Integer, IMSDataPoint>();
    buildingSegment = new Vector<Integer>(128);
  }

  /**
   * This method adds a MzMobilogram to this mobilogram. All values of this mobilogram (rt, m/z,
   * intensity and ranges) are updated on request
   * 
   * @param mzValue
   */
  public void addMzMobilogram(int scanNumber, IMSDataPoint iMSDataPoint) {
    dataPointsMap.put(scanNumber, iMSDataPoint);
    lastMzMobilogram = iMSDataPoint;
    mzSum += iMSDataPoint.getMZ();
    mzN++;
    mz = mzSum / mzN;
    mobilitySum += iMSDataPoint.getMobility();
    mobilityN++;
    mobility = mobilitySum / mobilityN;
    buildingSegment.add(scanNumber);

  }

  @Override
  public IMSDataPoint getIMSDataPoint(int scanNumber) {
    return dataPointsMap.get(scanNumber);
  }

  /**
   * Returns m/z value of last added data point
   */
  public IMSDataPoint getLastMzMobilogram() {
    return lastMzMobilogram;
  }

  /**
   * This method returns m/z value of the mobilogram
   */
  @Override
  public double getMZ() {
    return mz;
  }

  /**
   * This method returns a string with the basic information that defines this peak
   * 
   * @return String information
   */
  @Override
  public String toString() {
    return "mobilogram " + MZmineCore.getConfiguration().getMZFormat().format(mz) + " m/z";
  }

  @Override
  public double getArea() {
    return area;
  }

  @Override
  public double getHeight() {
    return height;
  }

  @Override
  public double getCcs() {
    // algorithm ToDo
    return ccs;
  }

  @Override
  public double getMobility() {
    return mobility;
  }

  @Override
  public int getMostIntenseFragmentScanNumber() {
    return fragmentScan;
  }

  @Override
  public int[] getAllMS2FragmentScanNumbers() {
    return allMS2FragmentScanNumbers;
  }

  @Override
  public @Nonnull IMSFeatureStatus getIMSFeatureStatus() {
    return IMSFeatureStatus.DETECTED;
  }

  @Override
  public double getRT() {
    return rt;
  }

  @Override
  public @Nonnull Range<Double> getRawIMSDataPointsIntensityRange() {
    return rawIMSDataPointsIntensityRange;
  }

  @Override
  public @Nonnull Range<Double> getRawIMSDataPointsMZRange() {
    return rawIMSDataPointsMZRange;
  }

  @Override
  public @Nonnull Range<Double> getRawIMSDataPointsRTRange() {
    return rawIMSDataPointsRTRange;
  }

  @Override
  public int getRepresentativeScanNumber() {
    return representativeScan;
  }

  @Override
  public @Nonnull int[] getScanNumbers() {
    return scanNumbers;
  }

  @Override
  public @Nonnull RawDataFile getDataFile() {
    return dataFile;
  }

  @Override
  public IsotopePattern getIsotopePattern() {
    return isotopePattern;
  }

  @Override
  public void setIsotopePattern(@Nonnull IsotopePattern isotopePattern) {
    this.isotopePattern = isotopePattern;
  }

  public void finishMobilogram() {

    int allScanNumbers[] = Ints.toArray(dataPointsMap.keySet());
    Arrays.sort(allScanNumbers);

    // Calculate median m/z
    double allMzValues[] = new double[allScanNumbers.length];
    for (int i = 0; i < allScanNumbers.length; i++) {
      allMzValues[i] = dataPointsMap.get(allScanNumbers[i]).getMZ();
    }
    mz = MathUtils.calcQuantile(allMzValues, 0.5f);

    // Update raw data point ranges, height, rt and representative scan
    height = Double.MIN_VALUE;
    for (int i = 0; i < allScanNumbers.length; i++) {

      IMSDataPoint mzMobilogram = dataPointsMap.get(allScanNumbers[i]);

      // Replace the MzMobilogram instance with an instance of SimpleIMSDataPoint,
      // to reduce the memory usage. After we finish this mobilogram, we
      // don't need the additional data provided by the MzMobilogram

      dataPointsMap.put(allScanNumbers[i], mzMobilogram);

      if (i == 0) {
        rawIMSDataPointsIntensityRange = Range.singleton(mzMobilogram.getIntensity());
        rawIMSDataPointsMZRange = Range.singleton(mzMobilogram.getMZ());
      } else {
        rawIMSDataPointsIntensityRange =
            rawIMSDataPointsIntensityRange.span(Range.singleton(mzMobilogram.getIntensity()));
        rawIMSDataPointsMZRange =
            rawIMSDataPointsMZRange.span(Range.singleton(mzMobilogram.getMZ()));
      }

      if (height < mzMobilogram.getIntensity()) {
        height = mzMobilogram.getIntensity();
        rt = dataFile.getScan(allScanNumbers[i]).getRetentionTime();
        mobility = dataFile.getScan(allScanNumbers[i]).getMobility();
        representativeScan = allScanNumbers[i];
      }
    }

    // Update area
    area = 0;
    for (int i = 1; i < allScanNumbers.length; i++) {
      // For area calculation, we use retention time in seconds
      double previousRT = dataFile.getScan(allScanNumbers[i - 1]).getRetentionTime() * 60d;
      double currentRT = dataFile.getScan(allScanNumbers[i]).getRetentionTime() * 60d;
      double previousHeight = dataPointsMap.get(allScanNumbers[i - 1]).getIntensity();
      double currentHeight = dataPointsMap.get(allScanNumbers[i]).getIntensity();
      area += (currentRT - previousRT) * (currentHeight + previousHeight) / 2;
    }

    // Update fragment scan
    fragmentScan = ScanUtils.findBestFragmentScan(dataFile, dataFile.getDataRTRange(1),
        rawIMSDataPointsMZRange);

    if (fragmentScan > 0) {
      Scan fragmentScanObject = dataFile.getScan(fragmentScan);
      int precursorCharge = fragmentScanObject.getPrecursorCharge();
      if (precursorCharge > 0)
        this.charge = precursorCharge;
    }

    rawIMSDataPointsRTRange = null;

    for (int scanNum : allScanNumbers) {
      double scanRt = dataFile.getScan(scanNum).getRetentionTime();
      IMSDataPoint dp = getIMSDataPoint(scanNum);

      if ((dp == null) || (dp.getIntensity() == 0.0))
        continue;

      if (rawIMSDataPointsRTRange == null)
        rawIMSDataPointsRTRange = Range.singleton(scanRt);
      else
        rawIMSDataPointsRTRange = rawIMSDataPointsRTRange.span(Range.singleton(scanRt));
    }

    // Discard the fields we don't need anymore
    buildingSegment = null;
    lastMzMobilogram = null;

  }

  public double getBuildingSegmentLength() {
    if (buildingSegment.size() < 2)
      return 0;
    int firstScan = buildingSegment.firstElement();
    int lastScan = buildingSegment.lastElement();
    double firstRT = dataFile.getScan(firstScan).getRetentionTime();
    double lastRT = dataFile.getScan(lastScan).getRetentionTime();
    return (lastRT - firstRT);
  }

  public int getNumberOfCommittedSegments() {
    return numOfCommittedSegments;
  }

  public void removeBuildingSegment() {
    for (int scanNumber : buildingSegment)
      dataPointsMap.remove(scanNumber);
    buildingSegment.clear();
  }

  public void commitBuildingSegment() {
    buildingSegment.clear();
    numOfCommittedSegments++;
  }

  public void addIMSDataPointsFromMobilogram(Mobilogram mobilogram) {
    for (Entry<Integer, IMSDataPoint> dp : mobilogram.dataPointsMap.entrySet()) {
      addMzMobilogram(dp.getKey(), dp.getValue());
    }
  }

  @Override
  public int getCharge() {
    return charge;
  }

  @Override
  public void setCharge(int charge) {
    this.charge = charge;
  }

  @Override
  public Double getFWHM() {
    return fwhm;
  }

  @Override
  public void setFWHM(Double fwhm) {
    this.fwhm = fwhm;
  }

  @Override
  public Double getTailingFactor() {
    return tf;
  }

  @Override
  public void setTailingFactor(Double tf) {
    this.tf = tf;
  }

  @Override
  public Double getAsymmetryFactor() {
    return af;
  }

  @Override
  public void setAsymmetryFactor(Double af) {
    this.af = af;
  }

  @Override
  public void setMobilogramInformation(SimpleMobilogramInformation peakInfoIn) {
    this.peakInfo = peakInfoIn;
  }

  @Override
  public SimpleMobilogramInformation getMobilogramInformation() {
    return peakInfo;
  }

}
