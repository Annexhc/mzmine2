package net.sf.mzmine.modules.rawdatamethods.rawdatasplitter;

public class RawDataFileSplitPosition {

  private double splitPosition;

  public RawDataFileSplitPosition(double splitPosition) {

    this.splitPosition = splitPosition;
  }

  public double getSplitPosition() {
    return splitPosition;
  }

  public void setSplitPosition(double newSplitPosition) {
    splitPosition = newSplitPosition;
  }

  public String toString() {

    return "Split raw file @" + Double.toString(splitPosition);
  }
}
