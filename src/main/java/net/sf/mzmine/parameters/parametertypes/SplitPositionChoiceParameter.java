package net.sf.mzmine.parameters.parametertypes;

import net.sf.mzmine.modules.rawdatamethods.rawdatasplitter.RawDataFileSplitPosition;

public class SplitPositionChoiceParameter extends MultiChoiceParameter<RawDataFileSplitPosition> {
  /**
   * Create the parameter.
   *
   * @param name name of the parameter.
   * @param description description of the parameter.
   */
  public SplitPositionChoiceParameter(String name, String description,
      RawDataFileSplitPosition[] choices) {
    super(name, description, choices);


  }

  @Override
  public MultiChoiceComponent createEditingComponent() {
    return new SplitPositionChoiceComponent(getChoices());
  }

  @Override
  public void setValueFromComponent(final MultiChoiceComponent component) {

    super.setValueFromComponent(component);
    setChoices((RawDataFileSplitPosition[]) component.getChoices());
  }

  @Override
  public void setValueToComponent(MultiChoiceComponent component,
      RawDataFileSplitPosition[] newValue) {
    super.setValueToComponent(component, newValue);
    setChoices((RawDataFileSplitPosition[]) component.getChoices());
  }

  @Override
  public SplitPositionChoiceParameter cloneParameter() {

    final SplitPositionChoiceParameter copy =
        new SplitPositionChoiceParameter(getName(), getDescription(), getChoices());
    copy.setChoices(getChoices());
    copy.setValue(getValue());
    return copy;
  }
}
