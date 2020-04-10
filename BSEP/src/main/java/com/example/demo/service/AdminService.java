package com.example.demo.service;

import com.example.demo.model.Admin;

import java.util.Collection;

public interface AdminService {

    Collection<Admin> findAll();
    Admin findById(Long id);

    Admin findByEmail(String email);

    Admin create(Admin admin) throws Exception;

    Admin update(Admin admin) throws Exception;


    void delete(Long id);

    void deleteByEmail(String email);
}
