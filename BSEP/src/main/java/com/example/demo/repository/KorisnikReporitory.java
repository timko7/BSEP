package com.example.demo.repository;

import com.example.demo.model.Korisnik;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KorisnikReporitory extends JpaRepository<Korisnik, Long> {

    Korisnik findByEmail(String email);
}
