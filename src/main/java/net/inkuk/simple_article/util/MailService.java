package net.inkuk.simple_article.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender javaMailSender;
    private static final String senderEmail= "sweetchild22.ik@gmail.com";

    public MimeMessage createMail(String mail, long number) {

        MimeMessage message = javaMailSender.createMimeMessage();

        try {

            String body = "<h3>" + "요청하신 인증 번호입니다." + "</h3>";
            body += "<h1>" + number + "</h1>";
            body += "<h3>" + "감사합니다." + "</h3>";

            message.setFrom(senderEmail);
            message.setRecipients(MimeMessage.RecipientType.TO, mail);
            message.setSubject("이메일 인증");
            message.setText(body,"UTF-8", "html");

            return message;

        } catch (MessagingException e) {

            Log.error(e.toString());
            return null;
        }
    }

    public boolean sendMail(String mail, long number) {

        MimeMessage message = createMail(mail, number);

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

