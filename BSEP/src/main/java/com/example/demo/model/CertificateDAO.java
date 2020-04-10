package com.example.demo.model;

import org.bouncycastle.asn1.x500.AttributeTypeAndValue;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;

import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Date;

public class CertificateDAO {

    public CertificateDAO() {
    }

    private  String cn;
    private  String o;
    private  String ou;
    private  String l;
    private  String c;
    private  String mail;
    private  String surname;
    private  String givenname;
    private  String uid;



    private String issuer;
    private  Date notBefore;
    private Date notAfter;
    private String extensionString;

    public CertificateDAO(String cn, String o, String ou, String l, String c, String mail, String surname, String givenname, String uid, String issuer, Date notBefore, Date notAfter, String extensionString) {
        this.cn = cn;
        this.o = o;
        this.ou = ou;
        this.l = l;
        this.c = c;
        this.mail = mail;
        this.surname = surname;
        this.givenname = givenname;
        this.uid = uid;
        this.issuer = issuer;
        this.notBefore = notBefore;
        this.notAfter = notAfter;
        this.extensionString = extensionString;
    }

    public CertificateDAO(X509Certificate certificate) throws CertificateEncodingException {
        JcaX509CertificateHolder certificateHolder = new JcaX509CertificateHolder(certificate);

        X500Name nameIssuer = new JcaX509CertificateHolder(certificate).getIssuer();
        X500Name nameSubject = new JcaX509CertificateHolder(certificate).getSubject();
        RDN[] rnds = nameSubject.getRDNs();
        for (RDN rdn: rnds) {
            AttributeTypeAndValue[] values = rdn.getTypesAndValues();
            for (AttributeTypeAndValue val : values) {
                if (val.getType().equals(BCStyle.CN)) {
                    cn = val.getValue().toString();
                } else if (val.getType().equals(BCStyle.GIVENNAME)) {
                    givenname = val.getValue().toString();
                } else if (val.getType().equals(BCStyle.SURNAME)) {
                    surname = val.getValue().toString();
                } else if (val.getType().equals(BCStyle.O)) {
                    o = val.getValue().toString();
                } else if (val.getType().equals(BCStyle.OU)) {
                    ou = val.getValue().toString();
                } else if (val.getType().equals(BCStyle.C)) {
                    c = val.getValue().toString();
                } else if (val.getType().equals(BCStyle.E)) {
                    mail = val.getValue().toString();
                }
                else if (val.getType().equals(BCStyle.UID)) {
                    uid = val.getValue().toString();
                }
            }
        }

        notBefore = certificate.getNotBefore();
        notAfter=certificate.getNotAfter();
    }


    public String getCn() {
        return cn;
    }

    public void setCn(String cn) {
        this.cn = cn;
    }

    public String getO() {
        return o;
    }

    public void setO(String o) {
        this.o = o;
    }

    public String getOU() {
        return ou;
    }

    public void setOU(String OU) {
        this.ou = OU;
    }

    public String getL() {
        return l;
    }

    public void setL(String l) {
        this.l = l;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    public String getMAIL() {
        return mail;
    }

    public void setMAIL(String MAIL) {
        this.mail = MAIL;
    }

    public String getSURNAME() {
        return surname;
    }

    public void setSURNAME(String SURNAME) {
        this.surname = SURNAME;
    }

    public String getGIVENNAME() {
        return givenname;
    }

    public void setGIVENNAME(String GIVENNAME) {
        this.givenname = GIVENNAME;
    }

    public String getUID() {
        return uid;
    }

    public void setUID(String UID) {
        this.uid = UID;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public Date getNotBefore() {
        return notBefore;
    }

    public void setNotBefore(Date notBefore) {
        this.notBefore = notBefore;
    }

    public Date getNotAfter() {
        return notAfter;
    }

    public void setNotAfter(Date notAfter) {
        this.notAfter = notAfter;
    }

    public String getExtensionString() {
        return extensionString;
    }

    public void setExtensionString(String extensionString) {
        this.extensionString = extensionString;
    }

    @Override
    public String toString() {
        return "CertificateDAO{" +
                "cn='" + cn + '\'' +
                ", O='" + o + '\'' +
                ", OU='" + ou + '\'' +
                ", L='" + l + '\'' +
                ", C='" + c + '\'' +
                ", MAIL='" + mail + '\'' +
                ", SURNAME='" + surname + '\'' +
                ", GIVENNAME='" + givenname + '\'' +
                ", UID='" + uid + '\'' +
                ", issuer='" + issuer + '\'' +
                ", notBefore=" + notBefore +
                ", notAfter=" + notAfter +
                ", extensionString='" + extensionString + '\'' +
                '}';
    }
}
