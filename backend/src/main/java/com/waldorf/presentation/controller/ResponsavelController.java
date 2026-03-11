package com.waldorf.presentation.controller;

import com.waldorf.application.dto.responsavel.ResponsavelRequestDTO;
import com.waldorf.application.dto.responsavel.ResponsavelResponseDTO;
import com.waldorf.application.service.ResponsavelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/responsaveis")
@RequiredArgsConstructor
@Tag(name = "Responsáveis", description = "Gestão de responsáveis / pais")
public class ResponsavelController {

    private final ResponsavelService responsavelService;

    @GetMapping
    @Operation(summary = "Listar responsáveis")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA','DIRETOR')")
    public ResponseEntity<Page<ResponsavelResponseDTO>> listar(
            @RequestParam(required = false) String nome,
            Pageable pageable) {
        return ResponseEntity.ok(responsavelService.listar(nome, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA','DIRETOR','PROFESSOR')")
    public ResponseEntity<ResponsavelResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(responsavelService.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Cadastrar responsável")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA')")
    public ResponseEntity<ResponsavelResponseDTO> criar(@Valid @RequestBody ResponsavelRequestDTO dto) {
        ResponsavelResponseDTO criado = responsavelService.criar(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA')")
    public ResponseEntity<ResponsavelResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ResponsavelRequestDTO dto) {
        return ResponseEntity.ok(responsavelService.atualizar(id, dto));
    }
}
