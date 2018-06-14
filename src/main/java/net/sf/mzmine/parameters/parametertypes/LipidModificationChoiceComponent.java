package net.sf.mzmine.parameters.parametertypes;

import javax.swing.JButton;
import net.sf.mzmine.modules.peaklistmethods.identification.lipidprediction.lipidmodification.AddLipidModificationAction;
import net.sf.mzmine.modules.peaklistmethods.identification.lipidprediction.lipidmodification.ExportLipidModificationsAction;
import net.sf.mzmine.modules.peaklistmethods.identification.lipidprediction.lipidmodification.ImportLipidModificationsAction;
import net.sf.mzmine.modules.peaklistmethods.identification.lipidprediction.lipidmodification.LipidModification;
import net.sf.mzmine.modules.peaklistmethods.identification.lipidprediction.lipidmodification.RemoveLipidModificationsAction;

public class LipidModificationChoiceComponent extends MultiChoiceComponent {

  public LipidModificationChoiceComponent(LipidModification[] theChoices) {
    super(theChoices);
    addButton(new JButton(new AddLipidModificationAction()));
    addButton(new JButton(new ImportLipidModificationsAction()));
    addButton(new JButton(new ExportLipidModificationsAction()));
    addButton(new JButton(new RemoveLipidModificationsAction()));
  }

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

}
