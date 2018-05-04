package net.sf.mzmine.parameters.parametertypes;

import net.sf.mzmine.modules.rawdatamethods.recalibrationmz.lockmass.LockMass;

public class LockMassParameter extends MultiChoiceParameter<LockMass> {
  /**
   * Create the parameter.
   *
   * @param name name of the parameter.
   * @param description description of the parameter.
   */
  public LockMassParameter(String name, String description, LockMass[] choices) {
    super(name, description, choices);


  }

  @Override
  public MultiChoiceComponent createEditingComponent() {
    return new LockMassComponent(getChoices());
  }

  @Override
  public void setValueFromComponent(final MultiChoiceComponent component) {

    super.setValueFromComponent(component);
    setChoices((LockMass[]) component.getChoices());
  }

  @Override
  public void setValueToComponent(MultiChoiceComponent component, LockMass[] newValue) {
    super.setValueToComponent(component, newValue);
    setChoices((LockMass[]) component.getChoices());
  }

  @Override
  public LockMassParameter cloneParameter() {

    final LockMassParameter copy = new LockMassParameter(getName(), getDescription(), getChoices());
    copy.setChoices(getChoices());
    copy.setValue(getValue());
    return copy;
  }
}
