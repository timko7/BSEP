package com.example.demo.service;

import com.example.demo.model.Admin;
import com.example.demo.model.KorisnikDTO;

import java.util.Collection;

public interface AdminService {

    Collection<Admin> findAll();
    Admin findById(Long id);

    Admin findByEmail(String email);

    Admin create(KorisnikDTO admin) throws Exception;

    Admin update(Admin admin) throws Exception;


    void delete(Long id);

    void deleteByEmail(String email);
}
