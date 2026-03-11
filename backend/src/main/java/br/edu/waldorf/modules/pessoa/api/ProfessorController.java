package br.edu.waldorf.modules.pessoa.api;

import br.edu.waldorf.modules.pessoa.api.dto.ProfessorResponseDTO;
import br.edu.waldorf.modules.pessoa.domain.model.Professor;
import br.edu.waldorf.modules.pessoa.domain.service.ProfessorService;
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

/**
 * Controller REST para Professores
 * Base path: /api/v1/teachers
 *
 * @author Sistema Waldorf
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/v1/teachers")
@RequiredArgsConstructor
@Tag(name = "Professores", description = "Gestão de professores")
public class ProfessorController {

    private final ProfessorService professorService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA','DIRECAO')")
    @Operation(summary = "Listar todos os professores ativos")
    public ResponseEntity<List<ProfessorResponseDTO>> listarAtivos() {
        return ResponseEntity.ok(
                professorService.listarAtivos().stream().map(this::toDTO).toList()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA','DIRECAO','PROFESSOR')")
    @Operation(summary = "Buscar professor por ID")
    public ResponseEntity<ProfessorResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(toDTO(professorService.buscarPorId(id)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA')")
    @Operation(summary = "Cadastrar novo professor")
    public ResponseEntity<ProfessorResponseDTO> criar(
            @RequestBody Professor professor,
            UriComponentsBuilder ucb
    ) {
        Professor salvo = professorService.criar(professor);
        var uri = ucb.path("/api/v1/teachers/{id}").buildAndExpand(salvo.getId()).toUri();
        return ResponseEntity.created(uri).body(toDTO(salvo));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA')")
    @Operation(summary = "Atualizar dados do professor")
    public ResponseEntity<ProfessorResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody Professor dados
    ) {
        return ResponseEntity.ok(toDTO(professorService.atualizar(id, dados)));
    }

    @DeleteMapping("/{id}/desligar")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Desligar professor")
    public ResponseEntity<Void> desligar(@PathVariable Long id) {
        professorService.desligar(id);
        return ResponseEntity.noContent().build();
    }

    private ProfessorResponseDTO toDTO(Professor p) {
        return ProfessorResponseDTO.builder()
                .id(p.getId())
                .nomeCompleto(p.getNomeCompleto())
                .email(p.getEmail())
                .telefonePrincipal(p.getTelefonePrincipal())
                .fotoUrl(p.getFotoUrl())
                .registroProfissional(p.getRegistroProfissional())
                .formacao(p.getFormacao())
                .especializacaoWaldorf(p.getEspecializacaoWaldorf())
                .anoFormacaoWaldorf(p.getAnoFormacaoWaldorf())
                .biografia(p.getBiografia())
                .situacao(p.getSituacao())
                .dataAdmissao(p.getDataAdmissao())
                .createdAt(p.getCreatedAt())
                .build();
    }
}
