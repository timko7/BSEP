package com.example.demo.service;

import com.example.demo.model.Korisnik;

import java.util.Collection;

public interface KorisnikService {

    Collection<Korisnik> findAll();

    Korisnik findById(Long id);

    Korisnik findByEmail(String email);

    Korisnik create(Korisnik korisnik) throws Exception;

    Korisnik update(Korisnik korisnik) throws Exception;

    void delete(Long id);
}
