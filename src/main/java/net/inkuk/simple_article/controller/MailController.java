package net.inkuk.simple_article.controller;

import net.inkuk.simple_article.database.DataBaseClientPool;
import net.inkuk.simple_article.util.Log;
import net.inkuk.simple_article.util.EMailService;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.*;


@RestController
public class MailController {

    public static class CodeAt{

        private final Date at;
        private final long code;

        public CodeAt(long code) {

            this.at = new Date();
            this.code = code;
        }

        public boolean isMatch(long code){

            return (this.code == code);
        }

        public boolean isExpired(){

            Date now = new Date();

            long span = now.getTime() - this.at.getTime();

            return (span > 1000 * 60 * 60);
        }
    }


    private final Map<String, CodeAt> hashCodeAt = new HashMap<String, CodeAt>();
    private final EMailService emailService;

    public MailController(EMailService emailService) {

        this.emailService = emailService;
    }


    public long createCode() {

        return (long)(Math.random() * (90000)) + 100000; //(long) Math.random() * (최댓값-최소값+1) + 최소값
    }

    @PostMapping("/verifyEmail")
    public ResponseEntity<?> postVerifyEmail(@RequestBody @NotNull Map<String, String> payload) {

        Log.debug("asdffasd");

        String email = payload.get("email");

        if(email == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        long code = createCode();
        boolean success = this.emailService.sendEMail(email, code);

        if(success) {
            this.hashCodeAt.remove(email);
            this.hashCodeAt.put(email, new CodeAt(code));
        }

        return new ResponseEntity<>(success ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @GetMapping("/verifyEmail/{mail}/{code}")
    public ResponseEntity<?> getVerifyEmail(@PathVariable String mail, @PathVariable long code) {

        this.hashCodeAt.values().removeIf(entry -> entry.isExpired());

        CodeAt codeAt = this.hashCodeAt.get(mail);

        if(codeAt == null)
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        boolean isMatch = codeAt.isMatch(code);

        if(isMatch)
            this.hashCodeAt.remove(mail);

        return new ResponseEntity<>(Map.of("match", isMatch), HttpStatus.OK);
    }
}
