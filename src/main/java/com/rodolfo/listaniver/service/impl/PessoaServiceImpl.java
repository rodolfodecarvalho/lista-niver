package com.rodolfo.listaniver.service.impl;

import com.rodolfo.listaniver.dto.PessoaInputDTO;
import com.rodolfo.listaniver.dto.PessoaOutputDTO;
import com.rodolfo.listaniver.dto.PessoaUpdateDTO;
import com.rodolfo.listaniver.entity.Pessoa;
import com.rodolfo.listaniver.exception.RecordNotFoundException;
import com.rodolfo.listaniver.mapper.PessoaMapper;
import com.rodolfo.listaniver.repository.EmailRepository;
import com.rodolfo.listaniver.repository.PessoaRepository;
import com.rodolfo.listaniver.service.PessoaService;
import com.rodolfo.listaniver.validator.PessoaValidator;
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
    private final PessoaMapper mapper;
    private final PessoaValidator validator;

    @Override
    public PessoaOutputDTO criar(PessoaInputDTO inputDTO) {
        log.info("Criando nova pessoa: {}", inputDTO.nome());

        validator.validateDuplicatePessoa(inputDTO.nome(), inputDTO.dataNascimento());

        Pessoa pessoa = mapper.toEntity(inputDTO);
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

        Pessoa pessoa = validator.findPessoaById(id);
        validator.validateDuplicatePessoaForUpdate(pessoa, updateDTO.nome(), updateDTO.dataNascimento());

        updatePessoaData(pessoa, updateDTO);
        Pessoa updatedPessoa = repository.save(pessoa);

        log.info("Pessoa atualizada com sucesso: ID {}", updatedPessoa.getId());
        return fromEntity(updatedPessoa);
    }

    @Override
    public void deletar(Long id) {
        log.info("Deletando pessoa ID: {}", id);

        validator.validatePessoaExists(id);
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

    private void updatePessoaData(Pessoa pessoa, PessoaUpdateDTO updateDTO) {
        pessoa.setNome(updateDTO.nome());
        pessoa.setDataNascimento(updateDTO.dataNascimento());

        emailRepository.deleteByPessoaId(pessoa.getId());
        pessoa.setEmails(mapper.convertEmailInputDTOsToEmails(updateDTO.emails(), pessoa));
    }
}