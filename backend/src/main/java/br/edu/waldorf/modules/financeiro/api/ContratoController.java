package br.edu.waldorf.modules.financeiro.api;

import br.edu.waldorf.modules.financeiro.api.dto.ContratoResponseDTO;
import br.edu.waldorf.modules.financeiro.domain.model.Contrato;
import br.edu.waldorf.modules.financeiro.domain.model.Mensalidade;
import br.edu.waldorf.modules.financeiro.domain.repository.MensalidadeRepository;
import br.edu.waldorf.modules.financeiro.domain.service.ContratoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
 * Controller REST para Contratos
 * Base path: /api/v1/contracts
 *
 * @author Sistema Waldorf
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/v1/contracts")
@RequiredArgsConstructor
@Tag(name = "Contratos", description = "Gestão de contratos financeiros")
public class ContratoController {

    private final ContratoService contratoService;
    private final MensalidadeRepository mensalidadeRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA','DIRECAO')")
    @Operation(summary = "Listar contratos com filtros")
    public ResponseEntity<Page<ContratoResponseDTO>> listar(
            @RequestParam(required = false) Contrato.SituacaoContrato situacao,
            @RequestParam(required = false) Integer anoLetivo,
            @PageableDefault(size = 25) Pageable pageable
    ) {
        Page<Contrato> page = contratoService.listarComFiltros(situacao, anoLetivo, pageable);
        return ResponseEntity.ok(page.map(this::toDTO));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA')")
    @Operation(summary = "Listar contratos pendentes de assinatura")
    public ResponseEntity<List<ContratoResponseDTO>> listarPendentes() {
        return ResponseEntity.ok(
                contratoService.listarPendentes().stream().map(this::toDTO).toList()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA','DIRECAO')")
    @Operation(summary = "Buscar contrato por ID")
    public ResponseEntity<ContratoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(toDTO(contratoService.buscarPorId(id)));
    }

    @GetMapping("/student/{alunoId}")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA','DIRECAO')")
    @Operation(summary = "Histórico de contratos de um aluno")
    public ResponseEntity<List<ContratoResponseDTO>> historicoAluno(@PathVariable Long alunoId) {
        return ResponseEntity.ok(
                contratoService.historicoAluno(alunoId).stream().map(this::toDTO).toList()
        );
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA')")
    @Operation(summary = "Criar novo contrato")
    public ResponseEntity<ContratoResponseDTO> criar(
            @Valid @RequestBody Contrato contrato,
            UriComponentsBuilder ucb
    ) {
        Contrato salvo = contratoService.criar(contrato);
        var uri = ucb.path("/api/v1/contracts/{id}").buildAndExpand(salvo.getId()).toUri();
        return ResponseEntity.created(uri).body(toDTO(salvo));
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA')")
    @Operation(summary = "Ativar contrato e gerar mensalidades")
    public ResponseEntity<ContratoResponseDTO> ativar(@PathVariable Long id) {
        return ResponseEntity.ok(toDTO(contratoService.ativar(id)));
    }

    @PatchMapping("/{id}/suspend")
    @PreAuthorize("hasAnyRole('ADMIN','DIRECAO')")
    @Operation(summary = "Suspender contrato")
    public ResponseEntity<Void> suspender(@PathVariable Long id) {
        contratoService.suspender(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reactivate")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA')")
    @Operation(summary = "Reativar contrato suspenso")
    public ResponseEntity<Void> reativar(@PathVariable Long id) {
        contratoService.reativar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN','DIRECAO')")
    @Operation(summary = "Cancelar contrato")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        contratoService.cancelar(id);
        return ResponseEntity.noContent().build();
    }

    // --- Mapeamento ---

    private ContratoResponseDTO toDTO(Contrato c) {
        List<Mensalidade> mensalidades = mensalidadeRepository.findByContratoIdOrderByNumeroParcela(c.getId());
        long pagas    = mensalidades.stream().filter(m -> m.getStatus() == Mensalidade.StatusMensalidade.PAGA).count();
        long atrasadas = mensalidades.stream().filter(m -> m.getStatus() == Mensalidade.StatusMensalidade.ATRASADA).count();

        return ContratoResponseDTO.builder()
                .id(c.getId())
                .numeroContrato(c.getNumeroContrato())
                .alunoId(c.getAluno().getId())
                .alunoNome(c.getAluno().getNomeCompleto())
                .responsavelId(c.getResponsavel().getId())
                .responsavelNome(c.getResponsavel().getNomeCompleto())
                .planoId(c.getPlano().getId())
                .planoNome(c.getPlano().getNome())
                .anoLetivo(c.getAnoLetivo())
                .valorBase(c.getValorBase())
                .descontoTotal(c.getDescontoTotal())
                .valorFinal(c.getValorFinal())
                .dataAssinatura(c.getDataAssinatura())
                .dataInicioVigencia(c.getDataInicioVigencia())
                .dataFimVigencia(c.getDataFimVigencia())
                .situacao(c.getSituacao())
                .formaPagamento(c.getFormaPagamento())
                .totalParcelas(c.getPlano().getNumeroParcelas())
                .parcelasPagas(pagas)
                .parcelasAtrasadas(atrasadas)
                .createdAt(c.getCreatedAt())
                .build();
    }
}
