package br.edu.waldorf.modules.escolar.api;

import br.edu.waldorf.modules.escolar.api.dto.MatriculaResponseDTO;
import br.edu.waldorf.modules.escolar.domain.model.Matricula;
import br.edu.waldorf.modules.escolar.domain.service.MatriculaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

/**
 * Controller REST para Matrículas
 * Base path: /api/v1/enrollments
 *
 * @author Sistema Waldorf
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/v1/enrollments")
@RequiredArgsConstructor
@Tag(name = "Matrículas", description = "Gestão de matrículas")
public class MatriculaController {

    private final MatriculaService matriculaService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA','DIRECAO')")
    @Operation(summary = "Listar matrículas com filtros")
    public ResponseEntity<Page<MatriculaResponseDTO>> listar(
            @RequestParam(required = false) Long turmaId,
            @RequestParam(required = false) Integer anoLetivo,
            @RequestParam(required = false) Matricula.SituacaoMatricula situacao,
            @PageableDefault(size = 25) Pageable pageable
    ) {
        Page<Matricula> page = matriculaService.listarComFiltros(turmaId, anoLetivo, situacao, pageable);
        return ResponseEntity.ok(page.map(this::toDTO));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA','DIRECAO')")
    @Operation(summary = "Buscar matrícula por ID")
    public ResponseEntity<MatriculaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(toDTO(matriculaService.buscarPorId(id)));
    }

    @GetMapping("/student/{alunoId}/history")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA','PROFESSOR','DIRECAO')")
    @Operation(summary = "Histórico de matrículas do aluno")
    public ResponseEntity<List<MatriculaResponseDTO>> historico(@PathVariable Long alunoId) {
        return ResponseEntity.ok(
                matriculaService.historicoAluno(alunoId).stream().map(this::toDTO).toList()
        );
    }

    @GetMapping("/class/{turmaId}")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA','PROFESSOR','DIRECAO')")
    @Operation(summary = "Listar matrículas ativas de uma turma")
    public ResponseEntity<List<MatriculaResponseDTO>> listarPorTurma(@PathVariable Long turmaId) {
        return ResponseEntity.ok(
                matriculaService.listarAtivasPorTurma(turmaId).stream().map(this::toDTO).toList()
        );
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA')")
    @Operation(summary = "Realizar matrícula de aluno em turma")
    public ResponseEntity<MatriculaResponseDTO> matricular(
            @RequestBody Map<String, Object> payload,
            UriComponentsBuilder ucb
    ) {
        Long alunoId = Long.valueOf(payload.get("alunoId").toString());
        Long turmaId = Long.valueOf(payload.get("turmaId").toString());
        String formaStr = payload.getOrDefault("formaIngresso", "NOVA").toString();
        Matricula.FormaIngresso forma = Matricula.FormaIngresso.valueOf(formaStr);

        Matricula salva = matriculaService.matricular(alunoId, turmaId, forma);
        var uri = ucb.path("/api/v1/enrollments/{id}").buildAndExpand(salva.getId()).toUri();
        return ResponseEntity.created(uri).body(toDTO(salva));
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA')")
    @Operation(summary = "Cancelar matrícula")
    public ResponseEntity<Void> cancelar(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body
    ) {
        String motivo = body != null ? body.getOrDefault("motivo", "") : "";
        matriculaService.cancelar(id, motivo);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/transfer")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA')")
    @Operation(summary = "Transferir aluno para outra turma")
    public ResponseEntity<MatriculaResponseDTO> transferir(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body
    ) {
        Long novaTurmaId = Long.valueOf(body.get("novaTurmaId").toString());
        matriculaService.transferir(id, novaTurmaId);
        Matricula novaMatricula = matriculaService.listarAtivasPorTurma(novaTurmaId)
                .stream().findFirst()
                .orElseThrow();
        return ResponseEntity.ok(toDTO(novaMatricula));
    }

    private MatriculaResponseDTO toDTO(Matricula m) {
        return MatriculaResponseDTO.builder()
                .id(m.getId())
                .numeroMatricula(m.getNumeroMatricula())
                .alunoId(m.getAluno().getId())
                .alunoNome(m.getAluno().getNomeCompleto())
                .turmaId(m.getTurma().getId())
                .turmaNome(m.getTurma().getNome())
                .anoLetivo(m.getAnoLetivo())
                .dataMatricula(m.getDataMatricula())
                .dataCancelamento(m.getDataCancelamento())
                .motivoCancelamento(m.getMotivoCancelamento())
                .formaIngresso(m.getFormaIngresso())
                .tipoEnsino(m.getTipoEnsino())
                .situacao(m.getSituacao())
                .mediaFinal(m.getMediaFinal())
                .frequenciaFinal(m.getFrequenciaFinal())
                .observacoes(m.getObservacoes())
                .createdAt(m.getCreatedAt())
                .build();
    }
}
