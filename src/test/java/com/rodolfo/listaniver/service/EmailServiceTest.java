package com.rodolfo.listaniver.service;


import com.rodolfo.listaniver.dto.EmailInputDTO;
import com.rodolfo.listaniver.dto.EmailOutputDTO;
import com.rodolfo.listaniver.entity.Email;
import com.rodolfo.listaniver.entity.Pessoa;
import com.rodolfo.listaniver.exception.RecordNotFoundException;
import com.rodolfo.listaniver.repository.EmailRepository;
import com.rodolfo.listaniver.repository.PessoaRepository;
import com.rodolfo.listaniver.service.impl.EmailServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    @Mock
    private EmailRepository emailRepository;

    @Mock
    private PessoaRepository pessoaRepository;

    @InjectMocks
    private EmailServiceImpl emailService;

    @Test
    void adicionarEmailDeveRetornarEmailOutputDTOQuandoPessoaExiste() {
        // Given
        Long pessoaId = 1L;
        EmailInputDTO emailInput = new EmailInputDTO("test@email.com");

        Pessoa pessoa = new Pessoa();
        pessoa.setId(pessoaId);
        pessoa.setNome("João Silva");
        pessoa.setDataNascimento(LocalDate.of(1990, 1, 1));

        Email emailToSave = new Email();
        emailToSave.setEmail(emailInput.email());
        emailToSave.setPessoa(pessoa);

        Email savedEmail = new Email();
        savedEmail.setId(1L);
        savedEmail.setEmail(emailInput.email());
        savedEmail.setPessoa(pessoa);

        given(pessoaRepository.findById(pessoaId)).willReturn(Optional.of(pessoa));
        given(emailRepository.save(any(Email.class))).willReturn(savedEmail);

        // When
        EmailOutputDTO result = emailService.adicionarEmail(pessoaId, emailInput);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.email()).isEqualTo("test@email.com");
        assertThat(result.pessoaId()).isEqualTo(pessoaId);

        verify(pessoaRepository).findById(pessoaId);
        verify(emailRepository).save(any(Email.class));
    }

    @Test
    void adicionarEmailDeveLancarRecordNotFoundExceptionQuandoPessoaNaoExiste() {
        // Given
        Long pessoaId = 999L;
        EmailInputDTO emailInput = new EmailInputDTO("test@email.com");

        given(pessoaRepository.findById(pessoaId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> emailService.adicionarEmail(pessoaId, emailInput))
                .isInstanceOf(RecordNotFoundException.class)
                .hasMessage("Pessoa not found with id: " + pessoaId);

        verify(pessoaRepository).findById(pessoaId);
        verify(emailRepository, never()).save(any(Email.class));
    }

    @Test
    void listarEmailsPorPessoaDeveRetornarListaEmailsQuandoPessoaExiste() {
        // Given
        Long pessoaId = 1L;

        Pessoa pessoa = new Pessoa();
        pessoa.setId(pessoaId);

        Email email1 = new Email();
        email1.setId(1L);
        email1.setEmail("email1@test.com");
        email1.setPessoa(pessoa);

        Email email2 = new Email();
        email2.setId(2L);
        email2.setEmail("email2@test.com");
        email2.setPessoa(pessoa);

        List<Email> emails = List.of(email1, email2);

        given(pessoaRepository.existsById(pessoaId)).willReturn(true);
        given(emailRepository.findByPessoaId(pessoaId)).willReturn(emails);

        // When
        List<EmailOutputDTO> result = emailService.listarEmailsPorPessoa(pessoaId);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).id()).isEqualTo(1L);
        assertThat(result.get(0).email()).isEqualTo("email1@test.com");
        assertThat(result.get(1).id()).isEqualTo(2L);
        assertThat(result.get(1).email()).isEqualTo("email2@test.com");

        verify(pessoaRepository).existsById(pessoaId);
        verify(emailRepository).findByPessoaId(pessoaId);
    }

    @Test
    void listarEmailsPorPessoaDeveLancarRecordNotFoundExceptionQuandoPessoaNaoExiste() {
        // Given
        Long pessoaId = 999L;

        given(pessoaRepository.existsById(pessoaId)).willReturn(false);

        // When & Then
        assertThatThrownBy(() -> emailService.listarEmailsPorPessoa(pessoaId))
                .isInstanceOf(RecordNotFoundException.class)
                .hasMessage("Pessoa not found with id: " + pessoaId);

        verify(pessoaRepository).existsById(pessoaId);
        verify(emailRepository, never()).findByPessoaId(pessoaId);
    }

    @Test
    void buscarPorIdDeveRetornarEmailOutputDTOQuandoEmailExiste() {
        // Given
        Long emailId = 1L;

        Pessoa pessoa = new Pessoa();
        pessoa.setId(1L);

        Email email = new Email();
        email.setId(emailId);
        email.setEmail("test@email.com");
        email.setPessoa(pessoa);

        given(emailRepository.findById(emailId)).willReturn(Optional.of(email));

        // When
        EmailOutputDTO result = emailService.buscarPorId(emailId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(emailId);
        assertThat(result.email()).isEqualTo("test@email.com");
        assertThat(result.pessoaId()).isEqualTo(1L);

        verify(emailRepository).findById(emailId);
    }

    @Test
    void buscarPorIdDeveLancarRecordNotFoundExceptionQuandoEmailNaoExiste() {
        // Given
        Long emailId = 999L;

        given(emailRepository.findById(emailId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> emailService.buscarPorId(emailId))
                .isInstanceOf(RecordNotFoundException.class)
                .hasMessage("Email not found with id: " + emailId);

        verify(emailRepository).findById(emailId);
    }

    @Test
    void atualizarEmailDeveRetornarEmailAtualizadoQuandoEmailExiste() {
        // Given
        Long emailId = 1L;
        EmailInputDTO emailInput = new EmailInputDTO("updated@email.com");

        Pessoa pessoa = new Pessoa();
        pessoa.setId(1L);

        Email emailExistente = new Email();
        emailExistente.setId(emailId);
        emailExistente.setEmail("old@email.com");
        emailExistente.setPessoa(pessoa);

        Email emailAtualizado = new Email();
        emailAtualizado.setId(emailId);
        emailAtualizado.setEmail("updated@email.com");
        emailAtualizado.setPessoa(pessoa);

        given(emailRepository.findById(emailId)).willReturn(Optional.of(emailExistente));
        given(emailRepository.save(any(Email.class))).willReturn(emailAtualizado);

        // When
        EmailOutputDTO result = emailService.atualizarEmail(emailId, emailInput);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(emailId);
        assertThat(result.email()).isEqualTo("updated@email.com");
        assertThat(result.pessoaId()).isEqualTo(1L);

        verify(emailRepository).findById(emailId);
        verify(emailRepository).save(emailExistente);
        assertThat(emailExistente.getEmail()).isEqualTo("updated@email.com");
    }

    @Test
    void atualizarEmailDeveLancarRecordNotFoundExceptionQuandoEmailNaoExiste() {
        // Given
        Long emailId = 999L;
        EmailInputDTO emailInput = new EmailInputDTO("test@email.com");

        given(emailRepository.findById(emailId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> emailService.atualizarEmail(emailId, emailInput))
                .isInstanceOf(RecordNotFoundException.class)
                .hasMessage("Email not found with id: " + emailId);

        verify(emailRepository).findById(emailId);
        verify(emailRepository, never()).save(any(Email.class));
    }

    @Test
    void removerEmailDeveRemoverEmailQuandoEmailExiste() {
        // Given
        Long emailId = 1L;

        given(emailRepository.existsById(emailId)).willReturn(true);

        // When
        emailService.removerEmail(emailId);

        // Then
        verify(emailRepository).existsById(emailId);
        verify(emailRepository).deleteById(emailId);
    }

    @Test
    void removerEmailDeveLancarRecordNotFoundExceptionQuandoEmailNaoExiste() {
        // Given
        Long emailId = 999L;

        given(emailRepository.existsById(emailId)).willReturn(false);

        // When & Then
        assertThatThrownBy(() -> emailService.removerEmail(emailId))
                .isInstanceOf(RecordNotFoundException.class)
                .hasMessage("Email not found with id: " + emailId);

        verify(emailRepository).existsById(emailId);
        verify(emailRepository, never()).deleteById(emailId);
    }
}