package com.rodolfo.listaniver.repository;

import com.rodolfo.listaniver.entity.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PessoaRepository extends JpaRepository<Pessoa, Long> {
    List<Pessoa> findByNomeContainingIgnoreCase(String nome);

    boolean existsByNomeAndDataNascimento(String nome, LocalDate dataNascimento);
}