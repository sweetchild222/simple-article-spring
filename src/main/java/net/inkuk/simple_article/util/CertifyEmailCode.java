package net.inkuk.simple_article.util;

import java.time.Instant;

public class CertifyEmailCode {

    private final String email;
    private final long code;
    private final long timestamp;
    private boolean certified = false;


    public CertifyEmailCode(String email, long code) {

        this.email = email;
        this.code = code;
        this.timestamp = Instant.now().toEpochMilli();
        this.certified = false;
    }


    public String getEMail(){

        return this.email;
    }


    public boolean isMatch(long code){

        return this.code == code;
    }


    public boolean isExpired(){

        long timestamp = Instant.now().toEpochMilli();

        long validTimespan = 1000 * 60 * 60; //1 hour
        //long validTimespan = 1000 * 20; //20 sec

        return (timestamp - this.timestamp) > validTimespan;
    }


    public void setCertified() {

        this.certified = true;
    }


    public boolean isCertified() {

        return this.certified;
    }
}
