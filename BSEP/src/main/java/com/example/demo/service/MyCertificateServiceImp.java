package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.repository.AdminRepository;
import com.example.demo.repository.AliasCARepository;
import com.example.demo.repository.AliasEERepository;
import com.example.demo.repository.PovuceniReposiitory;
import com.itextpdf.text.DocumentException;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.io.*;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

@Service
public class MyCertificateServiceImp implements MyCertificateService {


    private  KeyStore keyStore;
    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private AliasCARepository aliasCARepository;

    @Autowired
    private AliasEERepository aliasEERepository;

    @Autowired
    private PovuceniReposiitory povuceniReposiitory;

    @Override
    public Collection<MyCertificate> findAll() {
        return null;
    }

    //kreiranje sertifikata
    @Override
    public boolean createCA(CertificateDAO certificateDAO) throws Exception {
        // poziv metode koja vrsi pretragu i iscitavanje odabranog issuera na osnovu aliasa
        X509Certificate issuer = searchByAlias(certificateDAO.getIssuer());

        //provera validnosti datuma
        if(!datumValidan(certificateDAO, issuer)){
            return false;
        }

        //generisanje kljuceva za novi sertifikat
        KeyPair keyPairSubject = generateKeyPair();

        //poziva se metoda koja vraca kreirani sertifikat
        X509Certificate novi=napraviSertifikat(certificateDAO,keyPairSubject);

        //otvaranje i cuvanje sertifikata keystore za ca
        KeyStoreWriter ksw=new KeyStoreWriter();
        char[] pass= new char[3];
        pass[0]='1';
        pass[1]='2';
        pass[2]='3';

        /* OTKOMENTARISATI NAKON PROMENE U COMBOBOXU NA FRONTU (dodati ca) I ZAMENITI STRINGOVE GDE SU SADA NAZIVI FAJLOVA ISPOD i prefiks aliasa
        //sertifikate upisujemo u EE fajlove, osim ako u ekstenziji prosledjenoj sa fronta ne pise CA*/
        String ksfZaUpisPrivKljuca = new String("keyStoreEEPrivatni.jks");
        String ksfZaUpisSertifikata = new String("keyStoreEE.jks");
        String aliasPrefix = new String("EE");
        if(certificateDAO.getExtensionString().contains("CA")){
            ksfZaUpisPrivKljuca = new String("keyStoreCAPrivatni.jks");
            ksfZaUpisSertifikata = new String("keyStoreCA.jks");
            aliasPrefix = new String("CA");
        }

        //i isto uslov dodati da odredi da li se upisuje u aliasCArepository ili aliasEErep




        ksw.loadKeyStore(ksfZaUpisSertifikata, pass);
        ksw.writeCertificate(aliasPrefix + certificateDAO.getUID(), novi);
        //ksw.write("CA "+certificateDAO.getUID(),keyPairSubject.getPrivate(),pass,novi); ne treba ovo je kljuc
        ksw.saveKeyStore(ksfZaUpisSertifikata, pass);

        if(certificateDAO.getExtensionString().contains("CA")){
             aliasCARepository.save(new AliasCA(aliasPrefix + certificateDAO.getUID(),certificateDAO.getIssuer()));
        } else {
             aliasEERepository.save(new AliasEE(aliasPrefix + certificateDAO.getUID(),certificateDAO.getIssuer()));
        }

        //otvaranje i cuvanje privatno kljuca keystore za ca
        KeyStoreWriter ksw1=new KeyStoreWriter();
        char[] pass1= new char[3];
        pass1[0]='1';
        pass1[1]='2';
        pass1[2]='3';

        ksw1.loadKeyStore(ksfZaUpisPrivKljuca,pass1);
        ksw1.write(aliasPrefix + certificateDAO.getUID(),keyPairSubject.getPrivate(),pass,novi);
        ksw1.saveKeyStore(ksfZaUpisPrivKljuca,pass);

        KeyStoreReader ksr = new KeyStoreReader();

        //iscitavanje novog sertifikata iz ks fajla i ispis na konzolu
        //Certificate certificate1=ksr.readCertificate("keyStoreCA.jks","123","CA "+certificateDAO.getUID());
        //System.out.println(certificate1);

        return true;
    }


