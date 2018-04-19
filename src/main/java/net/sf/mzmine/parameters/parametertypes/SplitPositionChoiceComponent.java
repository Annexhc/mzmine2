package net.sf.mzmine.parameters.parametertypes;

import javax.swing.JButton;
import net.sf.mzmine.modules.rawdatamethods.rawdatasplitter.AddRawDataSplitPositionAction;

public class SplitPositionChoiceComponent extends MultiChoiceComponent {

  public SplitPositionChoiceComponent(Object[] theChoices) {
    super(theChoices);
    addButton(new JButton(new AddRawDataSplitPositionAction()));
  }

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

}
