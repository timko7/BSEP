package com.example.demo.repository;


import com.example.demo.model.AliasCA;
import com.example.demo.model.AliasEE;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;

public interface AliasEERepository extends JpaRepository<AliasEE, Long> {

    AliasEE findByAlias(String alias);
    ArrayList<AliasEE> findByAliasIssuer(String aliasIssuer);
}
