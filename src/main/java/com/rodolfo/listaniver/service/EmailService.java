package com.rodolfo.listaniver.service;

import com.rodolfo.listaniver.dto.EmailInputDTO;
import com.rodolfo.listaniver.dto.EmailOutputDTO;

import java.util.List;

public interface EmailService {

    EmailOutputDTO adicionarEmail(Long pessoaId, EmailInputDTO emailInputDTO);

    List<EmailOutputDTO> listarEmailsPorPessoa(Long pessoaId);

    void removerEmail(Long emailId);

    EmailOutputDTO buscarPorId(Long emailId);

    EmailOutputDTO atualizarEmail(Long emailId, EmailInputDTO emailInputDTO);
}