package com.waldorf.presentation.controller;

import com.waldorf.application.dto.financeiro.BaixaPagamentoRequestDTO;
import com.waldorf.application.dto.financeiro.MensalidadeResponseDTO;
import com.waldorf.application.dto.financeiro.ResumoFinanceiroDTO;
import com.waldorf.application.service.ContratoService;
import com.waldorf.application.service.MensalidadeService;
import com.waldorf.domain.entity.Mensalidade.StatusMensalidade;
import com.waldorf.domain.repository.ContratoRepository;
import com.waldorf.domain.repository.MensalidadeRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Year;
import java.util.List;

@RestController
@RequestMapping("/api/v1/financeiro")
@Tag(name = "Financeiro", description = "Contratos, mensalidades e resumo financeiro")
public class FinanceiroController {

    private final ContratoService contratoService;
    private final MensalidadeService mensalidadeService;
    private final MensalidadeRepository mensalidadeRepository;
    private final ContratoRepository contratoRepository;

    public FinanceiroController(ContratoService contratoService,
                                MensalidadeService mensalidadeService,
                                MensalidadeRepository mensalidadeRepository,
                                ContratoRepository contratoRepository) {
        this.contratoService = contratoService;
        this.mensalidadeService = mensalidadeService;
        this.mensalidadeRepository = mensalidadeRepository;
        this.contratoRepository = contratoRepository;
    }

    // === MENSALIDADES ===

    @GetMapping("/parcelas")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA','FINANCEIRO')")
    @Operation(summary = "Listar mensalidades", description = "Filtra por status e/ou contratoId")
    public List<MensalidadeResponseDTO> listarParcelas(
            @RequestParam(required = false) StatusMensalidade status,
            @RequestParam(required = false) Long contratoId) {
        if (contratoId != null) {
            return mensalidadeService.listarPorContrato(contratoId);
        }
        if (status != null) {
            return mensalidadeService.listarPorStatus(status);
        }
        return mensalidadeService.listarVencidas();
    }

    @PostMapping("/parcelas/{id}/pagar")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA','FINANCEIRO')")
    @Operation(summary = "Registrar pagamento de mensalidade")
    public ResponseEntity<MensalidadeResponseDTO> registrarPagamento(
            @PathVariable Long id,
            @Valid @RequestBody BaixaPagamentoRequestDTO dto) {
        return ResponseEntity.ok(mensalidadeService.registrarPagamento(id, dto));
    }

    @GetMapping("/contratos/{id}/parcelas")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA','FINANCEIRO','PROFESSOR')")
    @Operation(summary = "Listar mensalidades de um contrato")
    public List<MensalidadeResponseDTO> parcelasPorContrato(@PathVariable Long id) {
        return mensalidadeService.listarPorContrato(id);
    }

    @PostMapping("/contratos/{id}/ativar")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA','FINANCEIRO')")
    @Operation(summary = "Ativar contrato e gerar mensalidades automaticamente")
    public ResponseEntity<?> ativarContrato(@PathVariable Long id) {
        return ResponseEntity.ok(contratoService.ativar(id));
    }

    @PostMapping("/contratos/{id}/encerrar")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA','FINANCEIRO')")
    @Operation(summary = "Encerrar contrato")
    public ResponseEntity<?> encerrarContrato(@PathVariable Long id,
            @RequestBody(required = false) java.util.Map<String, String> body) {
        String motivo = body != null ? body.getOrDefault("motivo", "") : "";
        return ResponseEntity.ok(contratoService.encerrar(id, motivo));
    }

    // === RESUMO / DASHBOARD ===

    @GetMapping("/resumo")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA','FINANCEIRO')")
    @Operation(summary = "Resumo financeiro do ano letivo")
    public ResumoFinanceiroDTO resumo(
            @RequestParam(required = false) Integer anoLetivo) {
        int ano = anoLetivo != null ? anoLetivo : Year.now().getValue();

        BigDecimal totalReceita = mensalidadeRepository.somarReceitaPrevistaPorAno(ano);
        BigDecimal totalRecebido = mensalidadeRepository.somarReceitaRecebidaPorAno(ano);
        BigDecimal totalVencido = mensalidadeRepository.somarVencidosPorAno(ano);
        BigDecimal totalPendente = totalReceita.subtract(totalRecebido);
        long totalContratos = contratoRepository.count();
        long inadimplentes = mensalidadeRepository.countContratosComVencidos();
        double taxa = totalContratos > 0
                ? BigDecimal.valueOf(inadimplentes)
                        .divide(BigDecimal.valueOf(totalContratos), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .doubleValue()
                : 0.0;

        return new ResumoFinanceiroDTO(
                totalReceita, totalRecebido, totalPendente, totalVencido,
                totalContratos, inadimplentes, taxa
        );
    }
}
