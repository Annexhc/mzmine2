package net.sf.mzmine.modules.tools.kellerlist;

import org.jfree.data.xy.AbstractXYDataset;
import com.google.common.collect.Range;
import net.sf.mzmine.datamodel.RawDataFile;
import net.sf.mzmine.datamodel.Scan;
import net.sf.mzmine.util.ScanUtils;

public class KellerListDataset extends AbstractXYDataset {

  /**
   * Dataset for Keller list of contaminantes
   * 
   * @author Ansgar Korf (ansgar.korf@uni-muenster.de)
   */
  private static final long serialVersionUID = 1L;

  private double[] xValues;
  private double[] yValues;
  private int scanNumbersMSOneLevel;
  private boolean intensityOverZero;

  public KellerListDataset(RawDataFile dataFile, double accurateMass, Range<Double> rangeMZ,
      double noiseLevel, Scan scans[]) {
    this.scanNumbersMSOneLevel = scans.length;
    this.xValues = new double[scanNumbersMSOneLevel];
    this.yValues = new double[scanNumbersMSOneLevel];

    for (int i = 0; i < yValues.length; i++) {
      yValues[i] = ScanUtils.calculateTIC(scans[i], rangeMZ);
      if (yValues[i] > 0) {
        intensityOverZero = true;
      }
      xValues[i] = scans[i].getRetentionTime();
    }
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
}
