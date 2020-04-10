package com.example.demo.controller;


import com.example.demo.model.CertificateDAO;
import com.example.demo.model.MyCertificate;
import com.example.demo.service.MyCertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/api/certificate")
public class CertificateContoller {

    @Autowired
    private MyCertificateService certificateService;

    @RequestMapping(method = POST, value = "/create")
    public ResponseEntity<?> napraviSertifikat(@RequestBody CertificateDAO certificate) throws Exception {

        System.out.println("pre ulaska: "+certificate.toString());
        boolean povratna=certificateService.createCA(certificate);
        //TODO: ako je moguce da se vrati ovaj boolean na front? i ako je false da se ispise da datum mora biti u skladu sa izabranim issuerom
        //TODO: ako se vrati true onda da se refresuje stranica da moze novi sertifikat da se doda (da bi se u combobox ispisao i novi ca)
        //TODO: da se doda ispod forme tabela sa opisom mogucih ekstenzija
        //TODO: uid ako moze validacija da budu brojevi (a jedinstvenost po bazi?)
        //TODO: ako moze za datume validacija da mora drugi da bude veci od prvog

        return new ResponseEntity<>( povratna,HttpStatus.CREATED);
    }

    @RequestMapping(method = POST, value = "/createRoot")
    public ResponseEntity<?> napraviRootSertifikat(@RequestBody CertificateDAO certificate) throws Exception {

        System.out.println(certificate.toString());
        MyCertificate certificate1 = certificateService.createRoot(certificate);

        return new ResponseEntity<MyCertificate>(certificate1, HttpStatus.CREATED);
    }

    @RequestMapping(method = GET, value = "/aliasi")
    public ResponseEntity<?> vratiAliase() throws Exception {

        List<String> imena=new ArrayList<>();
        imena=certificateService.naziviSertifikata();
        return new ResponseEntity<>(imena, HttpStatus.CREATED);
    }

    @RequestMapping(method = GET, value = "/sviCASertifikati")
    public ResponseEntity<?> vratiSveCA() throws Exception {

        ArrayList<CertificateDAO> sertifikati=new ArrayList<>();
        sertifikati=certificateService.vratiSveCA();
        return new ResponseEntity<>(sertifikati, HttpStatus.CREATED);
    }

    @RequestMapping(method = GET, value = "/sviEESertifikati")
    public ResponseEntity<?> vratiSveEE() throws Exception {

        ArrayList<CertificateDAO> sertifikati=new ArrayList<>();
        sertifikati=certificateService.vratiSveEE();
        return new ResponseEntity<>(sertifikati, HttpStatus.CREATED);
    }

    @RequestMapping(method = GET, value = "/downloadCA/{uid}")
    public ResponseEntity<?> skiniEE(@PathVariable("uid") String uid) throws Exception {

        certificateService.skiniCa(uid);
        return new ResponseEntity<>( HttpStatus.OK);
    }
    @RequestMapping(method = GET, value = "/downloadEE/{uid}")
    public ResponseEntity<?> skiniCa(@PathVariable("uid") String uid) throws Exception {

        certificateService.skiniEE(uid);
        return new ResponseEntity<>( HttpStatus.OK);
    }

    @RequestMapping(method = GET, value = "/povuciCA/{uid}")
    public ResponseEntity<?> povuciSertCA(@PathVariable("uid") String uid) throws Exception {

        certificateService.povuciCertificateCA(uid);
        return new ResponseEntity<>( HttpStatus.OK);
    }

    @RequestMapping(method = GET, value = "/povuciEE/{uid}")
    public ResponseEntity<?> povuciSertEE(@PathVariable("uid") String uid) throws Exception {

        certificateService.povuciCertificateEE(uid);
        return new ResponseEntity<>( HttpStatus.OK);
    }

    @RequestMapping(method = GET, value = "/sviPovuceniSertifikati")
    public ResponseEntity<?> sviPovuceniSertifikati() throws Exception {

        ArrayList<CertificateDAO> sertifikati=new ArrayList<>();
        sertifikati = certificateService.vratiSvePovucene();
        return new ResponseEntity<>(sertifikati, HttpStatus.CREATED);
    }



}
