package com.example.demo.controller;


import com.example.demo.model.Admin;
import com.example.demo.model.KorisnikDTO;
import com.example.demo.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Context;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private AdminService adminKlinikeService;


    @RequestMapping(method = GET, value = "/user/{userId}")
    public Admin loadById(@PathVariable Long userId) {
        return adminKlinikeService.findById(userId);
    }

    @RequestMapping(method = POST, value = "/add")
    public ResponseEntity<?> dodajAdminaKlinike(@RequestBody KorisnikDTO adminKlinikeRequest) throws Exception {
        Admin exist = adminKlinikeService.findByEmail(adminKlinikeRequest.getEmail());
        if (exist != null) {
            return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
        }

        Admin admin = adminKlinikeService.create(adminKlinikeRequest);

        return new ResponseEntity<Admin>(admin, HttpStatus.CREATED);
    }


    @PutMapping(value = "/promeniPodatke/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> promeniPodatkeAdmina(@Context HttpServletRequest request, @RequestBody Admin adminKlinike)
            throws Exception {

        HttpSession session = request.getSession();
        Admin admin = (Admin) session.getAttribute("admin");

        if (admin == null) {
            return new ResponseEntity<>("Nedozvoljeno ponasanje!", HttpStatus.FORBIDDEN);
        } else {
            Admin a = adminKlinikeService.update(adminKlinike);
            return new ResponseEntity<Admin>(a, HttpStatus.OK);
        }
    }

    /*
     * url: /api/adminiKlinike/obrisi/{email}
     */
    @DeleteMapping(value = "/obrisi/{email}")
    public ResponseEntity<?> izbrisiAdminKlinike(@Context HttpServletRequest request, @PathVariable("email") String email) {
        HttpSession session = request.getSession();
        Admin admin = (Admin) session.getAttribute("admin");

        if (admin == null) {
            return new ResponseEntity<>("Nedozvoljeno ponasanje!", HttpStatus.FORBIDDEN);
        } else {
            adminKlinikeService.deleteByEmail(email);
            return new ResponseEntity<Admin>(HttpStatus.NO_CONTENT);
        }
    }
}
