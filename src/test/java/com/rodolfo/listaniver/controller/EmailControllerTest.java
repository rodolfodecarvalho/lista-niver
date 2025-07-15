package com.rodolfo.listaniver.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rodolfo.listaniver.dto.EmailInputDTO;
import com.rodolfo.listaniver.dto.EmailOutputDTO;
import com.rodolfo.listaniver.exception.RecordNotFoundException;
import com.rodolfo.listaniver.service.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmailController.class)
public class EmailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmailService emailService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void adicionarEmail_DeveRetornarEmailCriadoQuandoDadosValidos() throws Exception {
        // Given
        Long pessoaId = 1L;
        EmailInputDTO emailInput = new EmailInputDTO("test@email.com");
        EmailOutputDTO emailOutput = new EmailOutputDTO(1L, "test@email.com", pessoaId);

        given(emailService.adicionarEmail(eq(pessoaId), any(EmailInputDTO.class)))
                .willReturn(emailOutput);

        // When & Then
        mockMvc.perform(post("/api/emails/pessoa/{pessoaId}", pessoaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailInput)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("test@email.com"))
                .andExpect(jsonPath("$.pessoaId").value(pessoaId));
    }

    @Test
    void adicionarEmail_DeveRetornarUnprocessableEntityQuandoEmailInvalido() throws Exception {
        // Given
        Long pessoaId = 1L;
        EmailInputDTO emailInput = new EmailInputDTO("email-invalido");

        // When & Then
        mockMvc.perform(post("/api/emails/pessoa/{pessoaId}", pessoaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailInput)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void adicionarEmail_DeveRetornarNotFoundQuandoPessoaNaoExiste() throws Exception {
        // Given
        Long pessoaId = 999L;
        EmailInputDTO emailInput = new EmailInputDTO("test@email.com");

        given(emailService.adicionarEmail(eq(pessoaId), any(EmailInputDTO.class)))
                .willThrow(new RecordNotFoundException("Pessoa", pessoaId));

        // When & Then
        mockMvc.perform(post("/api/emails/pessoa/{pessoaId}", pessoaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailInput)))
                .andExpect(status().isNotFound());
    }

    @Test
    void listarEmailsPorPessoa_DeveRetornarListaEmailsQuandoPessoaExiste() throws Exception {
        // Given
        Long pessoaId = 1L;
        List<EmailOutputDTO> emails = List.of(
                new EmailOutputDTO(1L, "email1@test.com", pessoaId),
                new EmailOutputDTO(2L, "email2@test.com", pessoaId)
        );

        given(emailService.listarEmailsPorPessoa(pessoaId)).willReturn(emails);

        // When & Then
        mockMvc.perform(get("/api/emails/pessoa/{pessoaId}", pessoaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].email").value("email1@test.com"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].email").value("email2@test.com"));
    }

    @Test
    void listarEmailsPorPessoa_DeveRetornarNotFoundQuandoPessoaNaoExiste() throws Exception {
        // Given
        Long pessoaId = 999L;

        given(emailService.listarEmailsPorPessoa(pessoaId))
                .willThrow(new RecordNotFoundException("Pessoa", pessoaId));

        // When & Then
        mockMvc.perform(get("/api/emails/pessoa/{pessoaId}", pessoaId))
                .andExpect(status().isNotFound());
    }

    @Test
    void buscarEmailPorId_DeveRetornarEmailQuandoEmailExiste() throws Exception {
        // Given
        Long emailId = 1L;
        EmailOutputDTO email = new EmailOutputDTO(emailId, "test@email.com", 1L);

        given(emailService.buscarPorId(emailId)).willReturn(email);

        // When & Then
        mockMvc.perform(get("/api/emails/{emailId}", emailId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(emailId))
                .andExpect(jsonPath("$.email").value("test@email.com"))
                .andExpect(jsonPath("$.pessoaId").value(1L));
    }

    @Test
    void buscarEmailPorId_DeveRetornarNotFoundQuandoEmailNaoExiste() throws Exception {
        // Given
        Long emailId = 999L;

        given(emailService.buscarPorId(emailId))
                .willThrow(new RecordNotFoundException("Email", emailId));

        // When & Then
        mockMvc.perform(get("/api/emails/{emailId}", emailId))
                .andExpect(status().isNotFound());
    }

    @Test
    void atualizarEmail_DeveRetornarEmailAtualizadoQuandoDadosValidos() throws Exception {
        // Given
        Long emailId = 1L;
        EmailInputDTO emailInput = new EmailInputDTO("updated@email.com");
        EmailOutputDTO emailOutput = new EmailOutputDTO(emailId, "updated@email.com", 1L);

        given(emailService.atualizarEmail(eq(emailId), any(EmailInputDTO.class)))
                .willReturn(emailOutput);

        // When & Then
        mockMvc.perform(put("/api/emails/{emailId}", emailId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailInput)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(emailId))
                .andExpect(jsonPath("$.email").value("updated@email.com"))
                .andExpect(jsonPath("$.pessoaId").value(1L));
    }

    @Test
    void atualizarEmail_DeveRetornarUnprocessableEntityQuandoEmailInvalido() throws Exception {
        // Given
        Long emailId = 1L;
        EmailInputDTO emailInput = new EmailInputDTO("email-invalido");

        // When & Then
        mockMvc.perform(put("/api/emails/{emailId}", emailId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailInput)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void atualizarEmail_DeveRetornarNotFoundQuandoEmailNaoExiste() throws Exception {
        // Given
        Long emailId = 999L;
        EmailInputDTO emailInput = new EmailInputDTO("test@email.com");

        given(emailService.atualizarEmail(eq(emailId), any(EmailInputDTO.class)))
                .willThrow(new RecordNotFoundException("Email", emailId));

        // When & Then
        mockMvc.perform(put("/api/emails/{emailId}", emailId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailInput)))
                .andExpect(status().isNotFound());
    }

    @Test
    void removerEmail_DeveRetornarNoContentQuandoEmailExiste() throws Exception {
        // Given
        Long emailId = 1L;
        doNothing().when(emailService).removerEmail(emailId);

        // When & Then
        mockMvc.perform(delete("/api/emails/{emailId}", emailId))
                .andExpect(status().isNoContent());
    }

    @Test
    void removerEmail_DeveRetornarNotFoundQuandoEmailNaoExiste() throws Exception {
        // Given
        Long emailId = 999L;
        doThrow(new RecordNotFoundException("Email", emailId))
                .when(emailService).removerEmail(emailId);

        // When & Then
        mockMvc.perform(delete("/api/emails/{emailId}", emailId))
                .andExpect(status().isNotFound());
    }
}