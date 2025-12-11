package net.inkuk.simple_article.controller;

import net.inkuk.simple_article.database.DataBaseClientPool;
import net.inkuk.simple_article.util.MailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class MailController {

    private final MailService mailService;
    private long number = -1;
    private String mail = null;

    public MailController(MailService mailService) {

        this.mailService = mailService;
    }


    public long createNumber() {

        return (long)(Math.random() * (90000)) + 100000; //(long) Math.random() * (최댓값-최소값+1) + 최소값
    }

    @PostMapping("/mailVerify")
    public ResponseEntity<?> postMailVerify(@RequestBody Map<String, String> requestBody) {

        this.mail = requestBody.get("mail");

        if(this.mail == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        try {

            this.number = createNumber();
            boolean success = mailService.sendMail(this.mail, this.number);

            if(success)
                return new ResponseEntity<>(HttpStatus.OK);
            else
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        } catch (Exception e) {

            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/mailVerify")
    public ResponseEntity<?> getMailVerify(@RequestParam String mail, @RequestParam long number) {

        if(this.mail == null || this.number == -1)
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        boolean isMatch = this.mail.equals(mail) && (this.number == number);

        if(isMatch){

            final String sql = "update user set verified = 1 where username = '" + mail + "'";

            int affectCount = DataBaseClientPool.getClient().updateRow(sql);

            if(affectCount == -1)
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            else if(affectCount == 0)
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(Map.of("isMatched", isMatch), HttpStatus.OK);
    }
}
