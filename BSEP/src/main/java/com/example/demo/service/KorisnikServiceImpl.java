package com.example.demo.service;

import com.example.demo.model.Korisnik;
import com.example.demo.repository.KorisnikReporitory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public Korisnik create(Korisnik korisnik) throws Exception {
        Korisnik ret = new Korisnik();
        ret.copyValues(korisnik);
        ret = korisnikReporitory.save(ret);

        return ret;
    }

    @Override
    public Korisnik update(Korisnik korisnik) throws Exception {
        return null;
    }

    @Override
    public void delete(Long id) {

    }
}
