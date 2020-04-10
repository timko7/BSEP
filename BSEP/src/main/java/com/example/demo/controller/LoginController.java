package com.example.demo.controller;


import com.example.demo.model.Admin;
import com.example.demo.model.LoginZahtev;
import com.example.demo.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Context;

import java.util.Collection;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/api/login")
public class LoginController {

    @Autowired
    private AdminService adminSer;


    @RequestMapping(method = POST)
    public ResponseEntity<?> login(@RequestBody LoginZahtev zahtev, @Context HttpServletRequest request) {


        Collection<Admin> admini = adminSer.findAll();


        Admin ak=adminSer.findByEmail(zahtev.getEmail());

        if(ak!=null) {

                if(zahtev.getPassword().equals(ak.getPassword())) {
                    HttpSession session = request.getSession();
                    session.setAttribute("admin", ak);
                    return new ResponseEntity<Admin>(ak, HttpStatus.CREATED);
                }

            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }


        return new ResponseEntity<>(HttpStatus.NOT_FOUND);



    }

    @RequestMapping(method = GET, value = "/vratiUlogovanog")
    public Object vratiUlogovanog(@Context HttpServletRequest request) {

        HttpSession session = request.getSession();

        Admin admin = (Admin) session.getAttribute("admin");

         if(admin!=null){
            return admin;
        }
        else
            return null;
    }


    @RequestMapping(method = POST, value = "/logOut")
    public ResponseEntity logOut(@Context HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.invalidate();

        return ResponseEntity.status(200).build();
    }
}
