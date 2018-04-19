package net.sf.mzmine.modules.rawdatamethods.rawdatasplitter;

public class RawDataFileSplitPosition {

  private final int splitID;
  private final double splitPosition;

  public RawDataFileSplitPosition(final int splitID, final double splitPosition) {

    this.splitID = splitID;
    this.splitPosition = splitPosition;
  }

  public int getSplitID() {
    return splitID;
  }

  public double getSplitPosition() {
    return splitPosition;
  }

}
