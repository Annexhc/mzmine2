package net.sf.mzmine.modules.rawdatamethods.recalibrationmz.lockmass;

import java.util.Arrays;
import org.jfree.data.xy.AbstractXYDataset;
import com.google.common.collect.Range;
import net.sf.mzmine.datamodel.DataPoint;
import net.sf.mzmine.datamodel.RawDataFile;

public class LockMassDataSet extends AbstractXYDataset {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private double[] xValues;
  private double[] yValues;
  private int scanNumbersMSOneLevel;
  private double noiseLevel;
  private boolean intensityOverZero;
  // private boolean passedIntensityPreTest;

  public LockMassDataSet(RawDataFile dataFile, double exactMass, Range<Double> rangeMZ,
      double noiseLevel) {

    this.scanNumbersMSOneLevel = dataFile.getNumOfScans(1);
    this.xValues = new double[scanNumbersMSOneLevel];
    this.yValues = new double[scanNumbersMSOneLevel];
    this.noiseLevel = noiseLevel;

    // // Intensity pre test, check 3 data points, if all are zero dont build XIC
    // int[] testScanNumbers = new int[] {dataFile.getNumOfScans(1) - 1};
    // int testCounter = 0;
    // for (int i = 0; i < testScanNumbers.length; i++) {
    // DataPoint[] dpTest = dataFile.getScan(testScanNumbers[i]).getDataPointsByMass(rangeMZ);
    // double[] intensities = new double[dpTest.length];
    // for (int j = 0; j < dpTest.length; j++) {
    // intensities[j] = dpTest[j].getIntensity();
    // if (intensities[j] > 0) {
    // testCounter++;
    // }
    // }
    // if (testCounter == 4) {
    // passedIntensityPreTest = true;
    // } else {
    // passedIntensityPreTest = false;
    // }
    // }

    // get intensities for yValues
    // if (passedIntensityPreTest) {
    int[] msOneScanNumbers = dataFile.getScanNumbers(1);
    for (int i = 0; i < yValues.length; i++) {
      DataPoint[] dp = dataFile.getScan(msOneScanNumbers[i]).getDataPointsByMass(rangeMZ);
      double[] intensities = new double[dp.length];
      for (int j = 0; j < dp.length; j++) {
        intensities[j] = dp[j].getIntensity();
        if (intensities[j] > 0) {
          intensityOverZero = true;
        }
      }
      if (intensities.length > 0) {
        yValues[i] = getMaxIntensity(intensities);
      } else {
        yValues[i] = 0;
      }
      xValues[i] = dataFile.getScan(i + 1).getRetentionTime();
    }
  }
  // }

  private double getMaxIntensity(double[] intensities) {
    double intensity = 0;
    Arrays.sort(intensities);
    int maxIndex = intensities.length - 1;
    intensity = intensities[maxIndex];
    return intensity;
  }

  @Override
  public int getItemCount(int series) {
    return scanNumbersMSOneLevel;
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

  @Override
  public Comparable getSeriesKey(int series) {
    return "series";
  }

  public double[] getxValues() {
    return xValues;
  }

  public double[] getyValues() {
    return yValues;
  }

  public boolean intensityOverZero() {
    return intensityOverZero;
  }

  // public boolean passedIntensityPreTest() {
  // return passedIntensityPreTest;
  // }

}
