package com.example.demo.service;


import com.example.demo.model.Admin;
import com.example.demo.model.AliasCA;
import com.example.demo.model.IssuerData;
import com.example.demo.model.SubjectData;
import com.example.demo.repository.AdminRepository;
import com.example.demo.repository.AliasCARepository;
import com.itextpdf.text.DocumentException;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private AliasCARepository aliasCARepository;

    @Override
    public Collection<Admin> findAll() {
        return adminRepository.findAll();
    }

    @Override
    public Admin findById(Long id) {
        return adminRepository.findById(id).orElseGet(null);
    }

    @Override
    public Admin findByEmail(String email) {
        return adminRepository.findByEmail(email);
    }

    //prilikom registracije admina kreiramo root sertifikat
    @Override
    public Admin create(Admin admin) throws Exception {
        Admin ret = new Admin();
        ret.copyValues(admin);
        ret = adminRepository.save(ret);


        //kreiranje novog para kljuceva za root
        KeyPair keyPairIssuer = generateKeyPair();

        //kreiranje root sertifikata
        X509Certificate novi=napraviRoot(keyPairIssuer);

        //kreiranje keyStore file za upis CA sertifikata
        KeyStoreWriter ksw=new KeyStoreWriter();
        char[] pass= new char[3];
        pass[0]='1';
        pass[1]='2';
        pass[2]='3';

        ksw.loadKeyStore(null,pass);
        ksw.writeCertificate("CARoot", novi); //upis sertifikata
       // ksw.write("CaRoot", keyPairIssuer.getPrivate(), pass, novi); //upis
        ksw.saveKeyStore("keyStoreCA.jks", pass); //CA sertifikate cuvamo u ovom file-u

        //kreiranje keyStore file za upis privatnog kljuca CA sertifikata
        KeyStoreWriter ksw1=new KeyStoreWriter();
        char[] pass1= new char[3];
        pass1[0]='1';
        pass1[1]='3';
        pass1[2]='3';

        ksw1.loadKeyStore(null,pass1);
        ksw1.write("CARoot", keyPairIssuer.getPrivate(), pass, novi); //upis kljuca
        ksw1.saveKeyStore("keyStoreCAPrivatni.jks", pass); //priv. kljuceve CA sertifikata cuvamo u ovom file-u

        //kreiranje end-entity key store files
        createEndEntityKSFiles();


        //cuvanje spiska aliasa u bazi
        aliasCARepository.save(new AliasCA("CARoot","CARoot"));

        //citanje i ispis kreiranog sertifikata na kozolu
        KeyStoreReader ksr=new KeyStoreReader();
        Certificate certificate=ksr.readCertificate("keyStoreCA.jks","123","CARoot");
        System.out.println(certificate);


        return ret;
    }

    private void createEndEntityKSFiles() {

        //kreiranje keyStore file za upis EE sertifikata
        KeyStoreWriter ksw=new KeyStoreWriter();
        char[] pass= new char[3];
        pass[0]='1';
        pass[1]='2';
        pass[2]='3';

        ksw.loadKeyStore(null,pass);
        ksw.saveKeyStore("keyStoreEE.jks", pass); //EE sertifikate cuvamo u ovom file-u

        //kreiranje keyStore file za upis privatnog kljuca EE sertifikata
        KeyStoreWriter ksw1=new KeyStoreWriter();
        char[] pass1= new char[3];
        pass1[0]='1';
        pass1[1]='3';
        pass1[2]='3';

        ksw1.loadKeyStore(null,pass1);
        ksw1.saveKeyStore("keyStoreEEPrivatni.jks", pass); //priv. kljuceve EE sertifikata cuvamo u ovom file-u



    }

    private X509Certificate napraviRoot(KeyPair keyPair) throws NoSuchProviderException, CertificateException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, FileNotFoundException, DocumentException {
        // u slucaju roota ce se sopstveni privatni kljuc koristiti za potpisivanje

        // kreiranje podataka o subjektu
        SubjectData subjectData = generateSubjectData(keyPair);
        // kreiranje podataka o issueru
        IssuerData issuerData = generateIssuerData(keyPair.getPrivate());

        //Generise se sertifikat za subjekta, potpisan od strane issuer-a
        CertificateGenerator cg = new CertificateGenerator();
        X509Certificate cert = cg.generateCertificate(subjectData, issuerData,"CA");

        System.out.println("\n===== Podaci o izdavacu sertifikata =====");
        System.out.println(cert.getIssuerX500Principal().getName());
        System.out.println("\n===== Podaci o vlasniku sertifikata =====");
        System.out.println(cert.getSubjectX500Principal().getName());
        System.out.println("\n===== Sertifikat =====");
        System.out.println("-------------------------------------------------------");
        System.out.println(cert);
        System.out.println("-------------------------------------------------------");

        //Moguce je proveriti da li je digitalan potpis sertifikata ispravan, upotrebom javnog kljuca izdavaoca
        cert.verify(keyPair.getPublic());
        System.out.println("\nValidacija uspesna :)");

        PDFGenerator pdfGen = new PDFGenerator();
        pdfGen.createCertificatePDF(cert);


        System.out.println("Non critical: " + cert.getNonCriticalExtensionOIDs());

        return  cert;

    }
    private IssuerData generateIssuerData(PrivateKey issuerKey) {
        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        builder.addRDN(BCStyle.CN, "SOFTWARE ROOT");
        builder.addRDN(BCStyle.SURNAME, "Tim");
        builder.addRDN(BCStyle.GIVENNAME, "25");
        builder.addRDN(BCStyle.O, "UNS-FTN");
        builder.addRDN(BCStyle.OU, "Katedra za informatiku");
        builder.addRDN(BCStyle.C, "RS");
        builder.addRDN(BCStyle.E, "tim25@uns.ac.rs");
        //UID (USER ID) je ID korisnika
        builder.addRDN(BCStyle.UID, "25");

        //Kreiraju se podaci za issuer-a, sto u ovom slucaju ukljucuje:
        // - privatni kljuc koji ce se koristiti da potpise sertifikat koji se izdaje
        // - podatke o vlasniku sertifikata koji izdaje nov sertifikat
        return new IssuerData(issuerKey, builder.build());
    }

    private SubjectData generateSubjectData(KeyPair keyPairSubject) {
        try {
           //NEEE!!!!!
           // KeyPair keyPairSubject = generateKeyPair();

            //Datumi od kad do kad vazi sertifikat
            SimpleDateFormat iso8601Formater = new SimpleDateFormat("yyyy-MM-dd");
            Date startDate = iso8601Formater.parse("2017-12-31");
            Date endDate = iso8601Formater.parse("2022-12-31");

            //Serijski broj sertifikata
            String sn="1";
            //klasa X500NameBuilder pravi X500Name objekat koji predstavlja podatke o vlasniku
            X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
            builder.addRDN(BCStyle.CN, "SOFTWARE ROOT");
            builder.addRDN(BCStyle.SURNAME, "Tim");
            builder.addRDN(BCStyle.GIVENNAME, "25");
            builder.addRDN(BCStyle.O, "UNS-FTN");
            builder.addRDN(BCStyle.OU, "Katedra za informatiku");
            builder.addRDN(BCStyle.C, "RS");
            builder.addRDN(BCStyle.E, "tim25@uns.ac.rs");
            //UID (USER ID) je ID korisnika
            builder.addRDN(BCStyle.UID, "25");

            //Kreiraju se podaci za sertifikat, sto ukljucuje:
            // - javni kljuc koji se vezuje za sertifikat
            // - podatke o vlasniku
            // - serijski broj sertifikata
            // - od kada do kada vazi sertifikat
            return new SubjectData(keyPairSubject.getPublic(), builder.build(), sn, startDate, endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private KeyPair generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            keyGen.initialize(2048, random);
            return keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public Admin update(Admin admin) throws Exception {
        Admin adminKlinikeZaIzmenu = findById(admin.getId());
        adminKlinikeZaIzmenu.copyValues(admin);
        adminKlinikeZaIzmenu = adminRepository.save(adminKlinikeZaIzmenu);
        return adminKlinikeZaIzmenu;
    }

    @Override
    public void delete(Long id) {
        adminRepository.deleteById(id);
    }

    @Override
    public void deleteByEmail(String email) {
        Admin zaObrisati = findByEmail(email);
        adminRepository.deleteById(zaObrisati.getId());
    }
}
