package br.edu.waldorf.core.exception;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Handler global de exceções - retorna respostas RFC 9457 (Problem Details)
 *
 * @author Sistema Waldorf
 * @version 1.0.0
 * @since 2026-03-11
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // --- 404 Not Found ---

    @ExceptionHandler(EntityNotFoundException.class)
    public ProblemDetail handleEntityNotFound(EntityNotFoundException ex, WebRequest request) {
        log.warn("Entidade não encontrada: {}", ex.getMessage());
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        pd.setType(URI.create("https://waldorf.edu.br/errors/not-found"));
        pd.setTitle("Recurso não encontrado");
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }

    // --- 400 Bad Request ---

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Argumento inválido: {}", ex.getMessage());
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        pd.setType(URI.create("https://waldorf.edu.br/errors/bad-request"));
        pd.setTitle("Argumento inválido");
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }

    // --- 422 Unprocessable Entity ---

    @ExceptionHandler(IllegalStateException.class)
    public ProblemDetail handleIllegalState(IllegalStateException ex) {
        log.warn("Estado inválido: {}", ex.getMessage());
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        pd.setType(URI.create("https://waldorf.edu.br/errors/invalid-state"));
        pd.setTitle("Operação inválida");
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }

    // --- 400 Validation ---

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> erros = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String campo = error instanceof FieldError fe ? fe.getField() : error.getObjectName();
            String msg   = error.getDefaultMessage();
            erros.put(campo, msg);
        });
        log.warn("Erro de validação: {}", erros);
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Um ou mais campos estão inválidos"
        );
        pd.setType(URI.create("https://waldorf.edu.br/errors/validation"));
        pd.setTitle("Erro de validação");
        pd.setProperty("campos", erros);
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }

    // --- 403 Forbidden ---

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDenied(AccessDeniedException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN,
                "Você não tem permissão para realizar esta operação"
        );
        pd.setType(URI.create("https://waldorf.edu.br/errors/forbidden"));
        pd.setTitle("Acesso negado");
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }

    // --- 401 Unauthorized ---

    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail handleAuthentication(AuthenticationException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED,
                "Autenticação necessária"
        );
        pd.setType(URI.create("https://waldorf.edu.br/errors/unauthorized"));
        pd.setTitle("Não autenticado");
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }

    // --- 500 Internal Server Error ---

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneral(Exception ex, WebRequest request) {
        log.error("Erro interno não tratado: ", ex);
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocorreu um erro interno. Por favor, tente novamente."
        );
        pd.setType(URI.create("https://waldorf.edu.br/errors/internal"));
        pd.setTitle("Erro interno");
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }
}
