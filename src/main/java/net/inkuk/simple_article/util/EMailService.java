package net.inkuk.simple_article.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
public class EMailService {

    private final JavaMailSender javaMailSender;

    public EMailService(JavaMailSender javaMailSender){
        this.javaMailSender = javaMailSender;
    }

    private MimeMessage createCertifyCode(String email, long code) {

        MimeMessage message = javaMailSender.createMimeMessage();

        try {

            String body = "<h3>" + "Here is your verification code" + "</h3>";
            body += "<h1>" + code + "</h1>";
            body += "<h3>" + "this verification code is valid for one hour" + "</h3>";

            message.setRecipients(MimeMessage.RecipientType.TO, email);
            message.setSubject("Verification code by leafstory");
            message.setText(body,"UTF-8", "html");

            return message;

        } catch (MessagingException e) {

            Log.error(e.toString());
            return null;
        }
    }

    public boolean sendCertifyCode(String email, long code) {

        MimeMessage message = createCertifyCode(email, code);

        if(message == null)
            return false;

        try {

            javaMailSender.send(message);

            return true;

        }catch (MailException e){

            Log.error(e.toString());
            return false;
        }
    }



    private MimeMessage createPassword(String email, String password) {

        MimeMessage message = javaMailSender.createMimeMessage();

        try {

            String body = "<h3>" + "Here is a temporary password" + "</h3>";
            body += "<h1>" + password + "</h1>";

            message.setRecipients(MimeMessage.RecipientType.TO, email);
            message.setSubject("Temporary password by leafstory");
            message.setText(body,"UTF-8", "html");

            return message;

        } catch (MessagingException e) {

            Log.error(e.toString());
            return null;
        }
    }


    public boolean sendPassword(String email, String password) {

        MimeMessage message = createPassword(email, password);

        if(message == null)
            return false;

        try {

            javaMailSender.send(message);

            return true;

        }catch (MailException e){

            Log.error(e.toString());
            return false;
        }
    }
}

