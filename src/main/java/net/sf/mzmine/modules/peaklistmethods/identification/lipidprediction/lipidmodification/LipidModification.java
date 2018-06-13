package net.sf.mzmine.modules.peaklistmethods.identification.lipidprediction.lipidmodification;

import net.sf.mzmine.util.FormulaUtils;

public class LipidModification {

  private String lipidModification;

  public LipidModification(String lipidModification) {

    this.lipidModification = lipidModification;
  }

  public String getLipidModificatio() {
    return lipidModification;
  }

  public void setLipidModification(String newLipidModification) {
    lipidModification = newLipidModification;
  }

  public Double getModificationMass() {
    Double lipidModificationMass = null;
    lipidModificationMass = FormulaUtils.calculateExactMass(lipidModification);
    return lipidModificationMass;
  }

  public String toString() {
    return "Modify lipid with [" + lipidModification + "]";
  }

}
