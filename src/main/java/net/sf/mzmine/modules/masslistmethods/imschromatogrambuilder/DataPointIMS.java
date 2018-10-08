package net.sf.mzmine.modules.masslistmethods.imschromatogrambuilder;

import net.sf.mzmine.datamodel.DataPoint;

public interface DataPointIMS extends DataPoint {


  @Override
  public double getMZ();

  @Override
  public double getIntensity();

  public void setMZ(double mz);

  public void setIntensity(double mz);

}
