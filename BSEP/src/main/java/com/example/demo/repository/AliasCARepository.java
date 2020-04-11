package com.example.demo.repository;

import com.example.demo.model.AliasCA;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;

public interface AliasCARepository  extends JpaRepository<AliasCA,Long> {

    AliasCA findByAlias(String alias);
    ArrayList<AliasCA> findByAliasIssuer(String aliasIssuer);
}
