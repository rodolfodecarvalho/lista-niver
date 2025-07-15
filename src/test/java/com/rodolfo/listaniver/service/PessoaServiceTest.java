package com.rodolfo.listaniver.service;

import com.rodolfo.listaniver.dto.EmailInputDTO;
import com.rodolfo.listaniver.dto.PessoaInputDTO;
import com.rodolfo.listaniver.dto.PessoaOutputDTO;
import com.rodolfo.listaniver.dto.PessoaUpdateDTO;
import com.rodolfo.listaniver.entity.Email;
import com.rodolfo.listaniver.entity.Pessoa;
import com.rodolfo.listaniver.exception.DuplicatePessoaException;
import com.rodolfo.listaniver.exception.RecordNotFoundException;
import com.rodolfo.listaniver.repository.EmailRepository;
import com.rodolfo.listaniver.repository.PessoaRepository;
import com.rodolfo.listaniver.service.impl.PessoaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do Services de Pessoa")
public class PessoaServiceTest {

    @Mock
    private PessoaRepository repository;

    @Mock
    private EmailRepository emailRepository;

    @InjectMocks
    private PessoaServiceImpl service;

    private Pessoa pessoa;
    private PessoaInputDTO inputDTO;
    private PessoaUpdateDTO updateDTO;

    @BeforeEach
    void setUp() {
        Email email = new Email();
        email.setId(1L);
        email.setEmail("joao@email.com");
        
        pessoa = new Pessoa();
        pessoa.setId(1L);
        pessoa.setNome("João Silva");
        pessoa.setDataNascimento(LocalDate.of(1990, 5, 15));
        pessoa.setEmails(Set.of(email));
        email.setPessoa(pessoa);

        Set<EmailInputDTO> emailsInput = Set.of(new EmailInputDTO("joao@email.com"));
        inputDTO = new PessoaInputDTO("João Silva", LocalDate.of(1990, 5, 15), emailsInput);
        updateDTO = new PessoaUpdateDTO("João Santos", LocalDate.of(1990, 5, 15), emailsInput);
    }

    @Test
    void deveCriarPessoaComSucesso() {
        // Given
        when(repository.existsByNomeAndDataNascimento(anyString(), any(LocalDate.class))).thenReturn(false);
        when(repository.save(any(Pessoa.class))).thenReturn(pessoa);

        // When
        PessoaOutputDTO result = service.criar(inputDTO);

        // Then
        assertNotNull(result);
        assertEquals(pessoa.getId(), result.id());
        assertEquals(pessoa.getNome(), result.nome());
        assertEquals(pessoa.getDataNascimento(), result.dataNascimento());
        assertEquals(1, result.emails().size());
        assertEquals("joao@email.com", result.emails().iterator().next().email());

        verify(repository).existsByNomeAndDataNascimento(inputDTO.nome(), inputDTO.dataNascimento());
        verify(repository).save(any(Pessoa.class));
    }

    @Test
    void deveLancarExcecaoAoCriarPessoaDuplicada() {
        // Given
        when(repository.existsByNomeAndDataNascimento(anyString(), any(LocalDate.class))).thenReturn(true);

        // When & Then
        assertThrows(DuplicatePessoaException.class, () -> service.criar(inputDTO));

        verify(repository).existsByNomeAndDataNascimento(inputDTO.nome(), inputDTO.dataNascimento());
        verify(repository, never()).save(any(Pessoa.class));
    }

    @Test
    void deveBuscarPessoaPorIdComSucesso() {
        // Given
        when(repository.findById(anyLong())).thenReturn(Optional.of(pessoa));

        // When
        PessoaOutputDTO result = service.buscarPorId(1L);

        // Then
        assertNotNull(result);
        assertEquals(pessoa.getId(), result.id());
        assertEquals(pessoa.getNome(), result.nome());
        assertEquals(pessoa.getDataNascimento(), result.dataNascimento());
        assertEquals(1, result.emails().size());
        assertEquals("joao@email.com", result.emails().iterator().next().email());

        verify(repository).findById(1L);
    }

    @Test
    void deveLancarExcecaoAoBuscarPessoaInexistente() {
        // Given
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RecordNotFoundException.class, () -> service.buscarPorId(1L));

        verify(repository).findById(1L);
    }

    @Test
    void deveListarTodasPessoasComSucesso() {
        // Given
        List<Pessoa> pessoas = List.of(pessoa);
        when(repository.findAll()).thenReturn(pessoas);

        // When
        List<PessoaOutputDTO> result = service.listarTodos();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(pessoa.getId(), result.getFirst().id());

        verify(repository).findAll();
    }

    @Test
    void deveAtualizarPessoaComSucesso() {
        // Given
        when(repository.findById(anyLong())).thenReturn(Optional.of(pessoa));
        when(repository.existsByNomeAndDataNascimento(anyString(), any(LocalDate.class))).thenReturn(false);
        when(repository.save(any(Pessoa.class))).thenReturn(pessoa);
        doNothing().when(emailRepository).deleteByPessoaId(anyLong());

        // When
        PessoaOutputDTO result = service.atualizar(1L, updateDTO);

        // Then
        assertNotNull(result);
        verify(repository).findById(1L);
        verify(repository).save(any(Pessoa.class));
    }

    @Test
    void deveLancarExcecaoAoAtualizarPessoaInexistente() {
        // Given
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RecordNotFoundException.class, () -> service.atualizar(1L, updateDTO));

        verify(repository).findById(1L);
        verify(repository, never()).save(any(Pessoa.class));
    }

    @Test
    void deveDeletarPessoaComSucesso() {
        // Given
        when(repository.existsById(anyLong())).thenReturn(true);
        doNothing().when(repository).deleteById(anyLong());

        // When
        service.deletar(1L);

        // Then
        verify(repository).existsById(1L);
        verify(repository).deleteById(1L);
    }

    @Test
    void deveLancarExcecaoAoDeletarPessoaInexistente() {
        // Given
        when(repository.existsById(anyLong())).thenReturn(false);

        // When & Then
        assertThrows(RecordNotFoundException.class, () -> service.deletar(1L));

        verify(repository).existsById(1L);
        verify(repository, never()).deleteById(anyLong());
    }

    @Test
    void deveBuscarPessoasPorNomeComSucesso() {
        // Given
        List<Pessoa> pessoas = List.of(pessoa);
        when(repository.findByNomeContainingIgnoreCase(anyString())).thenReturn(pessoas);

        // When
        List<PessoaOutputDTO> result = service.buscarPorNome("João");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(pessoa.getId(), result.getFirst().id());

        verify(repository).findByNomeContainingIgnoreCase("João");
    }
}