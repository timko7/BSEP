package com.example.demo.model;

import javax.persistence.*;

@Entity
public class AliasCA {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "Alias", nullable = false)
    private String alias;

    @Column(name = "AliasIssuer", nullable = false)
    private String aliasIssuer;

    public AliasCA(Long id, String alias,String aliasIssuer) {
        this.id = id;
        this.alias = alias;
        this.aliasIssuer=aliasIssuer;
    }

    public AliasCA(String alias,String aliasIssuer) {

        this.alias = alias;
        this.aliasIssuer=aliasIssuer;
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

    public String getAliasIssuer() {
        return aliasIssuer;
    }

    public void setAliasIssuer(String aliasIssuer) {
        this.aliasIssuer = aliasIssuer;
    }

    public AliasCA() {
    }
}
