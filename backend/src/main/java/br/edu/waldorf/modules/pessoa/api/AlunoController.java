package br.edu.waldorf.modules.pessoa.api;

import br.edu.waldorf.modules.pessoa.api.dto.AlunoRequestDTO;
import br.edu.waldorf.modules.pessoa.api.dto.AlunoResponseDTO;
import br.edu.waldorf.modules.pessoa.domain.model.Aluno;
import br.edu.waldorf.modules.pessoa.domain.service.AlunoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
 * Controller REST para Alunos
 * Base path: /api/v1/students
 *
 * @author Sistema Waldorf
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
@Tag(name = "Alunos", description = "Gestão de alunos")
public class AlunoController {

    private final AlunoService alunoService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA','PROFESSOR','DIRECAO')")
    @Operation(summary = "Listar alunos com filtros e paginação")
    public ResponseEntity<Page<AlunoResponseDTO>> listar(
            @RequestParam(required = false) Aluno.SituacaoAluno situacao,
            @RequestParam(required = false) Long turmaId,
            @RequestParam(required = false) String nome,
            @PageableDefault(size = 25, sort = "nomeCompleto", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Page<Aluno> page = alunoService.listarComFiltros(situacao, turmaId, nome, pageable);
        return ResponseEntity.ok(page.map(this::toResponseDTO));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA','PROFESSOR','DIRECAO')")
    @Operation(summary = "Buscar aluno por ID")
    public ResponseEntity<AlunoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(toResponseDTO(alunoService.buscarPorId(id)));
    }

    @GetMapping("/matricula/{numeroMatricula}")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA')")
    @Operation(summary = "Buscar aluno por número de matrícula")
    public ResponseEntity<AlunoResponseDTO> buscarPorMatricula(@PathVariable String numeroMatricula) {
        return ResponseEntity.ok(toResponseDTO(alunoService.buscarPorMatricula(numeroMatricula)));
    }

    @GetMapping("/turma/{turmaId}")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA','PROFESSOR','DIRECAO')")
    @Operation(summary = "Listar alunos ativos de uma turma")
    public ResponseEntity<List<AlunoResponseDTO>> listarPorTurma(@PathVariable Long turmaId) {
        return ResponseEntity.ok(
                alunoService.listarPorTurma(turmaId).stream().map(this::toResponseDTO).toList()
        );
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA')")
    @Operation(summary = "Cadastrar novo aluno")
    public ResponseEntity<AlunoResponseDTO> criar(
            @Valid @RequestBody AlunoRequestDTO dto,
            UriComponentsBuilder ucb
    ) {
        Aluno aluno = fromRequestDTO(dto);
        Aluno salvo = alunoService.criar(aluno);
        var uri = ucb.path("/api/v1/students/{id}").buildAndExpand(salvo.getId()).toUri();
        return ResponseEntity.created(uri).body(toResponseDTO(salvo));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA')")
    @Operation(summary = "Atualizar dados do aluno")
    public ResponseEntity<AlunoResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody AlunoRequestDTO dto
    ) {
        Aluno atualizado = alunoService.atualizar(id, fromRequestDTO(dto));
        return ResponseEntity.ok(toResponseDTO(atualizado));
    }

    @DeleteMapping("/{id}/desligar")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA')")
    @Operation(summary = "Desligar aluno")
    public ResponseEntity<Void> desligar(@PathVariable Long id) {
        alunoService.desligar(id);
        return ResponseEntity.noContent().build();
    }

    // --- Helpers de mapeamento manual (sem MapStruct para simplicidade) ---

    private AlunoResponseDTO toResponseDTO(Aluno a) {
        return AlunoResponseDTO.builder()
                .id(a.getId())
                .numeroMatricula(a.getNumeroMatricula())
                .nomeCompleto(a.getNomeCompleto())
                .nomeSocial(a.getNomeSocial())
                .cpf(a.getCpf())
                .dataNascimento(a.getDataNascimento())
                .email(a.getEmail())
                .telefonePrincipal(a.getTelefonePrincipal())
                .fotoUrl(a.getFotoUrl())
                .situacao(a.getSituacao())
                .tipoSanguineo(a.getTipoSanguineo())
                .planoSaude(a.getPlanoSaude())
                .alergias(a.getAlergias())
                .necessidadesEspeciais(a.getNecessidadesEspeciais())
                .temperamento(a.getTemperamento())
                .naturalidade(a.getNaturalidade())
                .nomePai(a.getNomePai())
                .nomeMae(a.getNomeMae())
                .anoIngresso(a.getAnoIngresso())
                .turmaId(a.getTurmaAtual() != null ? a.getTurmaAtual().getId() : null)
                .turmaNome(a.getTurmaAtual() != null ? a.getTurmaAtual().getNome() : null)
                .createdAt(a.getCreatedAt())
                .updatedAt(a.getUpdatedAt())
                .build();
    }

    private Aluno fromRequestDTO(AlunoRequestDTO dto) {
        return Aluno.builder()
                .nomeCompleto(dto.getNomeCompleto())
                .nomeSocial(dto.getNomeSocial())
                .cpf(dto.getCpf())
                .dataNascimento(dto.getDataNascimento())
                .email(dto.getEmail())
                .telefonePrincipal(dto.getTelefonePrincipal())
                .fotoUrl(dto.getFotoUrl())
                .tipoSanguineo(dto.getTipoSanguineo())
                .planoSaude(dto.getPlanoSaude())
                .alergias(dto.getAlergias())
                .medicamentosControlados(dto.getMedicamentosControlados())
                .necessidadesEspeciais(dto.getNecessidadesEspeciais())
                .observacoesMedicas(dto.getObservacoesMedicas())
                .temperamento(dto.getTemperamento())
                .naturalidade(dto.getNaturalidade())
                .nacionalidade(dto.getNacionalidade() != null ? dto.getNacionalidade() : "Brasileiro")
                .nomePai(dto.getNomePai())
                .nomeMae(dto.getNomeMae())
                .anoIngresso(dto.getAnoIngresso())
                .lgpdConsentimentoGeral(dto.getLgpdConsentimentoGeral())
                .build();
    }
}
