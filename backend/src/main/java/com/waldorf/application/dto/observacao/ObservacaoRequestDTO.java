package com.waldorf.application.dto.observacao;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/**
 * DTO de entrada para criacao/atualizacao de observacoes pedagogicas.
 *
 * professorId e opcional: se ausente, o servico usa fallback para o
 * primeiro professor ativo. Em producao, sera resolvido via JWT
 * (@AuthenticationPrincipal ou SecurityContextHolder).
 */
public record ObservacaoRequestDTO(
        @NotNull Long alunoId,
        Long professorId,
        @NotBlank String aspecto,
        @NotBlank String conteudo,
        boolean privada,
        @NotNull LocalDate data
) {}