    //metoda koja vraca true ako su datumi validni
    private boolean datumValidan(CertificateDAO certificateDAO, X509Certificate issuer) {

        Date issuerNotBefore = issuer.getNotBefore();
        Date issuerNotAfter = issuer.getNotAfter();
        Date certNotBefore = certificateDAO.getNotBefore();
        Date certNotAfter = certificateDAO.getNotAfter();

            //da li su datumi novog cert u okviru datuma issuera?
        if((issuerNotBefore.compareTo(certNotBefore) <= 0) && (issuerNotAfter.compareTo(certNotAfter) >= 0)){
            //datum 1 se desava kasnije od datuma 2 vraca pozitivan broj
            //datum 1 se desava pre od datuma 2 vraca manje od nule
            //isti datumi vracaju 0

            //da li su datumi novog cert validni?
            if(certNotBefore.compareTo(certNotAfter) <= 0){
                return true;
            }

            return false;
        }

        return false;
    }

    private X509Certificate napraviSertifikat(CertificateDAO certificateDAO, KeyPair keyPairSubject) throws NoSuchProviderException, CertificateException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, IOException {

        // kreiranje subject podataka od podataka poslatih sa fronta i generisanih kljuceva
        SubjectData subjectData = generateSubjectData(certificateDAO, keyPairSubject);

        // poziv metode koja vrsi pretragu i iscitavanje odabranog issuera na osnovu aliasa
        X509Certificate issuer = searchByAlias(certificateDAO.getIssuer());

        // generisanje issuer podataka
        IssuerData issuerData = generateIssuerData(certificateDAO);

        // Generise se sertifikat za subjekta, potpisan od strane issuer-a
        CertificateGenerator cg = new CertificateGenerator();
        X509Certificate cert = cg.generateCertificate(subjectData, issuerData,certificateDAO.getExtensionString());

        // System.out.println("\n===== Podaci o izdavacu sertifikata =====");
        // System.out.println(cert.getIssuerX500Principal().getName());
        // System.out.println("\n===== Podaci o vlasniku sertifikata =====");
        // System.out.println(cert.getSubjectX500Principal().getName());
        System.out.println("\n===== Sertifikat =====");
        System.out.println("-------------------------------------------------------");
        System.out.println(cert);
        System.out.println("-------------------------------------------------------");

        // Moguce je proveriti da li je digitalan potpis sertifikata ispravan, upotrebom javnog kljuca izdavaoca
        cert.verify(issuer.getPublicKey());

        System.out.println("\nValidacija uspesna :)");


        System.out.println("Non critical: " + cert.getNonCriticalExtensionOIDs());
        return  cert;

    }

    //pretraga i iscitavanje CA sertifikata na osnovu aliasa
    public X509Certificate searchByAlias(String alias){

        KeyStoreReader ksr=new KeyStoreReader();
        X509Certificate certificate= ksr.readCertificate("keyStoreCA.jks","123",alias);
        //System.out.println(certificate);
        return certificate;

    }

    //pretraga i iscitavanje CA privatnog kljuca na osnovu aliasa
    private PrivateKey searchPrivateKeyByAlias(String alias){
        KeyStoreReader ksr=new KeyStoreReader();
        PrivateKey k=ksr.readPrivateKey("keyStoreCAPrivatni.jks","123",alias,"123");
        return  k;
    }

    private IssuerData generateIssuerData(CertificateDAO dao) throws IOException {
        X509Certificate issuer = searchByAlias(dao.getIssuer());
        PrivateKey issuerKey = searchPrivateKeyByAlias(dao.getIssuer());
        String ime = issuer.getIssuerX500Principal().getName();

        //Kreiraju se podaci za issuer-a, sto u ovom slucaju ukljucuje:
        // - privatni kljuc koji ce se koristiti da potpise sertifikat koji se izdaje
        // - podatke o vlasniku sertifikata koji izdaje nov sertifikat
        return new IssuerData(issuerKey, new X500Name(ime));
    }


