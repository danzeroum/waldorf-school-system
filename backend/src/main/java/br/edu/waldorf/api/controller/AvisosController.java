package br.edu.waldorf.api.controller;

import com.waldorf.application.dto.AvisoDTO;
import com.waldorf.application.dto.CreateAvisoRequest;
import com.waldorf.application.service.AvisoService;
import com.waldorf.domain.entity.Usuario;
import com.waldorf.infrastructure.repository.UsuarioRepository;
import com.waldorf.infrastructure.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/avisos")
@RequiredArgsConstructor
@Tag(name = "Avisos", description = "Avisos e comunicados do mural da escola")
public class AvisosController {

    private final AvisoService avisoService;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;

    @GetMapping
    @Operation(summary = "Lista avisos, filtrados opcionalmente por turma")
    public ResponseEntity<List<AvisoDTO>> listar(
            @RequestParam(required = false) Long turmaId) {
        return ResponseEntity.ok(avisoService.listar(turmaId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','COORDENADOR','PROFESSOR')")
    @Operation(summary = "Cria novo aviso")
    public ResponseEntity<AvisoDTO> criar(
            @RequestBody CreateAvisoRequest req,
            HttpServletRequest httpReq) {
        Long autorId = extrairUsuarioId(httpReq);
        return ResponseEntity.status(HttpStatus.CREATED).body(avisoService.criar(req, autorId));
    }

    @PostMapping("/{id}/lido")
    @Operation(summary = "Marca aviso como lido pelo usuário autenticado")
    public ResponseEntity<Void> marcarLido(@PathVariable Long id) {
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','COORDENADOR')")
    @Operation(summary = "Remove aviso")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        avisoService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    private Long extrairUsuarioId(HttpServletRequest req) {
        String header = req.getHeader("Authorization");
        String token  = header != null && header.startsWith("Bearer ") ? header.substring(7) : "";
        String email  = jwtService.extractUsername(token);
        Usuario u = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + email));
        return u.getId();
    }
}
