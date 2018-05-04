package net.sf.mzmine.modules.rawdatamethods.recalibrationmz.lockmass;

import java.awt.Component;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;
import net.sf.mzmine.parameters.parametertypes.LockMassComponent;

public class LockMassResetChoices extends AbstractAction {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /**
   * Create the action.
   */
  public LockMassResetChoices() {

    super("Remove");
    putValue(SHORT_DESCRIPTION, "Remove all lock masses");
  }

  @Override
  public void actionPerformed(final ActionEvent e) {

    // Parent component.
    final LockMassComponent parent = (LockMassComponent) SwingUtilities
        .getAncestorOfClass(LockMassComponent.class, (Component) e.getSource());

    if (parent != null) {

      // Reset default choices.
      parent.setChoices(new LockMass[0]);
    }
  }
}
