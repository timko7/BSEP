package com.example.demo.repository;


import com.example.demo.model.AliasEE;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AliasEERepository extends JpaRepository<AliasEE, Long> {

    AliasEE findByAlias(String alias);
}
