package com.rodolfo.listaniver.dto;

import com.rodolfo.listaniver.entity.Pessoa;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

public record PessoaOutputDTO(
        @NotNull
        @Positive
        Long id,

        @NotNull
        String nome,

        @NotNull
        LocalDate dataNascimento,

        Set<EmailOutputDTO> emails
) {
    public static PessoaOutputDTO fromEntity(Pessoa pessoa) {
        Set<EmailOutputDTO> emailsDto = pessoa.getEmails() != null
                ? pessoa.getEmails().stream()
                .map(EmailOutputDTO::fromEntity)
                .collect(Collectors.toSet())
                : Set.of();

        return new PessoaOutputDTO(
                pessoa.getId(),
                pessoa.getNome(),
                pessoa.getDataNascimento(),
                emailsDto
        );
    }
}