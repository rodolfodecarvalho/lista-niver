package com.rodolfo.listaniver.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(RecordNotFoundException.class)
    public ResponseEntity<ProblemDetail> recordNotFoundException(RecordNotFoundException ex) {
        log.error("Record Not Found: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Record Not Found");
        problemDetail.setType(URI.create("errors/not-found"));
        problemDetail.setProperty("timestamp", Instant.now());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }

    @ExceptionHandler(DuplicatePessoaException.class)
    public ResponseEntity<ProblemDetail> handleDuplicatePessoaException(DuplicatePessoaException ex) {
        log.error("Pessoa duplicada: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problemDetail.setTitle("Pessoa duplicada");
        problemDetail.setType(URI.create("errors/conflict"));
        problemDetail.setProperty("timestamp", Instant.now());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(problemDetail);
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<ProblemDetail> handleDateTimeParseException(DateTimeParseException ex) {
        log.error("Erro de parsing de data: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setTitle("Bad Request");
        problemDetail.setType(URI.create("errors/bad-request"));
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("message", "Formato de data inválido. Use o formato: yyyy-MM-dd (exemplo: 2024-01-15)");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidationException(MethodArgumentNotValidException ex) {
        log.error("Erro de validação: {}", ex.getMessage());

        Map<String, String> errors = extractValidationErrors(ex);
        String details = buildErrorDetails(ex);

        ProblemDetail problemDetail = createValidationProblemDetail(details, errors);

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(problemDetail);
    }

    private Map<String, String> extractValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> 
            errors.put(((FieldError) error).getField(), error.getDefaultMessage())
        );
        return errors;
    }

    private ProblemDetail createValidationProblemDetail(String details, Map<String, String> errors) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.UNPROCESSABLE_ENTITY, "Erro de validação"
        );
        problemDetail.setTitle("Validation error");
        problemDetail.setType(URI.create("errors/unprocessable-entity"));
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setDetail(details);
        problemDetail.setProperty("errors", errors);
        return problemDetail;
    }

    private String buildErrorDetails(MethodArgumentNotValidException ex) {
        return Optional.of(ex.getDetailMessageArguments())
                .map(args -> Arrays.stream(args)
                        .filter(msg -> !ObjectUtils.isEmpty(msg))
                        .reduce("Certifique-se de fornecer uma requisição válida", (a, b) -> a + " " + b)
                )
                .orElse("").toString();
    }
}
