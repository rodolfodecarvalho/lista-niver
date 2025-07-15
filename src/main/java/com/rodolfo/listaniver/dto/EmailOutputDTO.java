package com.rodolfo.listaniver.dto;

import com.rodolfo.listaniver.entity.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record EmailOutputDTO(
        @NotNull
        @Positive
        Long id,

        @NotNull
        String email,

        @NotNull
        @Positive
        Long pessoaId
) {
    public static EmailOutputDTO fromEntity(Email email) {
        return new EmailOutputDTO(
                email.getId(),
                email.getEmail(),
                email.getPessoa().getId()
        );
    }
}