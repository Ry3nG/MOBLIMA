package moblima.utils.services.email;

import moblima.utils.Constants;
import moblima.utils.Helper;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;


/**
 * The type Email service.
 * REFERENCE: https://www.baeldung.com/java-email
 */
public class EmailService {
  private final String senderAddress;
  private final String senderAuthPhrase;

  private final Properties properties;
  private final Session session;

  /**
   * Instantiates a new Email service.
   */
  public EmailService() {
    senderAddress = Constants.getEnv("STMP_USERNAME");
    senderAuthPhrase = Constants.getEnv("STMP_PASSWORD");

    properties = new Properties();
    properties.put("mail.smtp.host", "smtp.gmail.com");
    properties.put("mail.smtp.port", "465");
    properties.put("mail.smtp.auth", "true");
    properties.put("mail.smtp.socketFactory.port", "465");
    properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

    session = Session.getInstance(properties, new javax.mail.Authenticator() {
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(senderAddress, senderAuthPhrase);
      }
    });
  }

  /**
   * Sent email boolean.
   *
   * @param recepientAddress the recepient address
   * @param subject          the subject
   * @param rawMessage       the raw message
   * @return the boolean
   */
  public boolean sentEmail(String recepientAddress, String subject, String rawMessage) {
    boolean status = false;
    if (senderAuthPhrase == null) return status;

    try {
      Message message = new MimeMessage(session);
      message.setFrom(new InternetAddress("from@gmail.com"));
      message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recepientAddress));
      message.setSubject(subject);
      message.setText(rawMessage);

      Transport.send(message);

      status = true;
    } catch (MessagingException e) {
      Helper.logger("EmailService.sendEmail", "Failed to send email");
    }

    return status;
  }

  /**
   * Sent registration email boolean.
   *
   * @param recepientName    the recepient name
   * @param recepientAddress the recepient address
   * @return the boolean
   */
  public boolean sentRegistrationEmail(String recepientName, String recepientAddress) {
    String subject = "Welcome to MOBLIMA";

    String body = "Hello, " + recepientName + ",\n";
    body += "Welcome to MOBLIMA! You have successfully created an account.\n";
    body += "Start exploring the latest movies in theaters and book your seats to watch!\n";

    System.out.println("Processing registration . . .");
    return this.sentEmail(recepientAddress, subject, body);
  }

  /**
   * Sent booking email boolean.
   *
   * @param recepientName    the recepient name
   * @param recepientAddress the recepient address
   * @param bookingDetails   the booking details
   * @return the boolean
   */
  public boolean sentBookingEmail(String recepientName, String recepientAddress, String bookingDetails) {
    String subject = "Booking Summary";

    String body = "Hi, " + recepientName + ", thank you for booking with MOBLIMA.\n";
    body += "This email is a confirmation of your booking.\n\n";
    body += bookingDetails;

    System.out.println("Processing booking . . .");
    return this.sentEmail(recepientAddress, subject, body);
  }
}
