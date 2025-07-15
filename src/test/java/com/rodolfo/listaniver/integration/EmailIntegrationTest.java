package com.rodolfo.listaniver.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rodolfo.listaniver.dto.EmailInputDTO;
import com.rodolfo.listaniver.entity.Email;
import com.rodolfo.listaniver.entity.Pessoa;
import com.rodolfo.listaniver.repository.EmailRepository;
import com.rodolfo.listaniver.repository.PessoaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
@DisplayName("Testes de Integração Email")
public class EmailIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private EmailRepository emailRepository;

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private Pessoa pessoaTeste;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // Limpa os dados antes de cada teste
        emailRepository.deleteAll();
        pessoaRepository.deleteAll();

        // Cria pessoa para os testes
        pessoaTeste = new Pessoa();
        pessoaTeste.setNome("João Silva");
        pessoaTeste.setDataNascimento(LocalDate.of(1990, 1, 15));
        pessoaTeste = pessoaRepository.save(pessoaTeste);
    }

    @Test
    void deveCriarEmailComSucesso() throws Exception {
        // Given
        EmailInputDTO emailInputDTO = new EmailInputDTO("joao.silva@email.com");

        // When & Then
        mockMvc.perform(post("/api/emails/pessoa/{pessoaId}", pessoaTeste.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailInputDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value("joao.silva@email.com"))
                .andExpect(jsonPath("$.pessoaId").value(pessoaTeste.getId()));

        // Verifica se foi salvo no banco
        List<Email> emails = emailRepository.findByPessoaId(pessoaTeste.getId());
        assertThat(emails).hasSize(1);
        assertThat(emails.getFirst().getEmail()).isEqualTo("joao.silva@email.com");
    }

    @Test
    void deveRetornarErroQuandoPessoaNaoExiste() throws Exception {
        // Given
        EmailInputDTO emailInputDTO = new EmailInputDTO("teste@email.com");
        Long pessoaIdInexistente = 99999L;

        // When & Then
        mockMvc.perform(post("/api/emails/pessoa/{pessoaId}", pessoaIdInexistente)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailInputDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornarErroQuandoEmailInvalido() throws Exception {
        // Given
        EmailInputDTO emailInputDTO = new EmailInputDTO("email-invalido");

        // When & Then
        mockMvc.perform(post("/api/emails/pessoa/{pessoaId}", pessoaTeste.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailInputDTO)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void deveRetornarErroQuandoEmailVazio() throws Exception {
        // Given
        EmailInputDTO emailInputDTO = new EmailInputDTO("");

        // When & Then
        mockMvc.perform(post("/api/emails/pessoa/{pessoaId}", pessoaTeste.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailInputDTO)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void deveListarEmailsPorPessoa() throws Exception {
        // Given
        Email email1 = new Email();
        email1.setEmail("joao1@email.com");
        email1.setPessoa(pessoaTeste);
        emailRepository.save(email1);

        Email email2 = new Email();
        email2.setEmail("joao2@email.com");
        email2.setPessoa(pessoaTeste);
        emailRepository.save(email2);

        // When & Then
        mockMvc.perform(get("/api/emails/pessoa/{pessoaId}", pessoaTeste.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].email", containsInAnyOrder("joao1@email.com", "joao2@email.com")))
                .andExpect(jsonPath("$[*].pessoaId", everyItem(is(pessoaTeste.getId().intValue()))));
    }

    @Test
    void deveRetornarListaVaziaQuandoPessoaNaoTemEmails() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/emails/pessoa/{pessoaId}", pessoaTeste.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void deveRetornarErroQuandoListarEmailsDePessoaInexistente() throws Exception {
        // Given
        Long pessoaIdInexistente = 99999L;

        // When & Then
        mockMvc.perform(get("/api/emails/pessoa/{pessoaId}", pessoaIdInexistente))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveBuscarEmailPorId() throws Exception {
        // Given
        Email email = new Email();
        email.setEmail("joao@email.com");
        email.setPessoa(pessoaTeste);
        email = emailRepository.save(email);

        // When & Then
        mockMvc.perform(get("/api/emails/{emailId}", email.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(email.getId()))
                .andExpect(jsonPath("$.email").value("joao@email.com"))
                .andExpect(jsonPath("$.pessoaId").value(pessoaTeste.getId()));
    }

    @Test
    void deveRetornarErroQuandoBuscarEmailInexistente() throws Exception {
        // Given
        Long emailIdInexistente = 99999L;

        // When & Then
        mockMvc.perform(get("/api/emails/{emailId}", emailIdInexistente))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveAtualizarEmailComSucesso() throws Exception {
        // Given
        Email email = new Email();
        email.setEmail("joao@email.com");
        email.setPessoa(pessoaTeste);
        email = emailRepository.save(email);

        EmailInputDTO emailInputDTO = new EmailInputDTO("joao.atualizado@email.com");

        // When & Then
        mockMvc.perform(put("/api/emails/{emailId}", email.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailInputDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(email.getId()))
                .andExpect(jsonPath("$.email").value("joao.atualizado@email.com"))
                .andExpect(jsonPath("$.pessoaId").value(pessoaTeste.getId()));

        // Verifica se foi atualizado no banco
        Email emailAtualizado = emailRepository.findById(email.getId()).orElseThrow();
        assertThat(emailAtualizado.getEmail()).isEqualTo("joao.atualizado@email.com");
    }

    @Test
    void deveRetornarErroQuandoAtualizarEmailInexistente() throws Exception {
        // Given
        Long emailIdInexistente = 99999L;
        EmailInputDTO emailInputDTO = new EmailInputDTO("teste@email.com");

        // When & Then
        mockMvc.perform(put("/api/emails/{emailId}", emailIdInexistente)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailInputDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornarErroQuandoAtualizarComEmailInvalido() throws Exception {
        // Given
        Email email = new Email();
        email.setEmail("joao@email.com");
        email.setPessoa(pessoaTeste);
        email = emailRepository.save(email);

        EmailInputDTO emailInputDTO = new EmailInputDTO("email-invalido");

        // When & Then
        mockMvc.perform(put("/api/emails/{emailId}", email.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailInputDTO)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void deveRemoverEmailComSucesso() throws Exception {
        // Given
        Email email = new Email();
        email.setEmail("joao@email.com");
        email.setPessoa(pessoaTeste);
        email = emailRepository.save(email);

        // When & Then
        mockMvc.perform(delete("/api/emails/{emailId}", email.getId()))
                .andExpect(status().isNoContent());

        // Verifica se foi removido do banco
        assertThat(emailRepository.findById(email.getId())).isEmpty();
    }

    @Test
    void deveRetornarErroQuandoRemoverEmailInexistente() throws Exception {
        // Given
        Long emailIdInexistente = 99999L;

        // When & Then
        mockMvc.perform(delete("/api/emails/{emailId}", emailIdInexistente))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveValidarTamanhoMaximoDoEmail() throws Exception {
        // Given - Email com mais de 100 caracteres
        String emailLongo = "a".repeat(90) + "@email.com"; // 101 caracteres
        EmailInputDTO emailInputDTO = new EmailInputDTO(emailLongo);

        // When & Then
        mockMvc.perform(post("/api/emails/pessoa/{pessoaId}", pessoaTeste.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailInputDTO)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void devePermitirEmailComTamanhoLimite() throws Exception {
        // Given - Email com exatamente 100 caracteres
        String emailLimite = "a".repeat(89) + "@email.com"; // 100 caracteres
        Email email = new Email();
        email.setEmail(emailLimite);
        email.setPessoa(pessoaTeste);
        email = emailRepository.save(email);

        // When & Then
        mockMvc.perform(get("/api/emails/{emailId}", email.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(emailLimite));
    }

    @Test
    void deveProcessarMultiplosEmailsParaMesmaPessoa() throws Exception {
        // Given
        EmailInputDTO email1 = new EmailInputDTO("joao1@email.com");
        EmailInputDTO email2 = new EmailInputDTO("joao2@email.com");
        EmailInputDTO email3 = new EmailInputDTO("joao3@email.com");

        // When - Cria múltiplos emails
        mockMvc.perform(post("/api/emails/pessoa/{pessoaId}", pessoaTeste.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(email1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/emails/pessoa/{pessoaId}", pessoaTeste.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(email2)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/emails/pessoa/{pessoaId}", pessoaTeste.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(email3)))
                .andExpect(status().isCreated());

        // Then - Verifica se todos foram criados
        mockMvc.perform(get("/api/emails/pessoa/{pessoaId}", pessoaTeste.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[*].email", containsInAnyOrder("joao1@email.com", "joao2@email.com", "joao3@email.com")));
    }
}