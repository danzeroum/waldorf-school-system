package com.waldorf.presentation.controller;

import com.waldorf.application.dto.usuario.AlterarSenhaRequestDTO;
import com.waldorf.application.dto.usuario.UsuarioListResponseDTO;
import com.waldorf.application.dto.usuario.UsuarioRequestDTO;
import com.waldorf.application.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
@Tag(name = "Gestao de Usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<UsuarioListResponseDTO>> listarTodos() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<UsuarioListResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<UsuarioListResponseDTO> criar(@Valid @RequestBody UsuarioRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.criar(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<UsuarioListResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody UsuarioRequestDTO dto) {
        return ResponseEntity.ok(usuarioService.atualizar(id, dto));
    }

    @PatchMapping("/{id}/toggle-ativo")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<UsuarioListResponseDTO> toggleAtivo(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.toggleAtivo(id));
    }

    @PostMapping("/{id}/resetar-senha")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Map<String, String>> resetarSenha(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String s = body.get("novaSenha");
        if (s == null || s.isBlank()) return ResponseEntity.badRequest().body(Map.of("erro", "novaSenha obrigatorio"));
        usuarioService.resetarSenha(id, s);
        return ResponseEntity.ok(Map.of("mensagem", "Senha alterada"));
    }

    @PostMapping("/alterar-senha")
    public ResponseEntity<Map<String, String>> alterarSenha(@Valid @RequestBody AlterarSenhaRequestDTO dto) {
        usuarioService.alterarSenha(dto);
        return ResponseEntity.ok(Map.of("mensagem", "Senha alterada"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        usuarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/perfis")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<String>> listarPerfis() {
        return ResponseEntity.ok(usuarioService.listarPerfisDisponiveis());
    }
}
