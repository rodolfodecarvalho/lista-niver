package com.rodolfo.listaniver.controller;

import com.rodolfo.listaniver.dto.PessoaInputDTO;
import com.rodolfo.listaniver.dto.PessoaOutputDTO;
import com.rodolfo.listaniver.dto.PessoaUpdateDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Pessoa", description = "API para gerenciamento de pessoas")
public interface PessoaController {

    @Operation(summary = "Criar nova pessoa", description = "Cria uma nova pessoa no sistema")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Pessoa criada com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PessoaOutputDTO.class))), @ApiResponse(responseCode = "400", description = "Data inválida", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))), @ApiResponse(responseCode = "422", description = "Dados inválidos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))), @ApiResponse(responseCode = "409", description = "Pessoa já existe", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))})
    @PostMapping
    ResponseEntity<PessoaOutputDTO> criar(@Valid @RequestBody PessoaInputDTO inputDTO);

    @Operation(summary = "Buscar pessoa por ID", description = "Busca uma pessoa específica pelo ID")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Pessoa encontrada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PessoaOutputDTO.class))), @ApiResponse(responseCode = "404", description = "Pessoa não encontrada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))})
    @GetMapping("/{id}")
    ResponseEntity<PessoaOutputDTO> buscarPorId(@Parameter(description = "ID da pessoa", required = true, example = "1") @PathVariable Long id);

    @Operation(summary = "Listar todas as pessoas", description = "Retorna uma lista com todas as pessoas cadastradas")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Lista de pessoas retornada com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PessoaOutputDTO.class)))})
    @GetMapping
    ResponseEntity<List<PessoaOutputDTO>> listarTodos();

    @Operation(summary = "Atualizar pessoa", description = "Atualiza os dados de uma pessoa existente")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Pessoa atualizada com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PessoaOutputDTO.class))), @ApiResponse(responseCode = "400", description = "Data inválida", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))), @ApiResponse(responseCode = "422", description = "Dados inválidos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))), @ApiResponse(responseCode = "404", description = "Pessoa não encontrada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))), @ApiResponse(responseCode = "409", description = "Pessoa com dados duplicados já existe", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))})
    @PutMapping("/{id}")
    ResponseEntity<PessoaOutputDTO> atualizar(@Parameter(description = "ID da pessoa", required = true, example = "1") @PathVariable Long id, @Valid @RequestBody PessoaUpdateDTO updateDTO);

    @Operation(summary = "Deletar pessoa", description = "Remove uma pessoa do sistema")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Pessoa deletada com sucesso"), @ApiResponse(responseCode = "404", description = "Pessoa não encontrada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))})
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deletar(@Parameter(description = "ID da pessoa", required = true, example = "1") @PathVariable Long id);

    @Operation(summary = "Buscar pessoas por nome", description = "Busca pessoas que contenham o nome especificado")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Lista de pessoas encontradas", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PessoaOutputDTO.class)))})
    @GetMapping("/buscar")
    ResponseEntity<List<PessoaOutputDTO>> buscarPorNome(@Parameter(description = "Nome ou parte do nome da pessoa", required = true, example = "João") @RequestParam String nome);
}