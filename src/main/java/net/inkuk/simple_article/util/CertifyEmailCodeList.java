package net.inkuk.simple_article.util;

import java.util.*;


public class CertifyEmailCodeList {

    private final LinkedList<CertifyEmailCode> list = new LinkedList<>();

    public void add(String email, long code) {

        list.add(new CertifyEmailCode(email, code));
    }


    public void remove(String email) {

        list.removeIf(CertifyEmailCode -> CertifyEmailCode.getEMail().equals(email));

    }



    public CertifyEmailCode find(String email){

        for (CertifyEmailCode code : list) {
            if (code.getEMail().equals(email))
                return code;
        }

        return null;
    }



    public boolean isMatch(String email, long code){

        CertifyEmailCode certifyEmailCode = find(email);

        if(certifyEmailCode == null)
            return false;

        if(certifyEmailCode.isExpired())
            return false;

        return certifyEmailCode.isMatch(code);
    }


    public void setCertified(String email){

        CertifyEmailCode certifyEmailCode = find(email);

        if(certifyEmailCode != null)
            certifyEmailCode.setCertified();
    }


    public boolean isCertified(String email){

        CertifyEmailCode certifyEmailCode = find(email);

        if(certifyEmailCode == null)
            return false;

        if(certifyEmailCode.isExpired())
            return false;

        return certifyEmailCode.isCertified();
    }


    public void removeExpired(){

        list.removeIf(CertifyEmailCode::isExpired);
    }
}
