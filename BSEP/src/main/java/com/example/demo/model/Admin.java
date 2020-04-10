package com.example.demo.model;


import javax.persistence.*;

@Entity
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "Ime", nullable = false)
    private String ime;

    @Column(name = "Prezime", nullable = false)
    private String prezime;

    @Column(name = "Email", nullable = false)
    private String email;

    @Column(name = "Password", nullable = false)
    private String password;

    @Column(name = "RootCreated", nullable = false)
    private boolean rootCreated=false;

    public Admin(String ime, String prezime, String email, String password) {
        this.ime = ime;
        this.prezime = prezime;
        this.email = email;
        this.password = password;
        this.rootCreated=false;

    }

    public Admin() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public String getPrezime() {
        return prezime;
    }

    public void setPrezime(String prezime) {
        this.prezime = prezime;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isRootCreated() {
        return rootCreated;
    }

    public void setRootCreated(boolean rootCreated) {
        this.rootCreated = rootCreated;
    }

    public void copyValues(Admin adminKlinike) {
        this.ime = adminKlinike.getIme();
        this.prezime = adminKlinike.getPrezime();
        this.email = adminKlinike.getEmail();
        this.password = adminKlinike.getPassword();
        this.rootCreated=adminKlinike.isRootCreated();

    }

}
