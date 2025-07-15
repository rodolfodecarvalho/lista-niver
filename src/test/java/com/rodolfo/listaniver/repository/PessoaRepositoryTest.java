package com.rodolfo.listaniver.repository;

import com.rodolfo.listaniver.entity.Pessoa;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class PessoaRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PessoaRepository repository;

    @Test
    void findByNomeContainingIgnoreCaseDeveRetornarPessoasComNomeContendo() {
        // Given
        Pessoa pessoa1 = new Pessoa();
        pessoa1.setNome("João Silva");
        pessoa1.setDataNascimento(LocalDate.of(1990, 1, 1));
        entityManager.persistAndFlush(pessoa1);

        Pessoa pessoa2 = new Pessoa();
        pessoa2.setNome("Maria João");
        pessoa2.setDataNascimento(LocalDate.of(1985, 5, 10));
        entityManager.persistAndFlush(pessoa2);

        Pessoa pessoa3 = new Pessoa();
        pessoa3.setNome("Pedro Santos");
        pessoa3.setDataNascimento(LocalDate.of(1992, 3, 15));
        entityManager.persistAndFlush(pessoa3);

        // When
        List<Pessoa> result = repository.findByNomeContainingIgnoreCase("joão");

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Pessoa::getNome)
                .containsExactlyInAnyOrder("João Silva", "Maria João");
    }

    @Test
    void findByNomeContainingIgnoreCaseDeveRetornarListaVaziaQuandoNaoEncontrar() {
        // Given
        Pessoa pessoa = new Pessoa();
        pessoa.setNome("João Silva");
        pessoa.setDataNascimento(LocalDate.of(1990, 1, 1));
        entityManager.persistAndFlush(pessoa);

        // When
        List<Pessoa> result = repository.findByNomeContainingIgnoreCase("inexistente");

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void existsByNomeAndDataNascimentoDeveRetornarTrueQuandoExistir() {
        // Given
        Pessoa pessoa = new Pessoa();
        pessoa.setNome("João Silva");
        pessoa.setDataNascimento(LocalDate.of(1990, 1, 1));
        entityManager.persistAndFlush(pessoa);

        // When
        boolean exists = repository.existsByNomeAndDataNascimento("João Silva", LocalDate.of(1990, 1, 1));

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void existsByNomeAndDataNascimentoDeveRetornarFalseQuandoNaoExistir() {
        // Given
        Pessoa pessoa = new Pessoa();
        pessoa.setNome("João Silva");
        pessoa.setDataNascimento(LocalDate.of(1990, 1, 1));
        entityManager.persistAndFlush(pessoa);

        // When
        boolean exists = repository.existsByNomeAndDataNascimento("Maria Santos", LocalDate.of(1990, 1, 1));

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void existsByNomeAndDataNascimentoDeveRetornarFalseQuandoNomeIgualMasDataDiferente() {
        // Given
        Pessoa pessoa = new Pessoa();
        pessoa.setNome("João Silva");
        pessoa.setDataNascimento(LocalDate.of(1990, 1, 1));
        entityManager.persistAndFlush(pessoa);

        // When
        boolean exists = repository.existsByNomeAndDataNascimento("João Silva", LocalDate.of(1991, 1, 1));

        // Then
        assertThat(exists).isFalse();
    }
}