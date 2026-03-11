package br.edu.waldorf.modules.escolar.api;

import br.edu.waldorf.modules.escolar.domain.model.Curso;
import br.edu.waldorf.modules.escolar.domain.repository.CursoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para Cursos
 * Base path: /api/v1/courses
 */
@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
@Tag(name = "Cursos", description = "Etapas Waldorf")
public class CursoController {

    private final CursoRepository cursoRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA','PROFESSOR','DIRECAO')")
    @Operation(summary = "Listar cursos ativos")
    public ResponseEntity<List<Curso>> listar() {
        return ResponseEntity.ok(cursoRepository.findByAtivoTrueOrderByOrdemExibicao());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA','PROFESSOR','DIRECAO')")
    @Operation(summary = "Buscar curso por ID")
    public ResponseEntity<Curso> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(
                cursoRepository.findById(id)
                        .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Curso não encontrado: " + id))
        );
    }
}
