package com.rodolfo.listaniver.dto;

import com.rodolfo.listaniver.entity.Pessoa;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record PessoaOutputDTO(
        @NotNull
        @Positive
        Long id,

        @NotNull
        String nome,

        @NotNull
        LocalDate dataNascimento
) {
    public static PessoaOutputDTO fromEntity(Pessoa pessoa) {
        return new PessoaOutputDTO(
                pessoa.getId(),
                pessoa.getNome(),
                pessoa.getDataNascimento()
        );
    }
}