package net.sf.mzmine.modules.rawdatamethods.recalibrationmz.lockmass;

public class LockMass {

  private double lockMass;

  public LockMass(double lockMass) {
    this.lockMass = lockMass;
  }

  public double getLockMass() {
    return lockMass;
  }

  public void setSplitPosition(double newLockMass) {
    lockMass = newLockMass;
  }

  public String toString() {
    return "Use " + Double.toString(lockMass) + " for calibration";
  }
}
