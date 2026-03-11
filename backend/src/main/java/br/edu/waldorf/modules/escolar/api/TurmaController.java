package br.edu.waldorf.modules.escolar.api;

import br.edu.waldorf.modules.escolar.api.dto.TurmaRequestDTO;
import br.edu.waldorf.modules.escolar.api.dto.TurmaResponseDTO;
import br.edu.waldorf.modules.escolar.domain.model.Curso;
import br.edu.waldorf.modules.escolar.domain.model.Turma;
import br.edu.waldorf.modules.escolar.domain.repository.CursoRepository;
import br.edu.waldorf.modules.escolar.domain.service.TurmaService;
import br.edu.waldorf.modules.pessoa.domain.repository.ProfessorRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

/**
 * Controller REST para Turmas
 * Base path: /api/v1/classes
 *
 * @author Sistema Waldorf
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/v1/classes")
@RequiredArgsConstructor
@Tag(name = "Turmas", description = "Gestão de turmas")
public class TurmaController {

    private final TurmaService turmaService;
    private final CursoRepository cursoRepository;
    private final ProfessorRepository professorRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA','PROFESSOR','DIRECAO')")
    @Operation(summary = "Listar turmas com filtros")
    public ResponseEntity<Page<TurmaResponseDTO>> listar(
            @RequestParam(required = false) Turma.SituacaoTurma situacao,
            @RequestParam(required = false) Integer anoLetivo,
            @RequestParam(required = false) Long cursoId,
            @PageableDefault(size = 20, sort = "serie", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Page<Turma> page = turmaService.listarComFiltros(situacao, anoLetivo, cursoId, pageable);
        return ResponseEntity.ok(page.map(this::toDTO));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA','PROFESSOR','DIRECAO')")
    @Operation(summary = "Buscar turma por ID")
    public ResponseEntity<TurmaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(toDTO(turmaService.buscarPorId(id)));
    }

    @GetMapping("/ano/{anoLetivo}")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA','PROFESSOR','DIRECAO')")
    @Operation(summary = "Listar turmas de um ano letivo")
    public ResponseEntity<List<TurmaResponseDTO>> listarPorAno(@PathVariable Integer anoLetivo) {
        return ResponseEntity.ok(
                turmaService.listarPorAnoLetivo(anoLetivo).stream().map(this::toDTO).toList()
        );
    }

    @GetMapping("/teacher/{professorId}")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA','DIRECAO','PROFESSOR')")
    @Operation(summary = "Listar turmas de um professor")
    public ResponseEntity<List<TurmaResponseDTO>> listarPorProfessor(@PathVariable Long professorId) {
        return ResponseEntity.ok(
                turmaService.listarPorProfessor(professorId).stream().map(this::toDTO).toList()
        );
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA')")
    @Operation(summary = "Criar nova turma")
    public ResponseEntity<TurmaResponseDTO> criar(
            @Valid @RequestBody TurmaRequestDTO dto,
            UriComponentsBuilder ucb
    ) {
        Turma turma = fromRequestDTO(dto);
        Turma salva = turmaService.criar(turma);
        var uri = ucb.path("/api/v1/classes/{id}").buildAndExpand(salva.getId()).toUri();
        return ResponseEntity.created(uri).body(toDTO(salva));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA')")
    @Operation(summary = "Atualizar dados da turma")
    public ResponseEntity<TurmaResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody TurmaRequestDTO dto
    ) {
        return ResponseEntity.ok(toDTO(turmaService.atualizar(id, fromRequestDTO(dto))));
    }

    @PatchMapping("/{id}/iniciar")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA','DIRECAO')")
    @Operation(summary = "Iniciar turma (ABERTA -> EM_ANDAMENTO)")
    public ResponseEntity<Void> iniciar(@PathVariable Long id) {
        turmaService.iniciarTurma(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/concluir")
    @PreAuthorize("hasAnyRole('ADMIN','DIRECAO')")
    @Operation(summary = "Concluir turma")
    public ResponseEntity<Void> concluir(@PathVariable Long id) {
        turmaService.concluirTurma(id);
        return ResponseEntity.noContent().build();
    }

    // --- Mapeamento ---

    private TurmaResponseDTO toDTO(Turma t) {
        return TurmaResponseDTO.builder()
                .id(t.getId())
                .codigo(t.getCodigo())
                .nome(t.getNome())
                .cursoId(t.getCurso() != null ? t.getCurso().getId() : null)
                .cursoNome(t.getCurso() != null ? t.getCurso().getNome() : null)
                .anoLetivo(t.getAnoLetivo())
                .serie(t.getSerie())
                .turno(t.getTurno())
                .sala(t.getSala())
                .capacidadeMaxima(t.getCapacidadeMaxima())
                .vagasDisponiveis(t.getVagasDisponiveis())
                .professorTitularId(t.getProfessorTitular() != null ? t.getProfessorTitular().getId() : null)
                .professorTitularNome(t.getProfessorTitular() != null ? t.getProfessorTitular().getNomeCompleto() : null)
                .professorAuxiliarId(t.getProfessorAuxiliar() != null ? t.getProfessorAuxiliar().getId() : null)
                .professorAuxiliarNome(t.getProfessorAuxiliar() != null ? t.getProfessorAuxiliar().getNomeCompleto() : null)
                .dataInicio(t.getDataInicio())
                .dataFim(t.getDataFim())
                .situacao(t.getSituacao())
                .corTurma(t.getCorTurma())
                .createdAt(t.getCreatedAt())
                .build();
    }

    private Turma fromRequestDTO(TurmaRequestDTO dto) {
        Curso curso = cursoRepository.findById(dto.getCursoId())
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Curso não encontrado: " + dto.getCursoId()));
        Turma.TurmaBuilder builder = Turma.builder()
                .codigo(dto.getCodigo())
                .nome(dto.getNome())
                .curso(curso)
                .anoLetivo(dto.getAnoLetivo())
                .serie(dto.getSerie())
                .turno(dto.getTurno())
                .sala(dto.getSala())
                .capacidadeMaxima(dto.getCapacidadeMaxima() != null ? dto.getCapacidadeMaxima() : 25)
                .dataInicio(dto.getDataInicio())
                .dataFim(dto.getDataFim())
                .corTurma(dto.getCorTurma() != null ? dto.getCorTurma() : "#2196F3");
        if (dto.getProfessorTitularId() != null) {
            professorRepository.findById(dto.getProfessorTitularId()).ifPresent(builder::professorTitular);
        }
        if (dto.getProfessorAuxiliarId() != null) {
            professorRepository.findById(dto.getProfessorAuxiliarId()).ifPresent(builder::professorAuxiliar);
        }
        return builder.build();
    }
}
