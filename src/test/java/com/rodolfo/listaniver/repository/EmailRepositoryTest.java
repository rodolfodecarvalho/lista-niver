package com.rodolfo.listaniver.repository;

import com.rodolfo.listaniver.entity.Email;
import com.rodolfo.listaniver.entity.Pessoa;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class EmailRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EmailRepository repository;

    @Test
    void findByPessoaIdDeveRetornarEmailsDaPessoa() {
        // Given
        Pessoa pessoa = new Pessoa();
        pessoa.setNome("João Silva");
        pessoa.setDataNascimento(LocalDate.of(1990, 1, 1));
        entityManager.persistAndFlush(pessoa);

        Email email1 = new Email();
        email1.setEmail("joao@email.com");
        email1.setPessoa(pessoa);
        entityManager.persistAndFlush(email1);

        Email email2 = new Email();
        email2.setEmail("joao.silva@email.com");
        email2.setPessoa(pessoa);
        entityManager.persistAndFlush(email2);

        // When
        List<Email> result = repository.findByPessoaId(pessoa.getId());

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Email::getEmail)
                .containsExactlyInAnyOrder("joao@email.com", "joao.silva@email.com");
    }

    @Test
    void findByPessoaIdDeveRetornarListaVaziaQuandoPessoaNaoTemEmails() {
        // Given
        Pessoa pessoa = new Pessoa();
        pessoa.setNome("João Silva");
        pessoa.setDataNascimento(LocalDate.of(1990, 1, 1));
        entityManager.persistAndFlush(pessoa);

        // When
        List<Email> result = repository.findByPessoaId(pessoa.getId());

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void deleteByPessoaIdDeveRemoverTodosEmailsDaPessoa() {
        // Given
        Pessoa pessoa = new Pessoa();
        pessoa.setNome("João Silva");
        pessoa.setDataNascimento(LocalDate.of(1990, 1, 1));
        entityManager.persistAndFlush(pessoa);

        Email email1 = new Email();
        email1.setEmail("joao@email.com");
        email1.setPessoa(pessoa);
        entityManager.persistAndFlush(email1);

        Email email2 = new Email();
        email2.setEmail("joao.silva@email.com");
        email2.setPessoa(pessoa);
        entityManager.persistAndFlush(email2);

        // When
        repository.deleteByPessoaId(pessoa.getId());
        entityManager.flush();

        // Then
        List<Email> result = repository.findByPessoaId(pessoa.getId());
        assertThat(result).isEmpty();
    }

    @Test
    void deleteByPessoaIdNaoDeveAfetarEmailsDeOutrasPessoas() {
        // Given
        Pessoa pessoa1 = new Pessoa();
        pessoa1.setNome("João Silva");
        pessoa1.setDataNascimento(LocalDate.of(1990, 1, 1));
        entityManager.persistAndFlush(pessoa1);

        Pessoa pessoa2 = new Pessoa();
        pessoa2.setNome("Maria Santos");
        pessoa2.setDataNascimento(LocalDate.of(1985, 5, 10));
        entityManager.persistAndFlush(pessoa2);

        Email email1 = new Email();
        email1.setEmail("joao@email.com");
        email1.setPessoa(pessoa1);
        entityManager.persistAndFlush(email1);

        Email email2 = new Email();
        email2.setEmail("maria@email.com");
        email2.setPessoa(pessoa2);
        entityManager.persistAndFlush(email2);

        // When
        repository.deleteByPessoaId(pessoa1.getId());
        entityManager.flush();

        // Then
        List<Email> emailsPessoa1 = repository.findByPessoaId(pessoa1.getId());
        List<Email> emailsPessoa2 = repository.findByPessoaId(pessoa2.getId());
        
        assertThat(emailsPessoa1).isEmpty();
        assertThat(emailsPessoa2).hasSize(1);
        assertThat(emailsPessoa2.getFirst().getEmail()).isEqualTo("maria@email.com");
    }
}