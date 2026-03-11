package br.edu.waldorf.modules.pessoa.api;

import br.edu.waldorf.modules.pessoa.api.dto.ResponsavelResponseDTO;
import br.edu.waldorf.modules.pessoa.domain.model.Responsavel;
import br.edu.waldorf.modules.pessoa.domain.service.ResponsavelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

/**
 * Controller REST para Responsáveis
 * Base path: /api/v1/guardians
 *
 * @author Sistema Waldorf
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/v1/guardians")
@RequiredArgsConstructor
@Tag(name = "Responsáveis", description = "Gestão de responsáveis/pais")
public class ResponsavelController {

    private final ResponsavelService responsavelService;

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA','DIRECAO')")
    @Operation(summary = "Buscar responsável por ID")
    public ResponseEntity<ResponsavelResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(toDTO(responsavelService.buscarPorId(id)));
    }

    @GetMapping("/student/{alunoId}")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA','PROFESSOR','DIRECAO')")
    @Operation(summary = "Listar responsáveis de um aluno")
    public ResponseEntity<List<ResponsavelResponseDTO>> listarPorAluno(@PathVariable Long alunoId) {
        return ResponseEntity.ok(
                responsavelService.listarPorAluno(alunoId).stream().map(this::toDTO).toList()
        );
    }

    @GetMapping("/student/{alunoId}/emergency")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA','PROFESSOR','DIRECAO')")
    @Operation(summary = "Listar contatos de emergência de um aluno")
    public ResponseEntity<List<ResponsavelResponseDTO>> listarEmergencia(@PathVariable Long alunoId) {
        return ResponseEntity.ok(
                responsavelService.listarContatosEmergencia(alunoId).stream().map(this::toDTO).toList()
        );
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA')")
    @Operation(summary = "Cadastrar responsável")
    public ResponseEntity<ResponsavelResponseDTO> criar(
            @RequestBody Responsavel responsavel,
            UriComponentsBuilder ucb
    ) {
        Responsavel salvo = responsavelService.criar(responsavel);
        var uri = ucb.path("/api/v1/guardians/{id}").buildAndExpand(salvo.getId()).toUri();
        return ResponseEntity.created(uri).body(toDTO(salvo));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA')")
    @Operation(summary = "Atualizar dados do responsável")
    public ResponseEntity<ResponsavelResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody Responsavel dados
    ) {
        return ResponseEntity.ok(toDTO(responsavelService.atualizar(id, dados)));
    }

    private ResponsavelResponseDTO toDTO(Responsavel r) {
        return ResponsavelResponseDTO.builder()
                .id(r.getId())
                .nomeCompleto(r.getNomeCompleto())
                .email(r.getEmail())
                .telefonePrincipal(r.getTelefonePrincipal())
                .telefoneSecundario(r.getTelefoneSecundario())
                .fotoUrl(r.getFotoUrl())
                .tipoRelacao(r.getTipoRelacao())
                .profissao(r.getProfissao())
                .localTrabalho(r.getLocalTrabalho())
                .telefoneTrabalho(r.getTelefoneTrabalho())
                .autorizadoBuscar(r.getAutorizadoBuscar())
                .contatoEmergencia(r.getContatoEmergencia())
                .guardaCompartilhada(r.getGuardaCompartilhada())
                .prioridadeContato(r.getPrioridadeContato())
                .situacao(r.getSituacao())
                .createdAt(r.getCreatedAt())
                .build();
    }
}
