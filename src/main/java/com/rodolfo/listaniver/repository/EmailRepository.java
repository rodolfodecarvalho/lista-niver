package com.rodolfo.listaniver.repository;

import com.rodolfo.listaniver.entity.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EmailRepository extends JpaRepository<Email, Long> {

    List<Email> findByPessoaId(Long pessoaId);

    @Modifying
    @Query("DELETE FROM Email e WHERE e.pessoa.id = :pessoaId")
    void deleteByPessoaId(@Param("pessoaId") Long pessoaId);
}