    private SubjectData generateSubjectData(CertificateDAO dao,KeyPair keyPairSubject) {

        Date startDate = dao.getNotBefore();
        Date endDate = dao.getNotAfter();

        //Serijski broj sertifikata
        String sn=dao.getUID();
        //klasa X500NameBuilder pravi X500Name objekat koji predstavlja podatke o vlasniku
        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        builder.addRDN(BCStyle.CN, dao.getCn());
        builder.addRDN(BCStyle.SURNAME, dao.getSURNAME());
        builder.addRDN(BCStyle.GIVENNAME, dao.getGIVENNAME());
        builder.addRDN(BCStyle.O, dao.getO());
        builder.addRDN(BCStyle.OU, dao.getOU());
        builder.addRDN(BCStyle.C, dao.getC());
        builder.addRDN(BCStyle.E, dao.getMAIL());
        //UID (USER ID) je ID korisnika
        builder.addRDN(BCStyle.UID, dao.getUID());

        //Kreiraju se podaci za sertifikat, sto ukljucuje:
        // - javni kljuc koji se vezuje za sertifikat
        // - podatke o vlasniku
        // - serijski broj sertifikata
        // - od kada do kada vazi sertifikat
        return new SubjectData(keyPairSubject.getPublic(), builder.build(), sn, startDate, endDate);

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
    public MyCertificate createRoot(CertificateDAO certificate) throws Exception {
        return null;
    }

    @Override
    public MyCertificate revoke(MyCertificate certificate) throws Exception {
        return null;
    }

    @Override
    public void addToKeyStore(MyCertificate certificate, String keyStoreFile) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {

    }

    //vraca spisak aliasa iz baze
    @Override
    public List<String> naziviSertifikata() {
        List<AliasCA> aliasi=aliasCARepository.findAll();
        List<String> imenaAliasa=new ArrayList<>();
        for (AliasCA a: aliasi) {
            String uid = a.getAlias().replace("CA","");
            //ako je validan dodaj ga u listu
            if (validacijaCA(uid))
                imenaAliasa.add(a.getAlias());
        }
        return imenaAliasa;
    }

    @Override
    public List<String> naziviEESertifikata() {
        List<AliasEE> aliasi=aliasEERepository.findAll();
        List<String> imenaAliasa=new ArrayList<>();
        for (AliasEE a:
                aliasi) {
            imenaAliasa.add(a.getAlias());
        }
        return imenaAliasa;
    }
    @Override
    public List<String> naziviPovucenihSertifikata() {
        List<PovuceniAliasi> aliasi=povuceniReposiitory.findAll();
        List<String> imenaAliasa=new ArrayList<>();
        for (PovuceniAliasi a:
                aliasi) {
            imenaAliasa.add(a.getAlias());
        }
        return imenaAliasa;
    }

    @Override
    public ArrayList<CertificateDAO> vratiSveCA()throws KeyStoreException, NoSuchProviderException
    {
        KeyStoreReader ksr=new KeyStoreReader();
        System.out.println("udjoh");
        ArrayList<CertificateDAO> allCertificates = new ArrayList<CertificateDAO>();
        readAllCACertificates("keyStoreCA.jks","123").forEach(certif -> {

            try {
                allCertificates.add(new CertificateDAO(certif));
            } catch (CertificateEncodingException e) {
                e.printStackTrace();
            }
        });
        return allCertificates;

    }

    public ArrayList<CertificateDAO> vratiSveEE()throws KeyStoreException, NoSuchProviderException
    {
        KeyStoreReader ksr=new KeyStoreReader();
        System.out.println("udjoh");
        ArrayList<CertificateDAO> allCertificates = new ArrayList<CertificateDAO>();
        readAllEECertificates("keyStoreEE.jks","123").forEach(certif -> {

            try {
                allCertificates.add(new CertificateDAO(certif));
            } catch (CertificateEncodingException e) {
                e.printStackTrace();
            }
        });
        return allCertificates;

    }

    @Override
    public void skiniCa(String uid) throws IOException, DocumentException, CertificateEncodingException {
        KeyStoreReader ksr=new KeyStoreReader();
        X509Certificate cer=ksr.readCertificate("keyStoreCA.jks","123","CA"+uid);
        PDFGenerator pdfGenerator=new PDFGenerator();
        pdfGenerator.createCertificatePDF(cer);

    }

    @Override
    public void skiniEE(String uid) throws IOException, DocumentException, CertificateEncodingException {
        KeyStoreReader ksr=new KeyStoreReader();
        X509Certificate cer=ksr.readCertificate("keyStoreEE.jks","123","EE"+uid);
        PDFGenerator pdfGenerator=new PDFGenerator();
        pdfGenerator.createCertificatePDF(cer);
    }

    public List<X509Certificate> readAllCACertificates(String file, String password) throws KeyStoreException, NoSuchProviderException {
        KeyStoreReader ksr=new KeyStoreReader();
        keyStore = KeyStore.getInstance("JKS", "SUN");

        List<X509Certificate> certificates = new ArrayList<>();
        List<AliasCA> aliasiCA=aliasCARepository.findAll();
        List<String> aliasiImena=new ArrayList<>();
        for (AliasCA a:aliasiCA) {
            aliasiImena.add(a.getAlias());
        }

        try {
            keyStore.load(new FileInputStream(file),password.toCharArray());
            //<String> aliasi =aliasiImena;//NEMOJ DA ZABORVIS DA POZOVES AUT

            for (String ime:aliasiImena)

            {

                certificates.add(ksr.readCertificate(file,"123",ime));


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
		/*certificates.forEach(c-> {
			System.out.println(c.getSerialNumber());
		});*/
        return certificates;
    }

    public List<X509Certificate> readAllEECertificates(String file, String password) throws KeyStoreException, NoSuchProviderException {
        KeyStoreReader ksr=new KeyStoreReader();
        keyStore = KeyStore.getInstance("JKS", "SUN");

        List<X509Certificate> certificates = new ArrayList<>();
        List<AliasEE> aliasiEE=aliasEERepository.findAll();
        List<String> aliasiImena=new ArrayList<>();
        for (AliasEE a:aliasiEE) {
            aliasiImena.add(a.getAlias());
        }

        try {
            keyStore.load(new FileInputStream(file),password.toCharArray());
            //<String> aliasi =aliasiImena;//NEMOJ DA ZABORVIS DA POZOVES AUT

            for (String ime:aliasiImena)

            {

                certificates.add(ksr.readCertificate(file,"123",ime));


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
		/*certificates.forEach(c-> {
			System.out.println(c.getSerialNumber());
		});*/
        return certificates;
    }

    @Override
    public void povuciCertificateCA(String uid) {
        KeyStoreReader ksr=new KeyStoreReader();
        X509Certificate cer=ksr.readCertificate("keyStoreCA.jks","123","CA"+uid);

        PovuceniAliasi povuceni = new PovuceniAliasi("CA" + uid);
        povuceniReposiitory.save(povuceni);

        aliasCARepository.delete(aliasCARepository.findByAlias("CA" + uid));
        rekurzivnoPovlacenjeCer("CA"+uid,uid);

    }


    @Override
    public void povuciCertificateEE(String uid) {

        KeyStoreReader ksr=new KeyStoreReader();
        X509Certificate cer=ksr.readCertificate("keyStoreEE.jks","123","EE"+uid);

        PovuceniAliasi povuceni = new PovuceniAliasi("EE" + uid);
        povuceniReposiitory.save(povuceni);

        aliasEERepository.delete(aliasEERepository.findByAlias("EE" + uid));
    }

    public void rekurzivnoPovlacenjeCer(String CA,String uid){
        ArrayList<AliasEE> podredjeniEE = aliasEERepository.findByAliasIssuer(CA);

        if(!podredjeniEE.isEmpty()){
            for (AliasEE ee:podredjeniEE) {
                String uidStr=ee.getAlias().replace("EE","");
                povuciCertificateEE(uidStr);

            }

        }
        ArrayList<AliasCA>podredjeniCA=aliasCARepository.findByAliasIssuer(CA);

        if(!podredjeniCA.isEmpty()){
            for (AliasCA ca:podredjeniCA) {
                String uidStr=ca.getAlias().replace("CA","");
                KeyStoreReader ksr=new KeyStoreReader();
                X509Certificate cer=ksr.readCertificate("keyStoreCA.jks","123","CA"+uidStr);

                PovuceniAliasi povuceni = new PovuceniAliasi("CA" + uidStr);
                povuceniReposiitory.save(povuceni);

                aliasCARepository.delete(aliasCARepository.findByAlias("CA" + uidStr));
                rekurzivnoPovlacenjeCer(ca.getAlias(),uidStr);
            }
        }

    }


    @Override
    public ArrayList<CertificateDAO> vratiSvePovucene() throws NoSuchProviderException, KeyStoreException {
        System.out.println("udjoh");
        ArrayList<CertificateDAO> allCertificates = new ArrayList<CertificateDAO>();

        readAllPovuceneCACertificates("keyStoreCA.jks","123").forEach(certif -> {
            try {
                allCertificates.add(new CertificateDAO(certif));
            } catch (CertificateEncodingException e) {
                e.printStackTrace();
            }
        });

        readAllPovuceneEECertificates("keyStoreEE.jks","123").forEach(certif -> {
            try {
                allCertificates.add(new CertificateDAO(certif));
            } catch (CertificateEncodingException e) {
                e.printStackTrace();
            }
        });
        return allCertificates;
    }



    public List<X509Certificate> readAllPovuceneCACertificates(String file, String password) throws KeyStoreException, NoSuchProviderException {
        KeyStoreReader ksr = new KeyStoreReader();
        keyStore = KeyStore.getInstance("JKS", "SUN");

        List<X509Certificate> certificates = new ArrayList<>();

        List<PovuceniAliasi> povuceniAliasis = povuceniReposiitory.findAll();
        List<String> povuceniAliasiCA = new ArrayList<>();

        for (PovuceniAliasi povuceniAliasi : povuceniAliasis) {
            if (povuceniAliasi.getAlias().contains("CA")) {
                povuceniAliasiCA.add(povuceniAliasi.getAlias());
            }
        }

        try {
            keyStore.load(new FileInputStream(file),password.toCharArray());
            //<String> aliasi =aliasiImena;//NEMOJ DA ZABORVIS DA POZOVES AUT
            for (String ime : povuceniAliasiCA)
            {
                certificates.add(ksr.readCertificate(file,"123", ime));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
		/*certificates.forEach(c-> {
			System.out.println(c.getSerialNumber());
		});*/
        return certificates;
    }


    public List<X509Certificate> readAllPovuceneEECertificates(String file, String password) throws KeyStoreException, NoSuchProviderException {
        KeyStoreReader ksr=new KeyStoreReader();
        keyStore = KeyStore.getInstance("JKS", "SUN");

        List<X509Certificate> certificates = new ArrayList<>();

        List<PovuceniAliasi> povuceniAliasis = povuceniReposiitory.findAll();
        List<String> povuceniAliasiEE = new ArrayList<>();

        for (PovuceniAliasi povuceniAliasi : povuceniAliasis) {
            if (povuceniAliasi.getAlias().contains("EE")) {
                povuceniAliasiEE.add(povuceniAliasi.getAlias());
            }
        }

        try {
            keyStore.load(new FileInputStream(file),password.toCharArray());
            //<String> aliasi =aliasiImena;//NEMOJ DA ZABORVIS DA POZOVES AUT

            for (String ime : povuceniAliasiEE)
            {
                certificates.add(ksr.readCertificate(file,"123", ime));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
		/*certificates.forEach(c-> {
			System.out.println(c.getSerialNumber());
		});*/
        return certificates;
    }

    //metoda za proveru validacije CA sertifikata
    @Override
    public boolean validacijaCA(String uid) {
        //da li je u listi povucenih aliasa?
        List<PovuceniAliasi> povuceniCA = povuceniReposiitory.findAll();

        for (PovuceniAliasi ca : povuceniCA) {
            if(ca.getAlias().contentEquals("CA"+uid)) {
                return false;
            }
        }

        //da li je datum validan?
        KeyStoreReader ksr = new KeyStoreReader();
        X509Certificate cert = ksr.readCertificate("keyStoreCA.jks","123","CA"+uid);
        Date certNotBefore = cert.getNotBefore();
        Date certNotAfter = cert.getNotAfter();
        Date today = Calendar.getInstance().getTime();

        //cert.verify(issuer.getPublicKey());


        if ( !(today.after(certNotBefore) && today.before(certNotAfter)) ){

            return false;
        }


        AliasCA certCurr = aliasCARepository.findByAlias("CA"+uid);
        String issuerAlias = certCurr.getAliasIssuer();
        X509Certificate issuer = searchByAlias(issuerAlias);

        try {
            cert.verify(issuer.getPublicKey());
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        }


        return true;
    }

    //metoda za proveru validacije EE sertifikata
    @Override
    public boolean validacijaEE(String uid) {

        //da li je u listi povucenih aliasa?
        List<PovuceniAliasi> povuceniEE = povuceniReposiitory.findAll();

        for (PovuceniAliasi ee : povuceniEE) {
            if(ee.getAlias().contentEquals("EE"+uid)) {
                return false;
            }
        }

        //da li je datum validan?
        KeyStoreReader ksr = new KeyStoreReader();
        X509Certificate cert = ksr.readCertificate("keyStoreEE.jks","123","EE"+uid);
        Date certNotBefore = cert.getNotBefore();
        Date certNotAfter = cert.getNotAfter();
        Date today = Calendar.getInstance().getTime();




        if ( !(today.after(certNotBefore) && today.before(certNotAfter)) ){

            return false;
        }

        AliasEE certEE = aliasEERepository.findByAlias("EE"+uid);
        String issuerAlias = certEE.getAliasIssuer();
        X509Certificate issuer = searchByAlias(issuerAlias);

        try {
            cert.verify(issuer.getPublicKey());
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        }


        return true;
    }

    //metoda za proveru validacije svih sertifikata

    @Override
    public boolean validacijaSvi(String izabraniAliasSvi) {

        if(izabraniAliasSvi.contains("CA")){
            String uid = izabraniAliasSvi.replace("CA","");
            return validacijaCA(uid);
        } else if(izabraniAliasSvi.contains("EE")){
            String uid = izabraniAliasSvi.replace("EE","");
            return validacijaEE(uid);
        }

        return false;
    }

}