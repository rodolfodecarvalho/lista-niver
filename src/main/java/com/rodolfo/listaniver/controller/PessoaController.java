package com.rodolfo.listaniver.controller;

import com.rodolfo.listaniver.controller.impl.PessoaSwaggerController;
import com.rodolfo.listaniver.dto.PessoaInputDTO;
import com.rodolfo.listaniver.dto.PessoaOutputDTO;
import com.rodolfo.listaniver.dto.PessoaUpdateDTO;
import com.rodolfo.listaniver.service.PessoaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pessoas")
@RequiredArgsConstructor
@Slf4j
public class PessoaController implements PessoaSwaggerController {

    private final PessoaService service;

    @PostMapping
    public ResponseEntity<PessoaOutputDTO> criar(@Valid @RequestBody PessoaInputDTO inputDTO) {
        log.info("Requisição para criar pessoa: {}", inputDTO.nome());
        PessoaOutputDTO result = service.criar(inputDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }


    @GetMapping("/{id}")
    public ResponseEntity<PessoaOutputDTO> buscarPorId(@PathVariable Long id) {
        log.info("Requisição para buscar pessoa por ID: {}", id);
        PessoaOutputDTO result = service.buscarPorId(id);
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<List<PessoaOutputDTO>> listarTodos() {
        log.info("Requisição para listar todas as pessoas");
        List<PessoaOutputDTO> result = service.listarTodos();
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PessoaOutputDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody PessoaUpdateDTO updateDTO) {
        log.info("Requisição para atualizar pessoa ID: {}", id);
        PessoaOutputDTO result = service.atualizar(id, updateDTO);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        log.info("Requisição para deletar pessoa ID: {}", id);
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<PessoaOutputDTO>> buscarPorNome(@RequestParam String nome) {
        log.info("Requisição para buscar pessoas por nome: {}", StringUtils.replaceChars(nome, "\r\n", "__"));
        List<PessoaOutputDTO> result = service.buscarPorNome(nome);
        return ResponseEntity.ok(result);
    }
}