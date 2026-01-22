package com.example.registration.mail;

import com.example.registration.servlets.Authentication;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;

public class MailSender {

    private final Session mailSession;
    private final String fromAddress;

    public MailSender(String smtpHost, int smtpPort, String username, String password, String fromAddress) {
        this.fromAddress = fromAddress;

        Properties props = new Properties();
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", "" + smtpPort);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.pool", "true");
        props.put("mail.smtp.pool.size", "3");
        props.put("mail.smtp.pool.ttl", "300000");

        Authenticator auth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                    username,
                    password
                );
            }
        };

        this.mailSession = Session.getInstance(props, auth);
    }


    public void send(String to, String subject, String text, String html) {
        try {
            MimeMessage msg = new MimeMessage(mailSession);
            msg.setFrom(new InternetAddress(fromAddress));
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            msg.setSubject(subject, "UTF-8");

            MimeBodyPart textBodyPart = new MimeBodyPart();
            textBodyPart.setContent(text, "text/plain; charset=UTF-8");

            MimeBodyPart htmlBodyPart = new MimeBodyPart();
            htmlBodyPart.setContent(html, "text/html; charset=UTF-8");

            MimeMultipart multipart = new MimeMultipart("alternative");
            multipart.addBodyPart(textBodyPart);
            multipart.addBodyPart(htmlBodyPart);

            msg.setContent(multipart);

            Transport.send(msg);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
