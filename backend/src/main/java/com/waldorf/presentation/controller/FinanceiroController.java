package com.waldorf.presentation.controller;

import com.waldorf.application.dto.financeiro.ContratoRequestDTO;
import com.waldorf.application.dto.financeiro.ContratoResponseDTO;
import com.waldorf.application.service.ContratoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/finance")
@RequiredArgsConstructor
@Tag(name = "Financeiro")
public class FinanceiroController {

    private final ContratoService contratoService;

    @GetMapping("/contracts")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA','DIRETOR')")
    public ResponseEntity<List<ContratoResponseDTO>> listar() {
        return ResponseEntity.ok(List.of());
    }

    @PostMapping("/contracts")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA')")
    public ResponseEntity<ContratoResponseDTO> criar(@Valid @RequestBody ContratoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contratoService.criar(dto));
    }

    @GetMapping("/invoices")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA','DIRETOR','PAIS')")
    public ResponseEntity<List<?>> listarParcelas() {
        return ResponseEntity.ok(List.of());
    }
}
