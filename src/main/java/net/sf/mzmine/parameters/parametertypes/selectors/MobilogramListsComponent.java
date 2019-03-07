package net.sf.mzmine.parameters.parametertypes.selectors;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import net.sf.mzmine.datamodel.MobilogramList;
import net.sf.mzmine.main.MZmineCore;
import net.sf.mzmine.parameters.Parameter;
import net.sf.mzmine.parameters.impl.SimpleParameterSet;
import net.sf.mzmine.parameters.parametertypes.MultiChoiceParameter;
import net.sf.mzmine.parameters.parametertypes.StringParameter;
import net.sf.mzmine.util.ExitCode;

public class MobilogramListsComponent extends JPanel implements ActionListener {

  private static final long serialVersionUID = 1L;

  private final JComboBox<MobilogramListsSelectionType> typeCombo;
  private final JButton detailsButton;
  private final JLabel numMobilogramListsLabel;
  private MobilogramListsSelection currentValue = new MobilogramListsSelection();

  public MobilogramListsComponent() {

    super(new BorderLayout());

    setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));

    numMobilogramListsLabel = new JLabel();
    add(numMobilogramListsLabel, BorderLayout.WEST);

    typeCombo = new JComboBox<>(MobilogramListsSelectionType.values());
    typeCombo.addActionListener(this);
    add(typeCombo, BorderLayout.CENTER);

    detailsButton = new JButton("...");
    detailsButton.setEnabled(false);
    detailsButton.addActionListener(this);
    add(detailsButton, BorderLayout.EAST);

  }

  void setValue(MobilogramListsSelection newValue) {
    currentValue = newValue.clone();
    MobilogramListsSelectionType type = newValue.getSelectionType();
    if (type != null)
      typeCombo.setSelectedItem(type);
    updateNumMobilogramLists();
  }

  MobilogramListsSelection getValue() {
    return currentValue;
  }

  public void actionPerformed(ActionEvent event) {

    Object src = event.getSource();

    if (src == detailsButton) {
      MobilogramListsSelectionType type =
          (MobilogramListsSelectionType) typeCombo.getSelectedItem();

      if (type == MobilogramListsSelectionType.SPECIFIC_PEAKLISTS) {
        final MultiChoiceParameter<MobilogramList> plsParameter =
            new MultiChoiceParameter<MobilogramList>("Select peak lists", "Select peak lists",
                MZmineCore.getProjectManager().getCurrentProject().getMobilogramLists(),
                currentValue.getSpecificMobilogramLists());
        final SimpleParameterSet paramSet = new SimpleParameterSet(new Parameter[] {plsParameter});
        final Window parent = (Window) SwingUtilities.getAncestorOfClass(Window.class, this);
        final ExitCode exitCode = paramSet.showSetupDialog(parent, true);
        if (exitCode == ExitCode.OK) {
          MobilogramList pls[] = paramSet.getParameter(plsParameter).getValue();
          currentValue.setSpecificMobilogramLists(pls);
        }

      }

      if (type == MobilogramListsSelectionType.NAME_PATTERN) {
        final StringParameter nameParameter = new StringParameter("Name pattern",
            "Set name pattern that may include wildcards (*), e.g. *mouse* matches any name that contains mouse",
            currentValue.getNamePattern());
        final SimpleParameterSet paramSet = new SimpleParameterSet(new Parameter[] {nameParameter});
        final Window parent = (Window) SwingUtilities.getAncestorOfClass(Window.class, this);
        final ExitCode exitCode = paramSet.showSetupDialog(parent, true);
        if (exitCode == ExitCode.OK) {
          String namePattern = paramSet.getParameter(nameParameter).getValue();
          currentValue.setNamePattern(namePattern);
        }

      }

    }

    if (src == typeCombo) {
      MobilogramListsSelectionType type =
          (MobilogramListsSelectionType) typeCombo.getSelectedItem();
      currentValue.setSelectionType(type);
      detailsButton.setEnabled((type == MobilogramListsSelectionType.NAME_PATTERN)
          || (type == MobilogramListsSelectionType.SPECIFIC_PEAKLISTS));
    }

    updateNumMobilogramLists();

  }

  @Override
  public void setToolTipText(String toolTip) {
    typeCombo.setToolTipText(toolTip);
  }

  private void updateNumMobilogramLists() {
    if (currentValue.getSelectionType() == MobilogramListsSelectionType.BATCH_LAST_PEAKLISTS) {
      numMobilogramListsLabel.setText("");
      numMobilogramListsLabel.setToolTipText("");
    } else {
      MobilogramList pls[] = currentValue.getMatchingMobilogramLists();
      if (pls.length == 1) {
        String plName = pls[0].getName();
        if (plName.length() > 22)
          plName = plName.substring(0, 20) + "...";
        numMobilogramListsLabel.setText(plName);
      } else {
        numMobilogramListsLabel.setText(pls.length + " selected");
      }
      numMobilogramListsLabel.setToolTipText(currentValue.toString());
    }
  }
}
