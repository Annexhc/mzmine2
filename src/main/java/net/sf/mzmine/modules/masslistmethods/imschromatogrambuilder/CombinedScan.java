package net.sf.mzmine.modules.masslistmethods.imschromatogrambuilder;

import java.util.ArrayList;
import com.google.common.collect.Range;
import net.sf.mzmine.datamodel.DataPoint;
import net.sf.mzmine.datamodel.MassList;
import net.sf.mzmine.datamodel.MassSpectrumType;
import net.sf.mzmine.datamodel.PolarityType;
import net.sf.mzmine.datamodel.RawDataFile;
import net.sf.mzmine.datamodel.Scan;

public class CombinedScan implements Scan {

  private Scan scans[];
  private Range<Double> dataPointMZRange;
  private DataPointIMS highestDataPoint;
  private double tIC;
  private MassSpectrumType spectrumType;
  private int numberOfDataPoints;
  private DataPointIMS dataPoints[];
  private RawDataFile dataFile;
  private int scanNumber;
  private String scanDefinition;
  private int msLevel;
  private double retentionTime;
  private Range<Double> scanningMZRange;
  private double precursorMZ;
  private PolarityType polarityType;
  private int precursorCharge;
  private int fragmentScanNumbers[];
  private MassList massLists[];
  private MassList massList;

  public CombinedScan(Scan scans[]) {
    this.scans = scans;
    dataPointMZRange = scans[0].getDataPointMZRange();


    // sum up all data points
    ArrayList<DataPointIMS> dataPointsIMSList = new ArrayList<DataPointIMS>();

    for (int i = 0; i < scans.length; i++) {
      DataPoint dataPointsOfScan[] = scans[i].getDataPoints();
      for (int j = 0; j < scans.length; j++) {
        if (scans[i].getRetentionTime() == scans[j].getRetentionTime()) {
          double totalIntensity = 0;
          for (int k = 0; k < dataPointsOfScan.length; k++) {
            totalIntensity = totalIntensity + dataPointsOfScan[k].getIntensity();
            dataPoints[i].setMZ(dataPointsOfScan[k].getMZ());
          }
          dataPoints[i].setIntensity(totalIntensity);
          dataPointsIMSList.add(dataPoints[i]);
        }
      }
    }
  }

  @Override
  public Range<Double> getDataPointMZRange() {
    // TODO Auto-generated method stub
    return dataPointMZRange;
  }

  @Override
  public DataPoint getHighestDataPoint() {
    // TODO Auto-generated method stub
    return highestDataPoint;
  }

  @Override
  public double getTIC() {
    // TODO Auto-generated method stub
    return tIC;
  }

  @Override
  public MassSpectrumType getSpectrumType() {
    // TODO Auto-generated method stub
    return spectrumType;
  }

  @Override
  public int getNumberOfDataPoints() {
    // TODO Auto-generated method stub
    return numberOfDataPoints;
  }

  @Override
  public DataPoint[] getDataPoints() {
    // TODO Auto-generated method stub
    return dataPoints;
  }

  @Override
  public DataPoint[] getDataPointsByMass(Range<Double> mzRange) {
    // TODO Auto-generated method stub
    return dataPoints;
  }

  @Override
  public DataPoint[] getDataPointsOverIntensity(double intensity) {
    // TODO Auto-generated method stub
    return dataPoints;
  }

  @Override
  public RawDataFile getDataFile() {
    // TODO Auto-generated method stub
    return dataFile;
  }

  @Override
  public int getScanNumber() {
    // TODO Auto-generated method stub
    return scanNumber;
  }

  @Override
  public String getScanDefinition() {
    // TODO Auto-generated method stub
    return scanDefinition;
  }

  @Override
  public int getMSLevel() {
    // TODO Auto-generated method stub
    return msLevel;
  }

  @Override
  public double getRetentionTime() {
    // TODO Auto-generated method stub
    return retentionTime;
  }

  @Override
  public Range<Double> getScanningMZRange() {
    // TODO Auto-generated method stub
    return scanningMZRange;
  }

  @Override
  public double getPrecursorMZ() {
    // TODO Auto-generated method stub
    return precursorMZ;
  }

  @Override
  public PolarityType getPolarity() {
    // TODO Auto-generated method stub
    return polarityType;
  }

  @Override
  public int getPrecursorCharge() {
    // TODO Auto-generated method stub
    return precursorCharge;
  }

  @Override
  public int[] getFragmentScanNumbers() {
    // TODO Auto-generated method stub
    return fragmentScanNumbers;
  }

  @Override
  public MassList[] getMassLists() {
    // TODO Auto-generated method stub
    return massLists;
  }

  @Override
  public MassList getMassList(String name) {
    // TODO Auto-generated method stub
    return massList;
  }

  @Override
  public void addMassList(MassList massList) {
    // TODO Auto-generated method stub

  }

  @Override
  public void removeMassList(MassList massList) {
    // TODO Auto-generated method stub

  }

}
