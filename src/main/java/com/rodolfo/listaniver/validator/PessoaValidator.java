package com.rodolfo.listaniver.validator;

import com.rodolfo.listaniver.entity.Pessoa;
import com.rodolfo.listaniver.exception.DuplicatePessoaException;
import com.rodolfo.listaniver.exception.RecordNotFoundException;
import com.rodolfo.listaniver.repository.PessoaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class PessoaValidator {

    private final PessoaRepository repository;

    public void validateDuplicatePessoa(String nome, LocalDate dataNascimento) {
        if (repository.existsByNomeAndDataNascimento(nome, dataNascimento)) {
            throw new DuplicatePessoaException("Pessoa com nome e data de nascimento já existe");
        }
    }

    public void validateDuplicatePessoaForUpdate(Pessoa pessoa, String nome, LocalDate dataNascimento) {
        if (repository.existsByNomeAndDataNascimento(nome, dataNascimento) &&
                (!pessoa.getNome().equals(nome) || !pessoa.getDataNascimento().equals(dataNascimento))) {
            throw new DuplicatePessoaException("Pessoa com nome e data de nascimento já existe");
        }
    }

    public void validatePessoaExists(Long id) {
        if (!repository.existsById(id)) {
            throw new RecordNotFoundException("Pessoa", id);
        }
    }

    public Pessoa findPessoaById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Pessoa", id));
    }
}