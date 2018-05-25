package net.sf.mzmine.desktop.preferences;

import java.io.IOException;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import net.sf.mzmine.util.EMailUtil;

public class ErrorMail {

  public void sendErrorEmail(String emailTo, String emailFrom, String smtpServer, String subject,
      String msg, String password, Integer port) throws IOException {

    Properties props = new Properties();
    props.put("mail.smtp.host", smtpServer); // SMTP Host
    props.put("mail.smtp.socketFactory.port", port); // SSL Port
    props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); // SSL Factory
                                                                                  // Class
    props.put("mail.smtp.auth", "true"); // Enabling SMTP Authentication
    props.put("mail.smtp.port", port); // SMTP Port, gmail 465

    Authenticator auth = new Authenticator() {

      // override the getPasswordAuthentication method
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(emailFrom, password);
      }
    };
    Session session = Session.getDefaultInstance(props, auth);
    EMailUtil.sendEmail(session, emailTo, subject, msg);

  }
}
