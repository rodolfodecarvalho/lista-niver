package com.rodolfo.listaniver.service.impl;

import com.rodolfo.listaniver.dto.EmailInputDTO;
import com.rodolfo.listaniver.dto.EmailOutputDTO;
import com.rodolfo.listaniver.entity.Email;
import com.rodolfo.listaniver.entity.Pessoa;
import com.rodolfo.listaniver.exception.RecordNotFoundException;
import com.rodolfo.listaniver.repository.EmailRepository;
import com.rodolfo.listaniver.repository.PessoaRepository;
import com.rodolfo.listaniver.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EmailServiceImpl implements EmailService {

    private final EmailRepository emailRepository;
    private final PessoaRepository pessoaRepository;

    @Override
    public EmailOutputDTO adicionarEmail(Long pessoaId, EmailInputDTO emailInputDTO) {
        log.info("Adicionando email {} para pessoa ID: {}", emailInputDTO.email(), pessoaId);

        Pessoa pessoa = pessoaRepository.findById(pessoaId)
                .orElseThrow(() -> new RecordNotFoundException("Pessoa", pessoaId));

        Email email = new Email();
        email.setEmail(emailInputDTO.email());
        email.setPessoa(pessoa);

        Email savedEmail = emailRepository.save(email);

        log.info("Email adicionado com sucesso: ID {}", savedEmail.getId());
        return EmailOutputDTO.fromEntity(savedEmail);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmailOutputDTO> listarEmailsPorPessoa(Long pessoaId) {
        log.info("Listando emails para pessoa ID: {}", pessoaId);

        if (!pessoaRepository.existsById(pessoaId)) {
            throw new RecordNotFoundException("Pessoa", pessoaId);
        }

        return emailRepository.findByPessoaId(pessoaId)
                .stream()
                .map(EmailOutputDTO::fromEntity)
                .toList();
    }

    @Override
    public void removerEmail(Long emailId) {
        log.info("Removendo email ID: {}", emailId);

        if (!emailRepository.existsById(emailId)) {
            throw new RecordNotFoundException("Email", emailId);
        }

        emailRepository.deleteById(emailId);
        log.info("Email removido com sucesso: ID {}", emailId);
    }

    @Override
    @Transactional(readOnly = true)
    public EmailOutputDTO buscarPorId(Long emailId) {
        log.info("Buscando email por ID: {}", emailId);

        Email email = emailRepository.findById(emailId)
                .orElseThrow(() -> new RecordNotFoundException("Email", emailId));

        return EmailOutputDTO.fromEntity(email);
    }

    @Override
    public EmailOutputDTO atualizarEmail(Long emailId, EmailInputDTO emailInputDTO) {
        log.info("Atualizando email ID: {}", emailId);

        Email email = emailRepository.findById(emailId)
                .orElseThrow(() -> new RecordNotFoundException("Email", emailId));

        email.setEmail(emailInputDTO.email());

        Email updatedEmail = emailRepository.save(email);

        log.info("Email atualizado com sucesso: ID {}", updatedEmail.getId());
        return EmailOutputDTO.fromEntity(updatedEmail);
    }
}