package net.sf.mzmine.util.regression;

public interface TrendLine {
  public void setValues(double[] y, double[] x); // y ~ f(x)

  public double predict(double x); // get a predicted y for a given x

  public double getRSquared(double[] y, double[] x); // get R squared
}
