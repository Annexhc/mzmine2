package net.sf.mzmine.parameters.parametertypes.ranges;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import com.google.common.collect.Range;
import net.sf.mzmine.datamodel.RawDataFile;
import net.sf.mzmine.main.MZmineCore;
import net.sf.mzmine.parameters.dialogs.ParameterSetupDialog;
import net.sf.mzmine.parameters.parametertypes.selectors.RawDataFilesComponent;
import net.sf.mzmine.parameters.parametertypes.selectors.RawDataFilesParameter;

public class MobilityRangeComponent extends DoubleRangeComponent implements ActionListener {

  private static final long serialVersionUID = 1L;
  private final JButton setAutoButton;

  public MobilityRangeComponent() {

    super(MZmineCore.getConfiguration().getRTFormat());

    setBorder(BorderFactory.createEmptyBorder(0, 9, 0, 0));

    add(new JLabel("min."), 3, 0, 1, 1, 1, 0, GridBagConstraints.NONE);

    setAutoButton = new JButton("Auto range");
    setAutoButton.addActionListener(this);
    RawDataFile currentFiles[] = MZmineCore.getProjectManager().getCurrentProject().getDataFiles();
    setAutoButton.setEnabled(currentFiles.length > 0);
    add(setAutoButton, 4, 0, 1, 1, 1, 0, GridBagConstraints.NONE);
  }

  @Override
  public void actionPerformed(ActionEvent event) {

    Object src = event.getSource();

    if (src == setAutoButton) {
      RawDataFile currentFiles[] =
          MZmineCore.getProjectManager().getCurrentProject().getDataFiles();

      try {
        ParameterSetupDialog setupDialog =
            (ParameterSetupDialog) SwingUtilities.getWindowAncestor(this);
        RawDataFilesComponent rdc = (RawDataFilesComponent) setupDialog
            .getComponentForParameter(new RawDataFilesParameter());

        // If the current setup dialog has no raw data file selector, it
        // is probably in the parent dialog, so let's check it
        if (rdc == null) {
          setupDialog = (ParameterSetupDialog) setupDialog.getParent();
          if (setupDialog != null) {
            rdc = (RawDataFilesComponent) setupDialog
                .getComponentForParameter(new RawDataFilesParameter());
          }
        }
        if (rdc != null) {
          RawDataFile matchingFiles[] = rdc.getValue().getMatchingRawDataFiles();
          if (matchingFiles.length > 0)
            currentFiles = matchingFiles;
        }
      } catch (Exception e) {
        e.printStackTrace();
      }

      Range<Double> mobilityRange = null;
      for (RawDataFile file : currentFiles) {
        Range<Double> fileRange = file.getDataRTRange();
        if (mobilityRange == null)
          mobilityRange = fileRange;
        else
          mobilityRange = mobilityRange.span(fileRange);
      }
      setValue(mobilityRange);
    }

  }

  @Override
  public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);
    setAutoButton.setEnabled(enabled);
  }
}
