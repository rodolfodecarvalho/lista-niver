package com.rodolfo.listaniver.controller;

import com.rodolfo.listaniver.dto.EmailInputDTO;
import com.rodolfo.listaniver.dto.EmailOutputDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "Email", description = "API para gerenciamento de emails das pessoas")
public interface EmailController {

    @Operation(summary = "Adicionar novo email", description = "Adiciona um novo email para uma pessoa específica")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Email criado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmailOutputDTO.class))), @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos", content = @Content), @ApiResponse(responseCode = "404", description = "Pessoa não encontrada", content = @Content)})
    ResponseEntity<EmailOutputDTO> adicionarEmail(@Parameter(description = "ID da pessoa", required = true) @PathVariable Long pessoaId, @Parameter(description = "Dados do email a ser criado", required = true) @Valid @RequestBody EmailInputDTO emailInputDTO);

    @Operation(summary = "Listar emails por pessoa", description = "Retorna todos os emails de uma pessoa específica")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Lista de emails retornada com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmailOutputDTO.class))), @ApiResponse(responseCode = "404", description = "Pessoa não encontrada", content = @Content)})
    ResponseEntity<List<EmailOutputDTO>> listarEmailsPorPessoa(@Parameter(description = "ID da pessoa", required = true) @PathVariable Long pessoaId);

    @Operation(summary = "Buscar email por ID", description = "Retorna um email específico pelo seu ID")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Email encontrado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmailOutputDTO.class))), @ApiResponse(responseCode = "404", description = "Email não encontrado", content = @Content)})
    ResponseEntity<EmailOutputDTO> buscarEmailPorId(@Parameter(description = "ID do email", required = true) @PathVariable Long emailId);

    @Operation(summary = "Atualizar email", description = "Atualiza os dados de um email existente")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Email atualizado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmailOutputDTO.class))), @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos", content = @Content), @ApiResponse(responseCode = "404", description = "Email não encontrado", content = @Content)})
    ResponseEntity<EmailOutputDTO> atualizarEmail(@Parameter(description = "ID do email", required = true) @PathVariable Long emailId, @Parameter(description = "Novos dados do email", required = true) @Valid @RequestBody EmailInputDTO emailInputDTO);

    @Operation(summary = "Remover email", description = "Remove um email específico do sistema")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Email removido com sucesso", content = @Content), @ApiResponse(responseCode = "404", description = "Email não encontrado", content = @Content)})
    ResponseEntity<Void> removerEmail(@Parameter(description = "ID do email", required = true) @PathVariable Long emailId);
}