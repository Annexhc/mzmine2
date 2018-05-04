package net.sf.mzmine.modules.rawdatamethods.recalibrationmz.lockmass;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;
import net.sf.mzmine.main.MZmineCore;
import net.sf.mzmine.parameters.Parameter;
import net.sf.mzmine.parameters.ParameterSet;
import net.sf.mzmine.parameters.impl.SimpleParameterSet;
import net.sf.mzmine.parameters.parametertypes.DoubleParameter;
import net.sf.mzmine.parameters.parametertypes.LockMassComponent;
import net.sf.mzmine.util.ExitCode;

public class AddNewLockMassAction extends AbstractAction {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /**
   * Create the action.
   */
  public AddNewLockMassAction() {

    super("Add lock mass");
  }

  public void actionPerformed(final ActionEvent e) {

    // Parent component.
    final LockMassComponent parent = (LockMassComponent) SwingUtilities
        .getAncestorOfClass(LockMassComponent.class, (Component) e.getSource());

    if (parent != null) {

      // Show dialog.
      final ParameterSet parameters = new AddLockMassParameters();
      if (parameters.showSetupDialog(MZmineCore.getDesktop().getMainWindow(),
          true) == ExitCode.OK) {

        // Create new split position.
        final LockMass lockMass =
            new LockMass(parameters.getParameter(AddLockMassParameters.exactMass).getValue());

        // Add to list of choices (if not already present).
        final Collection<LockMass> choices =
            new ArrayList<LockMass>(Arrays.asList((LockMass[]) parent.getChoices()));
        if (!choices.contains(lockMass)) {

          choices.add(lockMass);
          parent.setChoices(choices.toArray(new LockMass[choices.size()]));
        }
      }
    }
  }

  /**
   * Represents a lock mass.
   */
  private static class AddLockMassParameters extends SimpleParameterSet {

    // Retention time to split.
    private static final DoubleParameter exactMass = new DoubleParameter("Exact mass",
        "Exact mass of selected lock mass", MZmineCore.getConfiguration().getMZFormat());

    private AddLockMassParameters() {
      super(new Parameter[] {exactMass});
    }
  }
}
