package com.example.demo.service;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import com.example.demo.model.IssuerData;
import com.example.demo.model.SubjectData;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.X509Extension;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;


public class CertificateGenerator {
    public CertificateGenerator() {
    }

    public X509Certificate generateCertificate(SubjectData subjectData, IssuerData issuerData, String extension) {
        try {
            //Posto klasa za generisanje sertifiakta ne moze da primi direktno privatni kljuc pravi se builder za objekat
            //Ovaj objekat sadrzi privatni kljuc izdavaoca sertifikata i koristiti se za potpisivanje sertifikata
            //Parametar koji se prosledjuje je algoritam koji se koristi za potpisivanje sertifiakta
            JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256WithRSAEncryption");
            //Takodje se navodi koji provider se koristi, u ovom slucaju Bouncy Castle
            builder = builder.setProvider("BC");

            //Formira se objekat koji ce sadrzati privatni kljuc i koji ce se koristiti za potpisivanje sertifikata
            ContentSigner contentSigner = builder.build(issuerData.getPrivateKey());

            //Postavljaju se podaci za generisanje sertifiakta
            X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(issuerData.getX500name(),
                    new BigInteger(subjectData.getSerialNumber()),
                    subjectData.getStartDate(),
                    subjectData.getEndDate(),
                    subjectData.getX500name(),
                    subjectData.getPublicKey());


            //Dodavanje ekstenzija

            if (extension.equals("CA - Certificate Authority")) {
                certGen.addExtension(Extension.basicConstraints, true, new BasicConstraints(true));
                certGen.addExtension(Extension.keyUsage, true, new KeyUsage(KeyUsage.nonRepudiation | KeyUsage.keyCertSign | KeyUsage.cRLSign));
            }
            if (extension.equals("CA, Document signing")) {
                certGen.addExtension(Extension.basicConstraints, true, new BasicConstraints(true));
                certGen.addExtension(Extension.keyUsage, true, new KeyUsage(KeyUsage.nonRepudiation | KeyUsage.keyCertSign | KeyUsage.cRLSign  | KeyUsage.digitalSignature));
            }
            if (extension.equals("CA, Data and key encipherment")) {
                certGen.addExtension(Extension.basicConstraints, true, new BasicConstraints(true));
                certGen.addExtension(Extension.keyUsage, true, new KeyUsage(KeyUsage.nonRepudiation | KeyUsage.keyCertSign | KeyUsage.cRLSign | KeyUsage.keyEncipherment | KeyUsage.dataEncipherment | KeyUsage.keyAgreement ));
            }
            if (extension.equals("CA, Document signing, Data and key encipherment")) {
                certGen.addExtension(Extension.basicConstraints, true, new BasicConstraints(true));
                certGen.addExtension(Extension.keyUsage, true, new KeyUsage(KeyUsage.nonRepudiation | KeyUsage.keyCertSign | KeyUsage.cRLSign | KeyUsage.keyEncipherment | KeyUsage.dataEncipherment | KeyUsage.keyAgreement | KeyUsage.digitalSignature));
            }

            if (extension.equals("Document signing")) {
                certGen.addExtension(Extension.basicConstraints, false, new BasicConstraints(true));
                certGen.addExtension(Extension.keyUsage, true, new KeyUsage(  KeyUsage.nonRepudiation | KeyUsage.digitalSignature));
            }

            if (extension.equals("Data and key encipherment")) {
                certGen.addExtension(Extension.basicConstraints, false, new BasicConstraints(true));
                certGen.addExtension(Extension.keyUsage, true, new KeyUsage(KeyUsage.keyEncipherment | KeyUsage.dataEncipherment | KeyUsage.keyAgreement |
                        KeyUsage.nonRepudiation));
                }

            if (extension.equals("Document signing, Data and key encipherment")) {
                certGen.addExtension(Extension.basicConstraints, false, new BasicConstraints(true));

                certGen.addExtension(Extension.keyUsage, true, new KeyUsage(KeyUsage.keyEncipherment | KeyUsage.dataEncipherment | KeyUsage.keyAgreement |
                        KeyUsage.nonRepudiation | KeyUsage.digitalSignature));
                  }


            //Generise se sertifikat
            X509CertificateHolder certHolder = certGen.build(contentSigner);


            //Builder generise sertifikat kao objekat klase X509CertificateHolder
            //Nakon toga je potrebno certHolder konvertovati u sertifikat, za sta se koristi certConverter
            JcaX509CertificateConverter certConverter = new JcaX509CertificateConverter();
            certConverter = certConverter.setProvider("BC");

            //Konvertuje objekat u sertifikat

            return certConverter.getCertificate(certHolder);
        } catch (CertificateEncodingException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (OperatorCreationException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (CertIOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
