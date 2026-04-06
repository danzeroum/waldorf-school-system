package com.waldorf.presentation.controller;
import com.waldorf.application.dto.professor.ProfessorRequestDTO;
import com.waldorf.application.dto.professor.ProfessorResponseDTO;
import com.waldorf.application.service.ProfessorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/professores") @RequiredArgsConstructor @Tag(name="Professores")
public class ProfessorController {
    private final ProfessorService service;
    @GetMapping @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA','DIRETOR')") public ResponseEntity<Page<ProfessorResponseDTO>> listar(Pageable p) { return ResponseEntity.ok(service.listar(p)); }
    @GetMapping("/{id}") @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA','DIRETOR','PROFESSOR')") public ResponseEntity<ProfessorResponseDTO> buscar(@PathVariable Long id) { return ResponseEntity.ok(service.buscarPorId(id)); }
    @PostMapping @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA')") public ResponseEntity<ProfessorResponseDTO> criar(@Valid @RequestBody ProfessorRequestDTO dto) { return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto)); }
    @PutMapping("/{id}") @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA')") public ResponseEntity<ProfessorResponseDTO> atualizar(@PathVariable Long id,@Valid @RequestBody ProfessorRequestDTO dto) { return ResponseEntity.ok(service.atualizar(id,dto)); }
    @DeleteMapping("/{id}") @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA')") public ResponseEntity<Void> inativar(@PathVariable Long id) { service.inativar(id); return ResponseEntity.noContent().build(); }
}
