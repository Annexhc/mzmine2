package net.sf.mzmine.datamodel.impl;

import java.text.Format;
import net.sf.mzmine.datamodel.IMSDataPoint;
import net.sf.mzmine.main.MZmineCore;

public class SimpleIMSDataPoint implements IMSDataPoint {

  private double mz, intensity, mobility;

  /**
   * Constructor which copies the data from another IMSDataPoint
   */
  public SimpleIMSDataPoint(IMSDataPoint dp) {
    this.mz = dp.getMZ();
    this.intensity = dp.getIntensity();
    this.mobility = dp.getMobility();
  }

  /**
   * @param mz
   * @param intensity
   */
  public SimpleIMSDataPoint(double mz, double intensity, double mobility) {
    this.mz = mz;
    this.intensity = intensity;
    this.mobility = mobility;
  }

  @Override
  public double getIntensity() {
    return intensity;
  }

  @Override
  public double getMZ() {
    return mz;
  }

  @Override
  public double getMobility() {
    return mobility;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof IMSDataPoint))
      return false;
    IMSDataPoint dp = (IMSDataPoint) obj;
    return (this.mz == dp.getMZ())
        && (this.intensity == dp.getIntensity() && (this.mobility == dp.getMobility()));
  }

  @Override
  public int hashCode() {
    return (int) (this.mz + this.intensity);
  }

  @Override
  public String toString() {
    Format mzFormat = MZmineCore.getConfiguration().getMZFormat();
    Format intensityFormat = MZmineCore.getConfiguration().getIntensityFormat();
    String str =
        "m/z: " + mzFormat.format(mz) + ", intensity: " + intensityFormat.format(intensity);
    return str;
  }


}
