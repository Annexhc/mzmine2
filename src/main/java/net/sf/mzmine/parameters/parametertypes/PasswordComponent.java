package net.sf.mzmine.parameters.parametertypes;

import javax.swing.JPanel;
import javax.swing.JPasswordField;

public class PasswordComponent extends JPanel {

  /*
   * Parameter component to enter a password for e-Mail error messages
   */
  private static final long serialVersionUID = 1L;

  private final JPasswordField passwordField;

  public PasswordComponent(int inputsize) {
    passwordField = new JPasswordField(inputsize);
    add(passwordField);
  }

  public void setText(String text) {
    passwordField.setText(text);
  }

  public String getText() {
    return passwordField.getPassword().toString();
  }

  @Override
  public void setToolTipText(String toolTip) {
    passwordField.setToolTipText(toolTip);
  }
}
