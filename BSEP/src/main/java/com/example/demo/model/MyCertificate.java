package com.example.demo.model;

import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.*;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;


public  class MyCertificate extends  X509Certificate implements Serializable {

   private  int version=3;
    private String signatureAlgorithm="md5WithRSAEncryptio";
    private int serialNumber;
    private int subjectId;
    private String issuer;
    private boolean validity;
    private  Date notBefore;
    private Date notAfter;
    private  String subject;
    private PrivateKey subjectPrivateKeyInfo;
    private PublicKey  subjectPublicKeyInfo;
    private ArrayList<MyCertificate> issuedCertificates=new ArrayList<>();
    private MyCertificate CA;
    private ArrayList<MyCertificate>path=new ArrayList<>();
    private  boolean isRoot=false;
    private Extension extension;
    private String extensionString;

    public Extension getExtension() {
        return extension;
    }

    public void setExtension(Extension extension) {
        this.extension = extension;
    }

    public boolean isRoot() {
        return isRoot;
    }

    public void setRoot(boolean root) {
        isRoot = root;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public void setSerialNumber(int serialNumber) {
        this.serialNumber = serialNumber;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public boolean isValidity() {
        return validity;
    }

    public void setValidity(boolean validity) {
        this.validity = validity;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }



    public String getSignatureAlgorithm() {
        return signatureAlgorithm;
    }

    public void setSignatureAlgorithm(String signatureAlgorithm) {
        this.signatureAlgorithm = signatureAlgorithm;
    }

    public void setNotBefore(Date notBefore) {
        this.notBefore = notBefore;
    }

    public void setNotAfter(Date notAfter) {
        this.notAfter = notAfter;
    }

    public PrivateKey getSubjectPrivateKeyInfo() {
        return subjectPrivateKeyInfo;
    }

    public void setSubjectPrivateKeyInfo(PrivateKey subjectPrivateKeyInfo) {
        this.subjectPrivateKeyInfo = subjectPrivateKeyInfo;
    }

    public void setSubjectPublicKeyInfo(PublicKey subjectPublicKeyInfo) {
        this.subjectPublicKeyInfo = subjectPublicKeyInfo;
    }

    public PublicKey getSubjectPublicKeyInfo() {
        return subjectPublicKeyInfo;
    }

    public ArrayList<MyCertificate> getIssuedCertificates() {
        return issuedCertificates;
    }

    public void setIssuedCertificates(ArrayList<MyCertificate> issuedCertificates) {
        this.issuedCertificates = issuedCertificates;
    }

    public MyCertificate getCA() {
        return CA;
    }

    public void setCA(MyCertificate CA) {
        this.CA = CA;
    }

    public ArrayList<MyCertificate> getPath() {
        return path;
    }

    public void setPath(ArrayList<MyCertificate> path) {
        this.path = path;
    }

    public String getExtensionString() {
        return extensionString;
    }

    public void setExtensionString(String extensionString) {
        this.extensionString = extensionString;
    }

    public MyCertificate() {
        this.version = 3;
        this.signatureAlgorithm="md5WithRSAEncryptio";
        KeyPair kp=generateKeys();
        this.subjectPrivateKeyInfo=kp.getPrivate();
        this.subjectPublicKeyInfo=kp.getPublic();
        this.validity=true;
    }

    public void updatePath(){
        this.path=this.CA.path;
        this.path.add(this.CA);
    }
    private KeyPair generateKeys() {
        try {
            //Generator para kljuceva
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            //Za kreiranje kljuceva neophodno je definisati generator pseudoslucajnih brojeva
            //Ovaj generator mora biti bezbedan (nije jednostavno predvideti koje brojeve ce RNG generisati)
            //U ovom primeru se koristi generator zasnovan na SHA1 algoritmu, gde je SUN provajder
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            //inicijalizacija generatora, 2048 bitni kljuc
            keyGen.initialize(2048, random);

            //generise par kljuceva koji se sastoji od javnog i privatnog kljuca
            return keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public void checkValidity() throws CertificateExpiredException, CertificateNotYetValidException {

    }

    @Override
    public void checkValidity(Date date) throws CertificateExpiredException, CertificateNotYetValidException {

    }

    @Override
    public int getVersion() {
        return 0;
    }

    @Override
    public BigInteger getSerialNumber() {
        return null;
    }

    @Override
    public Principal getIssuerDN() {
        return null;
    }

    @Override
    public Principal getSubjectDN() {
        return null;
    }

    @Override
    public Date getNotBefore() {
        return null;
    }

    @Override
    public Date getNotAfter() {
        return null;
    }

    @Override
    public byte[] getTBSCertificate() throws CertificateEncodingException {
        return new byte[0];
    }

    @Override
    public byte[] getSignature() {
        return new byte[0];
    }

    @Override
    public String getSigAlgName() {
        return null;
    }

    @Override
    public String getSigAlgOID() {
        return null;
    }

    @Override
    public byte[] getSigAlgParams() {
        return new byte[0];
    }

    @Override
    public boolean[] getIssuerUniqueID() {
        return new boolean[0];
    }

    @Override
    public boolean[] getSubjectUniqueID() {
        return new boolean[0];
    }

    @Override
    public boolean[] getKeyUsage() {
        return new boolean[0];
    }

    @Override
    public int getBasicConstraints() {
        return 0;
    }

    @Override
    public byte[] getEncoded() throws CertificateEncodingException {
        return new byte[0];
    }

    @Override
    public void verify(PublicKey key) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {

    }

    @Override
    public void verify(PublicKey key, String sigProvider) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {

    }

    @Override
    public String toString() {
        return null;
    }

    @Override
    public PublicKey getPublicKey() {
        return null;
    }

    @Override
    public boolean hasUnsupportedCriticalExtension() {
        return false;
    }

    @Override
    public Set<String> getCriticalExtensionOIDs() {
        return null;
    }

    @Override
    public Set<String> getNonCriticalExtensionOIDs() {
        return null;
    }

    @Override
    public byte[] getExtensionValue(String oid) {
        return new byte[0];
    }
}
