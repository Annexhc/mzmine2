package net.sf.mzmine.modules.rawdatamethods.rawdatasplitter;

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
import net.sf.mzmine.parameters.parametertypes.IntegerParameter;
import net.sf.mzmine.parameters.parametertypes.SplitPositionChoiceComponent;
import net.sf.mzmine.util.ExitCode;

public class AddRawDataSplitPositionAction extends AbstractAction {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /**
   * Create the action.
   */
  public AddRawDataSplitPositionAction() {

    super("Add...");
  }

  public void actionPerformed(final ActionEvent e) {

    // Parent component.
    final SplitPositionChoiceComponent parent = (SplitPositionChoiceComponent) SwingUtilities
        .getAncestorOfClass(SplitPositionChoiceComponent.class, (Component) e.getSource());

    if (parent != null) {

      // Show dialog.
      final ParameterSet parameters = new AddSplitPositionParameters();
      if (parameters.showSetupDialog(MZmineCore.getDesktop().getMainWindow(),
          true) == ExitCode.OK) {

        // Create new split position.
        final RawDataFileSplitPosition rawDataFileSplitPosition = new RawDataFileSplitPosition(
            parameters.getParameter(AddSplitPositionParameters.splitID).getValue(),
            parameters.getParameter(AddSplitPositionParameters.splitPosition).getValue());

        // Add to list of choices (if not already present).
        final Collection<RawDataFileSplitPosition> choices =
            new ArrayList<RawDataFileSplitPosition>(
                Arrays.asList((RawDataFileSplitPosition[]) parent.getChoices()));
        if (!choices.contains(rawDataFileSplitPosition)) {

          choices.add(rawDataFileSplitPosition);
          parent.setChoices(choices.toArray(new RawDataFileSplitPosition[choices.size()]));
        }
      }
    }
  }

  /**
   * Represents a split position.
   */
  private static class AddSplitPositionParameters extends SimpleParameterSet {

    // Adduct name.
    private static final IntegerParameter splitID =
        new IntegerParameter("Number of split", "Number of split");

    // Adduct mass difference.
    private static final DoubleParameter splitPosition = new DoubleParameter("Split position",
        "Scan number after split position", MZmineCore.getConfiguration().getRTFormat());

    private AddSplitPositionParameters() {
      super(new Parameter[] {splitID, splitPosition});
    }
  }
}
