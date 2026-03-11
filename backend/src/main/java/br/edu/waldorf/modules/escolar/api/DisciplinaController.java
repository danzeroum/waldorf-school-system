package br.edu.waldorf.modules.escolar.api;

import br.edu.waldorf.modules.escolar.domain.model.Disciplina;
import br.edu.waldorf.modules.escolar.domain.repository.DisciplinaRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para Disciplinas
 * Base path: /api/v1/disciplines
 */
@RestController
@RequestMapping("/api/v1/disciplines")
@RequiredArgsConstructor
@Tag(name = "Disciplinas", description = "Áreas do conhecimento Waldorf")
public class DisciplinaController {

    private final DisciplinaRepository disciplinaRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA','PROFESSOR','DIRECAO')")
    @Operation(summary = "Listar disciplinas ativas")
    public ResponseEntity<List<Disciplina>> listar() {
        return ResponseEntity.ok(disciplinaRepository.findByAtivoTrueOrderByNome());
    }

    @GetMapping("/area/{area}")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA','PROFESSOR','DIRECAO')")
    @Operation(summary = "Listar disciplinas por área do conhecimento")
    public ResponseEntity<List<Disciplina>> listarPorArea(@PathVariable Disciplina.AreaConhecimento area) {
        return ResponseEntity.ok(disciplinaRepository.findByAreaConhecimento(area));
    }
}
