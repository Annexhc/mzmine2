package net.sf.mzmine.parameters.parametertypes;

import javax.swing.JButton;
import net.sf.mzmine.modules.rawdatamethods.recalibrationmz.lockmass.AddNewLockMassAction;
import net.sf.mzmine.modules.rawdatamethods.recalibrationmz.lockmass.LockMass;

public class LockMassComponent extends MultiChoiceComponent {

  public LockMassComponent(LockMass[] theChoices) {
    super(theChoices);
    addButton(new JButton(new AddNewLockMassAction()));
  }

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

}
