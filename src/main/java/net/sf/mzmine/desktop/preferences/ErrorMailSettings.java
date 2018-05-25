package net.sf.mzmine.desktop.preferences;

import net.sf.mzmine.parameters.Parameter;
import net.sf.mzmine.parameters.impl.SimpleParameterSet;
import net.sf.mzmine.parameters.parametertypes.IntegerParameter;
import net.sf.mzmine.parameters.parametertypes.PasswordParameter;
import net.sf.mzmine.parameters.parametertypes.StringParameter;

/**
 * Error mail settings
 */
public class ErrorMailSettings extends SimpleParameterSet {

  // we use the same address to send and receive emails
  public static final StringParameter eMailAddress =
      new StringParameter("E-mail address", "Enter your e-Mail address");

  public static final PasswordParameter eMailPassword =
      new PasswordParameter("E-mail password", "Enter your e-Mail passowrd", true);

  public static final StringParameter smtpHost =
      new StringParameter("Host server smtp", "Enter host server smtp, e.g. smtp.gmail.com");

  public static final IntegerParameter smtpPort =
      new IntegerParameter("smtp port", "Enter smtp port, for gmail 465");


  public ErrorMailSettings() {
    super(new Parameter[] {eMailAddress, eMailPassword, smtpHost, smtpPort});
  }

}
