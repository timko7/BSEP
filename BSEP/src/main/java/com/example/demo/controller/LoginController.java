package com.example.demo.controller;


import com.example.demo.model.Admin;
import com.example.demo.model.Korisnik;
import com.example.demo.model.KorisnikDTO;
import com.example.demo.model.LoginZahtev;
import com.example.demo.service.AdminService;
import com.example.demo.service.KorisnikService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Context;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collection;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
@RequestMapping("/api/login")
public class LoginController {

    @Autowired
    private AdminService adminSer;

    @Autowired
    private KorisnikService korisnikService;


    @RequestMapping(method = POST, value = "/regKorisnika")
    public ResponseEntity<?> dodajKorisnika(@RequestBody KorisnikDTO korisnikRequest) throws Exception {
        Admin existAdmin = adminSer.findByEmail(korisnikRequest.getEmail());
        Korisnik existKor = korisnikService.findByEmail(korisnikRequest.getEmail());

        if (existAdmin != null || existKor != null) {
            return new ResponseEntity<>("Email vec postoji u bazi! Pokusajte drugi email.", HttpStatus.METHOD_NOT_ALLOWED);
        }

        Korisnik korisnik = korisnikService.create(korisnikRequest);

        return new ResponseEntity<Korisnik>(korisnik, HttpStatus.CREATED);
    }


    @RequestMapping(method = POST)
    public ResponseEntity<?> login(@RequestBody LoginZahtev zahtev, @Context HttpServletRequest request) {


        Collection<Admin> admini = adminSer.findAll();


        Admin ak = adminSer.findByEmail(zahtev.getEmail());

        if (ak != null) {

            if(checkIntegrity(zahtev.getPassword(), ak.getPassword())){
                //if (zahtev.getPassword().equals(ak.getPassword())) {
                HttpSession session = request.getSession();
                session.setAttribute("admin", ak);
                return new ResponseEntity<Admin>(ak, HttpStatus.CREATED);
            }

        } else {
            Korisnik korisnik = korisnikService.findByEmail(zahtev.getEmail());
            if (korisnik != null) {
                if(checkIntegrity(zahtev.getPassword(), korisnik.getPassword())){
                    HttpSession session = request.getSession();
                    session.setAttribute("korisnik", korisnik);
                    return new ResponseEntity<Korisnik>(korisnik, HttpStatus.CREATED);
                }
            }
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    //Metoda koja proverava da li je ostcen integritet poruke
    private boolean checkIntegrity(String data, byte[] dataHash) {
        byte[] newDataHash = hash(data);
        return Arrays.equals(dataHash, newDataHash);
    }
    public byte[] hash(String data) {
        //Kao hes funkcija koristi SHA-256
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] dataHash = sha256.digest(data.getBytes());
            return dataHash;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
    @RequestMapping(method = GET, value = "/vratiUlogovanog")
    public Object vratiUlogovanog(@Context HttpServletRequest request) {

        HttpSession session = request.getSession();

        Admin admin = (Admin) session.getAttribute("admin");

        if (admin != null) {
            return admin;
        } else {
            return (Korisnik) session.getAttribute("korisnik");
        }
    }


    @RequestMapping(method = PUT, value = "/logOut")
    public ResponseEntity logOut(@Context HttpServletRequest request) {
        HttpSession session = request.getSession();
        System.out.println("...LOGOUTK... " + session.getAttribute("korisnik"));
        System.out.println("...LOGOUTA... " + session.getAttribute("admin"));
        session.invalidate();

        return ResponseEntity.status(200).build();
    }
}
