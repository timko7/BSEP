package com.example.demo.repository;

import com.example.demo.model.AliasEE;
import com.example.demo.model.PovuceniAliasi;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PovuceniReposiitory extends JpaRepository<PovuceniAliasi, Long> {

    PovuceniAliasi findByAlias(String alias);
}
