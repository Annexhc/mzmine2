package net.sf.mzmine.parameters.parametertypes;

import javax.swing.JButton;
import net.sf.mzmine.modules.peaklistmethods.identification.lipidprediction.lipidmodification.AddLipidModificationAction;
import net.sf.mzmine.modules.peaklistmethods.identification.lipidprediction.lipidmodification.LipidModification;

public class LipidModificationChoiceComponent extends MultiChoiceComponent {

  public LipidModificationChoiceComponent(LipidModification[] theChoices) {
    super(theChoices);
    addButton(new JButton(new AddLipidModificationAction()));
  }

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

}
