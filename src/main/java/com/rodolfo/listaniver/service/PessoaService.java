package com.rodolfo.listaniver.service;

import com.rodolfo.listaniver.dto.PessoaInputDTO;
import com.rodolfo.listaniver.dto.PessoaOutputDTO;
import com.rodolfo.listaniver.dto.PessoaUpdateDTO;

import java.util.List;

public interface PessoaService {
    PessoaOutputDTO criar(PessoaInputDTO inputDTO);

    PessoaOutputDTO buscarPorId(Long id);

    List<PessoaOutputDTO> listarTodos();

    PessoaOutputDTO atualizar(Long id, PessoaUpdateDTO updateDTO);

    void deletar(Long id);

    List<PessoaOutputDTO> buscarPorNome(String nome);
}