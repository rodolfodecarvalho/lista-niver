package com.rodolfo.listaniver.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rodolfo.listaniver.controller.impl.PessoaControllerImpl;
import com.rodolfo.listaniver.dto.*;
import com.rodolfo.listaniver.service.PessoaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PessoaControllerImpl.class)
@DisplayName("Testes do Controller de Pessoa")
public class PessoaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PessoaService service;

    @Autowired
    private ObjectMapper objectMapper;

    private PessoaInputDTO inputDTO;
    private PessoaOutputDTO outputDTO;
    private PessoaUpdateDTO updateDTO;

    @BeforeEach
    void setUp() {
        Set<EmailInputDTO> emailsInput = Set.of(new EmailInputDTO("joao@email.com"));
        Set<EmailOutputDTO> emailsOutput = Set.of(new EmailOutputDTO(1L, "joao@email.com", 1L));
        
        inputDTO = new PessoaInputDTO("João Silva", LocalDate.of(1990, 5, 15), emailsInput);
        outputDTO = new PessoaOutputDTO(1L, "João Silva", LocalDate.of(1990, 5, 15), emailsOutput);
        updateDTO = new PessoaUpdateDTO("João Santos", LocalDate.of(1990, 5, 15), emailsInput);
    }

    @Test
    void deveCriarPessoaComSucesso() throws Exception {
        // Given
        when(service.criar(any(PessoaInputDTO.class))).thenReturn(outputDTO);

        // When & Then
        mockMvc.perform(post("/api/pessoas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.dataNascimento").value("1990-05-15"));
    }

    @Test
    void deveRetornarUnprocessableEntityParaDadosInvalidos() throws Exception {
        // Given
        PessoaInputDTO invalidDTO = new PessoaInputDTO("", LocalDate.now().plusDays(1), Set.of(new EmailInputDTO("invalid@email.com")));

        // When & Then
        mockMvc.perform(post("/api/pessoas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void deveBuscarPessoaPorIdComSucesso() throws Exception {
        // Given
        when(service.buscarPorId(anyLong())).thenReturn(outputDTO);

        // When & Then
        mockMvc.perform(get("/api/pessoas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("João Silva"));
    }

    @Test
    void deveListarTodasPessoasComSucesso() throws Exception {
        // Given
        when(service.listarTodos()).thenReturn(List.of(outputDTO));

        // When & Then
        mockMvc.perform(get("/api/pessoas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nome").value("João Silva"));
    }

    @Test
    void deveAtualizarPessoaComSucesso() throws Exception {
        // Given
        Set<EmailOutputDTO> emailsOutput = Set.of(new EmailOutputDTO(1L, "joao@email.com", 1L));
        PessoaOutputDTO updatedOutput = new PessoaOutputDTO(1L, "João Santos", LocalDate.of(1990, 5, 15), emailsOutput);
        when(service.atualizar(anyLong(), any(PessoaUpdateDTO.class))).thenReturn(updatedOutput);

        // When & Then
        mockMvc.perform(put("/api/pessoas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("João Santos"));
    }

    @Test
    void deveDeletarPessoaComSucesso() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/pessoas/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveBuscarPessoasPorNomeComSucesso() throws Exception {
        // Given
        when(service.buscarPorNome("João")).thenReturn(List.of(outputDTO));

        // When & Then
        mockMvc.perform(get("/api/pessoas/buscar")
                        .param("nome", "João"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].nome").value("João Silva"));
    }
}