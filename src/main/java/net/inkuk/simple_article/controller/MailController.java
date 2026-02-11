package net.inkuk.simple_article.controller;

import net.inkuk.simple_article.database.DataBaseClientPool;
import net.inkuk.simple_article.util.Log;
import net.inkuk.simple_article.util.EMailService;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class MailController {

    private final EMailService emailService;
    private long code = -1;
    private String email = null;

    public MailController(EMailService emailService) {

        this.emailService = emailService;
    }


    public long createCode() {

        return (long)(Math.random() * (90000)) + 100000; //(long) Math.random() * (최댓값-최소값+1) + 최소값
    }

    @PostMapping("/verifyEmail")
    public ResponseEntity<?> postVerifyEmail(@RequestBody @NotNull Map<String, String> payload) {

        this.email = payload.get("email");

        if(this.email == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        this.code = createCode();
        boolean success = this.emailService.sendEMail(this.email, this.code);

        return new ResponseEntity<>(success ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @GetMapping("/verifyEmail/{mail}/{code}")
    public ResponseEntity<?> getVerifyEmail(@PathVariable String mail, @PathVariable long code) {

        if(this.email == null || this.code == -1)
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        boolean isMatch = this.email.equals(mail) && (this.code == code);

        return new ResponseEntity<>(Map.of("match", isMatch), HttpStatus.OK);
    }
}
