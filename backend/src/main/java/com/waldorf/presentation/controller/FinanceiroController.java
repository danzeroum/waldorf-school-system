package com.waldorf.presentation.controller;

import com.waldorf.application.dto.financeiro.ContratoRequestDTO;
import com.waldorf.application.dto.financeiro.ContratoResponseDTO;
import com.waldorf.application.service.ContratoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/financeiro/")
@RequiredArgsConstructor
@Tag(name = "Financeiro")
public class FinanceiroController {

    private final ContratoService contratoService;

    @GetMapping("/contratos")
    @Operation(summary = "Listar contratos")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA','DIRETOR','FINANCEIRO')")
    public ResponseEntity<List<ContratoResponseDTO>> listar(
            @RequestParam(required = false) Long alunoId) {
        return ResponseEntity.ok(contratoService.listar(alunoId));
    }

    @PostMapping("/contratos")
    @Operation(summary = "Criar contrato")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA','FINANCEIRO')")
    public ResponseEntity<ContratoResponseDTO> criar(@Valid @RequestBody ContratoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contratoService.criar(dto));
    }

    @GetMapping("/contratos/{id}")
    @Operation(summary = "Buscar contrato por ID")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA','DIRETOR','FINANCEIRO')")
    public ResponseEntity<ContratoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(contratoService.buscarPorId(id));
    }
}
