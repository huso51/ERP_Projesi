/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erprest.connection;

import com.erprest.model.EmailConnection;
import com.erprest.model.ResponseData;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author caner
 */
public class EMailConnectionHelper {

    Session session;
    Properties props = new Properties();
    EmailConnection emailConnection;

    public EMailConnectionHelper() {
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.authentication.required", "true");
        props.put("mail.smtp.host", "smtp.yandex.com.tr");
        props.put("mail.smtp.ssl.trust", "smtp.yandex.com.tr");
        props.put("mail.smtp.port", "587");

        emailConnection = new EmailConnection();
        emailConnection.setUsername("java@java.com.tr");
        emailConnection.setPassword("19992002");
        emailConnection.setHost("smtp.yandex.com.tr");
        emailConnection.setPort(587);
        
        session = Session.getInstance(props,
                new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("java@java.com.tr", "19992002");
            }
        });
    }

    public EMailConnectionHelper(final EmailConnection emailConnection) {
        this.emailConnection = emailConnection;

        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.authentication.required", "true");
        props.put("mail.smtp.host", emailConnection.getHost());
        props.put("mail.smtp.ssl.trust", emailConnection.getHost());
        props.put("mail.smtp.port", ""+emailConnection.getPort());

        session = Session.getInstance(props,
                new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailConnection.getUsername(), emailConnection.getPassword());
            }
        });
    }

    public Response sendPassword(String emailTo, String redirectUrl) {
        try {
            String baslik = "Hoşgeldiniz";
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(emailConnection.getUsername(), "Java Erp Portal"));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(emailTo));
            message.setSubject(baslik);
            message.setText(redirectUrl);

            Transport.send(message);

        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return Response.ok().build();
    }

    public void sendTestMessage(String emailTo) {
        try {
            String baslik = "Email test mesajı";
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(emailConnection.getUsername(), "Java Erp Portal"));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(emailTo));
            message.setSubject(baslik);
            message.setText("Bu bir email test mesajıdır.");

            Transport.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new WebApplicationException(e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    public Response sendtoCustomer(String tenantFrom,String emailTo, byte[] zipByte) {
        try {
            String baslik = "Fatura Detayı";
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(emailConnection.getUsername(), tenantFrom));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(emailTo));
            message.setSubject(baslik);
            message.setText("Fatura detayınız ekte bulunmaktadır.");
            Multipart multipart = new MimeMultipart();
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setDataHandler(new DataHandler(zipByte, "application/octet-stream"));
            messageBodyPart.setFileName("fatura.zip");
            multipart.addBodyPart(messageBodyPart);

            message.setContent(multipart);
            Transport.send(message);

        } catch (MessagingException | IOException e) {
            throw new RuntimeException(e);
        }
        return Response.ok().build();

    }

    public static boolean isValidEmailAddress(String email) {
        boolean result = true;
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            result = false;
        }
        return result;
    }
}
