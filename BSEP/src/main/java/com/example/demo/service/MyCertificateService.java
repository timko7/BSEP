package com.example.demo.service;

import com.example.demo.model.Admin;
import com.example.demo.model.CertificateDAO;
import com.example.demo.model.MyCertificate;
import com.itextpdf.text.DocumentException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface MyCertificateService {

    Collection<MyCertificate> findAll();
    boolean createCA(CertificateDAO certificate) throws Exception;
    MyCertificate createRoot(CertificateDAO certificate) throws Exception;
    MyCertificate revoke(MyCertificate certificate) throws Exception;
    void addToKeyStore(MyCertificate certificate,String keyStoreFile)throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException;
    List<String> naziviSertifikata();
    ArrayList<CertificateDAO>vratiSveCA()throws KeyStoreException, NoSuchProviderException;
    ArrayList<CertificateDAO>vratiSveEE()throws KeyStoreException, NoSuchProviderException;

    void skiniCa(String uid) throws FileNotFoundException, DocumentException;

    void skiniEE(String uid) throws FileNotFoundException, DocumentException;
}
