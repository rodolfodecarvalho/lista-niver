package com.rodolfo.listaniver.entity;

import com.rodolfo.listaniver.dto.EmailInputDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "pessoa")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pessoa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(name = "data_nascimento", nullable = false)
    private LocalDate dataNascimento;

    @OneToMany(mappedBy = "pessoa", cascade = CascadeType.ALL)
    private Set<Email> emails;

    public Set<Email> convertEmailInputDTOsToEmails(Set<EmailInputDTO> emailInputDTOs) {
        if (emailInputDTOs == null || emailInputDTOs.isEmpty()) {
            return new HashSet<>();
        }

        return emailInputDTOs.stream()
                .map(emailInputDTO -> {
                    Email email = new Email();
                    email.setEmail(emailInputDTO.email());
                    email.setPessoa(this);
                    return email;
                })
                .collect(Collectors.toSet());
    }
}