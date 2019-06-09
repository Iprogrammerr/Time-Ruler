package com.iprogrammerr.time.ruler.email;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;

public class ConfigurableEmailServer implements EmailServer {

    private static final String CONTENT_TYPE = "text/html";
    private final String admin;
    private final Properties properties;
    private final Authenticator authenticator;

    public ConfigurableEmailServer(String admin, String password, String host, int port, boolean ssl) {
        this.admin = admin;
        this.authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(admin, password);
            }
        };
        this.properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.ssl.enable", String.valueOf(ssl));
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", String.valueOf(port));
    }

    @Override
    public void send(Email email) {
        Session session = Session.getInstance(properties, authenticator);
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(admin));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(email.recipient));
            message.setSubject(email.subject);
            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(email.text, CONTENT_TYPE);
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(mimeBodyPart);
            message.setContent(multipart);
            Transport.send(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
