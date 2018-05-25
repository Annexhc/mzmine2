package net.sf.mzmine.parameters.parametertypes;

import java.util.Collection;
import javax.swing.BorderFactory;
import org.w3c.dom.Element;
import net.sf.mzmine.parameters.UserParameter;

public class PasswordParameter implements UserParameter<String, PasswordComponent> {

  private String name, description, value;
  private int inputsize = 20;
  private boolean valueRequired = true;

  public PasswordParameter(String name, String description) {
    this(name, description, null);
  }

  public PasswordParameter(String name, String description, int inputsize) {
    this.name = name;
    this.description = description;
    this.inputsize = inputsize;
  }

  public PasswordParameter(String name, String description, String defaultValue) {
    this.name = name;
    this.description = description;
    this.value = defaultValue;
  }

  public PasswordParameter(String name, String description, boolean valueRequired) {
    this.name = name;
    this.description = description;
    this.valueRequired = valueRequired;
  }

  public PasswordParameter(String name, String description, String defaultValue,
      boolean valueRequired) {
    this.name = name;
    this.description = description;
    this.value = defaultValue;
    this.valueRequired = valueRequired;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getValue() {
    return value;
  }

  @Override
  public void setValue(String newValue) {
    this.value = newValue;
  }

  @Override
  public boolean checkValue(Collection<String> errorMessages) {
    if (!valueRequired)
      return true;
    if ((value == null) || (value.trim().length() == 0)) {
      errorMessages.add(name + " is not set properly");
      return false;
    }
    return true;
  }

  @Override
  public void loadValueFromXML(Element xmlElement) {
    value = xmlElement.getTextContent();
  }

  @Override
  public void saveValueToXML(Element xmlElement) {
    if (value == null)
      return;
    xmlElement.setTextContent(value);
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public PasswordComponent createEditingComponent() {
    PasswordComponent passwordComponent = new PasswordComponent(inputsize);
    passwordComponent.setBorder(BorderFactory.createCompoundBorder(passwordComponent.getBorder(),
        BorderFactory.createEmptyBorder(0, 4, 0, 0)));
    return passwordComponent;
  }

  @Override
  public void setValueFromComponent(PasswordComponent component) {
    value = component.getText().toString();
  }

  @Override
  public void setValueToComponent(PasswordComponent component, String newValue) {
    component.setText(newValue);
  }

  @Override
  public PasswordParameter cloneParameter() {
    PasswordParameter copy = new PasswordParameter(name, description);
    copy.setValue(this.getValue());
    return copy;
  }

}
