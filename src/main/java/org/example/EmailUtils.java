package org.example;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailUtils {
    public static void sendEmail(String email, String subject, String content)  throws MessagingException, SendFailedException {
        final String SMTP_HOST = Config.getConfig().emailHost;
        final String SMTP_PORT = Config.getConfig().emailPort;
        final String GMAIL_USERNAME = Config.getConfig().emailUsername;
        final String GMAIL_PASSWORD = Config.getConfig().emailPassword;

        Properties prop = System.getProperties();
        prop.setProperty("mail.smtp.starttls.enable", "true");
        prop.setProperty("mail.smtp.host", SMTP_HOST);
        prop.setProperty("mail.smtp.user", GMAIL_USERNAME);
        prop.setProperty("mail.smtp.password", GMAIL_PASSWORD);
        prop.setProperty("mail.smtp.port", SMTP_PORT);
        prop.setProperty("mail.smtp.auth", "true");
        System.out.println("Props : " + prop);

        Session session = Session.getInstance(prop, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(GMAIL_USERNAME,
                        GMAIL_PASSWORD);
            }
        });
        MimeMessage message = new MimeMessage(Session.getDefaultInstance(System.getProperties()));
        try {
            message.setFrom(GMAIL_USERNAME);
        } catch (MessagingException e) {
            throw new RuntimeException("INTERNAL SERVER ERROR");
        }
        message.addRecipient(MimeMessage.RecipientType.TO, new javax.mail.internet.InternetAddress(email));
        message.setSubject(subject);
        message.setText(content);
        message.setRecipients(MimeMessage.RecipientType.TO, email);
        Transport transport = session.getTransport("smtp");
        transport.connect(SMTP_HOST, GMAIL_USERNAME, GMAIL_PASSWORD);
        transport.sendMessage(message, message.getAllRecipients());
        transport.close();
    }
}
