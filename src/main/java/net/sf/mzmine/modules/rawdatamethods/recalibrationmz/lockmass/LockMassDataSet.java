package net.sf.mzmine.modules.rawdatamethods.recalibrationmz.lockmass;

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
  private int scanNumbers;

  public LockMassDataSet(RawDataFile dataFile, double exactMass, Range<Double> rangeMZ) {

    this.scanNumbers = dataFile.getNumOfScans();
    this.xValues = new double[scanNumbers];
    this.yValues = new double[scanNumbers];

    // get intensities for yValues
    for (int i = 1; i < yValues.length; i++) {
      DataPoint[] dp = dataFile.getScan(i).getDataPoints();
      System.out.println(dp.length);
      double intensitySum = 0;
      for (int j = 0; j < dp.length; j++) {
        intensitySum = intensitySum + dp[j].getIntensity();
      }
      yValues[i] = intensitySum;
      xValues[i] = dataFile.getScan(i).getRetentionTime();
    }

  }


  @Override
  public int getItemCount(int series) {
    return scanNumbers;
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

}
