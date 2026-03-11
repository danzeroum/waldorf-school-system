package br.edu.waldorf.modules.financeiro.api;

import br.edu.waldorf.modules.financeiro.api.dto.MensalidadeResponseDTO;
import br.edu.waldorf.modules.financeiro.api.dto.PagamentoRequestDTO;
import br.edu.waldorf.modules.financeiro.domain.model.Mensalidade;
import br.edu.waldorf.modules.financeiro.domain.model.Pagamento;
import br.edu.waldorf.modules.financeiro.domain.service.MensalidadeService;
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

import java.util.List;

/**
 * Controller REST para Mensalidades
 * Base path: /api/v1/installments
 *
 * @author Sistema Waldorf
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/v1/installments")
@RequiredArgsConstructor
@Tag(name = "Mensalidades", description = "Gestão de mensalidades e pagamentos")
public class MensalidadeController {

    private final MensalidadeService mensalidadeService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA','DIRECAO')")
    @Operation(summary = "Listar mensalidades com filtros")
    public ResponseEntity<Page<MensalidadeResponseDTO>> listar(
            @RequestParam(required = false) Mensalidade.StatusMensalidade status,
            @RequestParam(required = false) Integer anoReferencia,
            @RequestParam(required = false) Integer mesReferencia,
            @PageableDefault(size = 30) Pageable pageable
    ) {
        return ResponseEntity.ok(
                mensalidadeService.listarComFiltros(status, anoReferencia, mesReferencia, pageable)
                                  .map(this::toDTO)
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA','DIRECAO')")
    @Operation(summary = "Buscar mensalidade por ID")
    public ResponseEntity<MensalidadeResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(toDTO(mensalidadeService.buscarPorId(id)));
    }

    @GetMapping("/contract/{contratoId}")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA','DIRECAO')")
    @Operation(summary = "Listar parcelas de um contrato")
    public ResponseEntity<List<MensalidadeResponseDTO>> listarPorContrato(@PathVariable Long contratoId) {
        return ResponseEntity.ok(
                mensalidadeService.listarPorContrato(contratoId).stream().map(this::toDTO).toList()
        );
    }

    @GetMapping("/overdue")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA','DIRECAO')")
    @Operation(summary = "Listar mensalidades em atraso")
    public ResponseEntity<List<MensalidadeResponseDTO>> listarAtrasadas() {
        return ResponseEntity.ok(
                mensalidadeService.listarAtrasadas().stream().map(this::toDTO).toList()
        );
    }

    @GetMapping("/due-soon")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA')")
    @Operation(summary = "Listar mensalidades a vencer nos próximos N dias (padrão: 7)")
    public ResponseEntity<List<MensalidadeResponseDTO>> aVencer(
            @RequestParam(defaultValue = "7") int dias
    ) {
        return ResponseEntity.ok(
                mensalidadeService.listarAVencer(dias).stream().map(this::toDTO).toList()
        );
    }

    @PostMapping("/{id}/pay")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA')")
    @Operation(summary = "Registrar pagamento de mensalidade")
    public ResponseEntity<MensalidadeResponseDTO> pagar(
            @PathVariable Long id,
            @Valid @RequestBody PagamentoRequestDTO dto
    ) {
        Pagamento pag = mensalidadeService.registrarPagamento(
                id, dto.getValor(), dto.getFormaPagamento(),
                dto.getGatewayId(), dto.getComprovanteUrl()
        );
        return ResponseEntity.ok(toDTO(pag.getMensalidade()));
    }

    @PostMapping("/payments/{pagamentoId}/refund")
    @PreAuthorize("hasAnyRole('ADMIN','DIRECAO')")
    @Operation(summary = "Estornar pagamento")
    public ResponseEntity<MensalidadeResponseDTO> estornar(@PathVariable Long pagamentoId) {
        Pagamento estornado = mensalidadeService.estornarPagamento(pagamentoId);
        return ResponseEntity.ok(toDTO(estornado.getMensalidade()));
    }

    // --- Mapeamento ---

    private MensalidadeResponseDTO toDTO(Mensalidade m) {
        return MensalidadeResponseDTO.builder()
                .id(m.getId())
                .contratoId(m.getContrato().getId())
                .alunoNome(m.getContrato().getAluno().getNomeCompleto())
                .numeroParcela(m.getNumeroParcela())
                .mesReferencia(m.getMesReferencia())
                .anoReferencia(m.getAnoReferencia())
                .valorParcela(m.getValorParcela())
                .valorDesconto(m.getValorDesconto())
                .valorJuros(m.getValorJuros())
                .valorMulta(m.getValorMulta())
                .valorTotal(m.calcularValorTotal())
                .valorPago(m.getValorPago())
                .dataVencimento(m.getDataVencimento())
                .dataPagamento(m.getDataPagamento())
                .status(m.getStatus())
                .codigoBarras(m.getCodigoBarras())
                .pixQrCode(m.getPixQrCode())
                .build();
    }
}
