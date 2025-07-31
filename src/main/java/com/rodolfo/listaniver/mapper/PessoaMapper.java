package com.rodolfo.listaniver.mapper;

import com.rodolfo.listaniver.dto.EmailInputDTO;
import com.rodolfo.listaniver.dto.PessoaInputDTO;
import com.rodolfo.listaniver.entity.Email;
import com.rodolfo.listaniver.entity.Pessoa;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PessoaMapper {

    public Pessoa toEntity(PessoaInputDTO inputDTO) {
        Pessoa pessoa = new Pessoa();
        pessoa.setNome(inputDTO.nome());
        pessoa.setDataNascimento(inputDTO.dataNascimento());
        pessoa.setEmails(convertEmailInputDTOsToEmails(inputDTO.emails(), pessoa));
        return pessoa;
    }

    public Set<Email> convertEmailInputDTOsToEmails(Set<EmailInputDTO> emailInputDTOs, Pessoa pessoa) {
        if (emailInputDTOs == null || emailInputDTOs.isEmpty()) {
            return new HashSet<>();
        }

        return emailInputDTOs.stream()
                .map(emailInputDTO -> {
                    Email email = new Email();
                    email.setEmail(emailInputDTO.email());
                    email.setPessoa(pessoa);
                    return email;
                })
                .collect(Collectors.toSet());
    }
}