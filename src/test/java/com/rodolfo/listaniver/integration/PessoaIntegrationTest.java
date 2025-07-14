package com.rodolfo.listaniver.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rodolfo.listaniver.dto.PessoaInputDTO;
import com.rodolfo.listaniver.dto.PessoaOutputDTO;
import com.rodolfo.listaniver.dto.PessoaUpdateDTO;
import com.rodolfo.listaniver.entity.Pessoa;
import com.rodolfo.listaniver.repository.PessoaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Testes de Integração")
public class PessoaIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private PessoaRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        repository.deleteAll();
    }

    @Test
    void deveRealizarCrudCompletoComSucesso() throws Exception {
        // CREATE
        PessoaInputDTO inputDTO = new PessoaInputDTO("João Silva", LocalDate.of(1990, 5, 15));

        String createResponse = mockMvc.perform(post("/api/pessoas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.dataNascimento").value("1990-05-15"))
                .andReturn().getResponse().getContentAsString();

        PessoaOutputDTO created = objectMapper.readValue(createResponse, PessoaOutputDTO.class);

        // READ
        mockMvc.perform(get("/api/pessoas/" + created.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(created.id()))
                .andExpect(jsonPath("$.nome").value("João Silva"));

        // UPDATE
        PessoaUpdateDTO updateDTO = new PessoaUpdateDTO("João Santos", LocalDate.of(1990, 5, 15));

        mockMvc.perform(put("/api/pessoas/" + created.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("João Santos"));

        // DELETE
        mockMvc.perform(delete("/api/pessoas/" + created.id()))
                .andExpect(status().isNoContent());

        // Verificar se foi deletado
        mockMvc.perform(get("/api/pessoas/" + created.id()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornarConflictParaPessoaDuplicada() throws Exception {
        // Criar primeira pessoa
        Pessoa pessoa = new Pessoa();
        pessoa.setNome("João Silva");
        pessoa.setDataNascimento(LocalDate.of(1990, 5, 15));
        repository.save(pessoa);

        // Tentar criar pessoa duplicada
        PessoaInputDTO inputDTO = new PessoaInputDTO("João Silva", LocalDate.of(1990, 5, 15));

        mockMvc.perform(post("/api/pessoas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    void deveBuscarPessoasPorNome() throws Exception {
        // Criar pessoas
        repository.save(new Pessoa(null, "João Silva", LocalDate.of(1990, 5, 15)));
        repository.save(new Pessoa(null, "João Santos", LocalDate.of(1985, 3, 10)));
        repository.save(new Pessoa(null, "Maria Silva", LocalDate.of(1992, 8, 20)));

        // Buscar por nome
        mockMvc.perform(get("/api/pessoas/buscar")
                        .param("nome", "João"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }
}