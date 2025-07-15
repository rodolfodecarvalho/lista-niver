package com.rodolfo.listaniver.service.impl;

import com.rodolfo.listaniver.dto.PessoaInputDTO;
import com.rodolfo.listaniver.dto.PessoaOutputDTO;
import com.rodolfo.listaniver.dto.PessoaUpdateDTO;
import com.rodolfo.listaniver.entity.Pessoa;
import com.rodolfo.listaniver.exception.DuplicatePessoaException;
import com.rodolfo.listaniver.exception.RecordNotFoundException;
import com.rodolfo.listaniver.repository.EmailRepository;
import com.rodolfo.listaniver.repository.PessoaRepository;
import com.rodolfo.listaniver.service.PessoaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.rodolfo.listaniver.dto.PessoaOutputDTO.fromEntity;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PessoaServiceImpl implements PessoaService {

    private final PessoaRepository repository;
    private final EmailRepository emailRepository;

    @Override
    public PessoaOutputDTO criar(PessoaInputDTO inputDTO) {
        log.info("Criando nova pessoa: {}", inputDTO.nome());

        if (repository.existsByNomeAndDataNascimento(inputDTO.nome(), inputDTO.dataNascimento())) {
            throw new DuplicatePessoaException("Pessoa com nome e data de nascimento já existe");
        }

        Pessoa pessoa = new Pessoa();
        pessoa.setNome(inputDTO.nome());
        pessoa.setDataNascimento(inputDTO.dataNascimento());
        pessoa.setEmails(pessoa.convertEmailInputDTOsToEmails(inputDTO.emails()));

        // Salva a pessoa primeiro
        Pessoa savedPessoa = repository.save(pessoa);

        log.info("Pessoa criada com sucesso: ID {}", savedPessoa.getId());
        return fromEntity(savedPessoa);
    }

    @Override
    @Transactional(readOnly = true)
    public PessoaOutputDTO buscarPorId(Long id) {
        log.info("Buscando pessoa por ID: {}", id);

        Pessoa pessoa = repository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Pessoa", id));

        return fromEntity(pessoa);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PessoaOutputDTO> listarTodos() {
        log.info("Listando todas as pessoas");

        return repository.findAll()
                .stream()
                .map(PessoaOutputDTO::fromEntity)
                .toList();
    }

    @Override
    public PessoaOutputDTO atualizar(Long id, PessoaUpdateDTO updateDTO) {
        log.info("Atualizando pessoa ID: {}", id);

        Pessoa pessoa = repository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Pessoa", id));

        // Verifica se já existe outra pessoa com o mesmo nome e data de nascimento
        if (repository.existsByNomeAndDataNascimento(updateDTO.nome(), updateDTO.dataNascimento()) &&
                (!pessoa.getNome().equals(updateDTO.nome()) || !pessoa.getDataNascimento().equals(updateDTO.dataNascimento()))) {
            throw new DuplicatePessoaException("Pessoa com nome e data de nascimento já existe");
        }

        pessoa.setNome(updateDTO.nome());
        pessoa.setDataNascimento(updateDTO.dataNascimento());

        emailRepository.deleteByPessoaId(pessoa.getId());

        pessoa.setEmails(pessoa.convertEmailInputDTOsToEmails(updateDTO.emails()));

        Pessoa updatedPessoa = repository.save(pessoa);

        log.info("Pessoa atualizada com sucesso: ID {}", updatedPessoa.getId());
        return fromEntity(updatedPessoa);
    }

    @Override
    public void deletar(Long id) {
        log.info("Deletando pessoa ID: {}", id);

        if (!repository.existsById(id)) {
            throw new RecordNotFoundException("Pessoa", id);
        }

        repository.deleteById(id);
        log.info("Pessoa deletada com sucesso: ID {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PessoaOutputDTO> buscarPorNome(String nome) {
        log.info("Buscando pessoas por nome: {}", nome);

        return repository.findByNomeContainingIgnoreCase(nome)
                .stream()
                .map(PessoaOutputDTO::fromEntity)
                .toList();
    }
}