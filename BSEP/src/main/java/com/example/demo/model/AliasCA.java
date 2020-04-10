package com.example.demo.model;

import javax.persistence.*;

@Entity
public class AliasCA {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "Alias", nullable = false)
    private String alias;


    public AliasCA(Long id, String alias) {
        this.id = id;
        this.alias = alias;
    }

    public AliasCA(String alias) {
        this.alias = alias;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public AliasCA() {
    }
}
