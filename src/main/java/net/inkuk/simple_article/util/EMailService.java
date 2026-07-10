package net.inkuk.simple_article.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EMailService {

    private final JavaMailSender javaMailSender;
    private static final String senderEmail= "sweetchild22.ik@gmail.com";

    public EMailService(JavaMailSender javaMailSender){
        this.javaMailSender = javaMailSender;
    }

    private MimeMessage createCertifyCode(String email, long code) {

        MimeMessage message = javaMailSender.createMimeMessage();

        try {

            String body = "<h3>" + "요청하신 인증 번호입니다." + "</h3>";
            body += "<h1>인증 번호 : " + code + "</h1>";
            body += "<h3>" + "인증 번호 유효 기간은 1시간 입니다. 감사합니다." + "</h3>";

            message.setFrom(senderEmail);
            message.setRecipients(MimeMessage.RecipientType.TO, email);
            message.setSubject("이메일 인증");
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

            String body = "<h3>" + "임시 비밀번호가 발급되었습니다." + "</h3>";
            body += "<h1>임시 비밀 번호 : " + password + "</h1>";
            body += "<h3>" + "감사합니다." + "</h3>";

            message.setFrom(senderEmail);
            message.setRecipients(MimeMessage.RecipientType.TO, email);
            message.setSubject("임시 비밀 번호 발급");
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

