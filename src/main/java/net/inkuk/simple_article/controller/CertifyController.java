package net.inkuk.simple_article.controller;

import net.inkuk.simple_article.util.EMailService;
import net.inkuk.simple_article.util.CertifiedEmail;
import net.inkuk.simple_article.util.Log;
import net.inkuk.simple_article.util.ObjectCovert;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@RestController
public class CertifyController {

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

    public CertifyController(EMailService emailService) {

        this.emailService = emailService;
    }


    public long createCode() {

        return (long)(Math.random() * (90000)) + 100000; //(long) Math.random() * (최댓값-최소값+1) + 최소값
    }



//    @PostMapping("certify/user-join")
//    @GetMapping("certify/{code}/user-join/{mail}")

//    @PostMapping("certify/password-reset")
//    @GetMapping("certify/{code}/password-reset")

//    @PostMapping("certify/user/password")
//    @PostMapping("user/password-reset")
//    public ResponseEntity<?> postVerifyail(@RequestBody @NotNull Map<String, String> payload) {
//
//        return null;
//    }

    @PostMapping("certify/user-join")
    public ResponseEntity<?> postCertifyUserJoin(@RequestBody @NotNull Map<String, String> payload) {

        final String email = payload.get("email");

        if(email == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if(email.length() > 50)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        final long code = createCode();
        final boolean success = this.emailService.sendEMail(email, code);

        if(success) {
            this.hashCodeAt.remove(email);
            this.hashCodeAt.put(email, new CodeAt(code));
        }

        return new ResponseEntity<>(success ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR);
    }


    private boolean isLong(String str) {

        if (str == null || str.trim().isEmpty())
            return false;

        try {
            Long.parseLong(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @PatchMapping("certify/user-join")
    public ResponseEntity<?> patchCertifyUserJoin(@RequestBody @NotNull Map<String, String> payload) {

        final String email = ObjectCovert.asString(payload.get("email"));

        if(email == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if(email.length() > 50)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        final String code = ObjectCovert.asString(payload.get("code"));

        if(!isLong(code))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        this.hashCodeAt.values().removeIf(CodeAt::isExpired);

        final CodeAt codeAt = this.hashCodeAt.get(email);

        if(codeAt == null)
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        final boolean isMatch = codeAt.isMatch(Long.parseLong(code));

        if(isMatch) {

            if(!CertifiedEmail.putUserJoin(email))
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            this.hashCodeAt.remove(email);
        }

        return new ResponseEntity<>(Map.of("match", isMatch), HttpStatus.OK);
    }
}
