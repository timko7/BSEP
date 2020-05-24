package com.example.demo.service;

import com.example.demo.model.Korisnik;
import com.example.demo.model.KorisnikDTO;
import com.example.demo.repository.KorisnikReporitory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;

@Service
public class KorisnikServiceImpl implements KorisnikService {

    @Autowired
    private KorisnikReporitory korisnikReporitory;

    @Override
    public Collection<Korisnik> findAll() {
        return korisnikReporitory.findAll();
    }

    @Override
    public Korisnik findById(Long id) {
        return korisnikReporitory.findById(id).orElseGet(null);
    }

    @Override
    public Korisnik findByEmail(String email) {
        return korisnikReporitory.findByEmail(email);
    }

    @Override
    public Korisnik create(KorisnikDTO korisnik) throws Exception {
        String password=korisnik.getPassword();
        byte[] dataHash = hash(password);
        Korisnik ret = new Korisnik();
        ret.setPassword(dataHash);
        ret.setIme(korisnik.getIme());
        ret.setPrezime( korisnik.getPrezime());
        ret.setEmail(korisnik.getEmail());
        //ret.copyValues(korisnik);
        ret = korisnikReporitory.save(ret);

        return ret;
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
    @Override
    public Korisnik update(Korisnik korisnik) throws Exception {
        return null;
    }

    @Override
    public void delete(Long id) {

    }
}
