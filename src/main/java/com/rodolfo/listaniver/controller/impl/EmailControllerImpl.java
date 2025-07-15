package com.rodolfo.listaniver.controller.impl;

import com.rodolfo.listaniver.controller.EmailController;
import com.rodolfo.listaniver.dto.EmailInputDTO;
import com.rodolfo.listaniver.dto.EmailOutputDTO;
import com.rodolfo.listaniver.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emails")
@RequiredArgsConstructor
@Slf4j
public class EmailControllerImpl implements EmailController {

    private final EmailService emailService;

    @PostMapping("/pessoa/{pessoaId}")
    public ResponseEntity<EmailOutputDTO> adicionarEmail(@PathVariable Long pessoaId, @Valid @RequestBody EmailInputDTO emailInputDTO) {

        log.info("Requisição para adicionar email para pessoa ID: {}", pessoaId);

        EmailOutputDTO emailCriado = emailService.adicionarEmail(pessoaId, emailInputDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(emailCriado);
    }

    @GetMapping("/pessoa/{pessoaId}")
    public ResponseEntity<List<EmailOutputDTO>> listarEmailsPorPessoa(@PathVariable Long pessoaId) {
        log.info("Requisição para listar emails da pessoa ID: {}", pessoaId);

        List<EmailOutputDTO> emails = emailService.listarEmailsPorPessoa(pessoaId);

        return ResponseEntity.ok(emails);
    }

    @GetMapping("/{emailId}")
    public ResponseEntity<EmailOutputDTO> buscarEmailPorId(@PathVariable Long emailId) {
        log.info("Requisição para buscar email ID: {}", emailId);

        EmailOutputDTO email = emailService.buscarPorId(emailId);

        return ResponseEntity.ok(email);
    }

    @PutMapping("/{emailId}")
    public ResponseEntity<EmailOutputDTO> atualizarEmail(@PathVariable Long emailId, @Valid @RequestBody EmailInputDTO emailInputDTO) {

        log.info("Requisição para atualizar email ID: {}", emailId);

        EmailOutputDTO emailAtualizado = emailService.atualizarEmail(emailId, emailInputDTO);

        return ResponseEntity.ok(emailAtualizado);
    }

    @DeleteMapping("/{emailId}")
    public ResponseEntity<Void> removerEmail(@PathVariable Long emailId) {
        log.info("Requisição para remover email ID: {}", emailId);

        emailService.removerEmail(emailId);

        return ResponseEntity.noContent().build();
    }
}