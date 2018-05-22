package net.sf.mzmine.parameters.parametertypes;

import javax.swing.JButton;
import net.sf.mzmine.modules.rawdatamethods.rawdatasplitter.AddRawDataSplitPositionAction;
import net.sf.mzmine.modules.rawdatamethods.rawdatasplitter.RawDataFileSplitPosition;

public class SplitPositionChoiceComponent extends MultiChoiceComponent {

  public SplitPositionChoiceComponent(RawDataFileSplitPosition[] theChoices) {
    super(theChoices);
    addButton(new JButton(new AddRawDataSplitPositionAction()));
  }

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

}
