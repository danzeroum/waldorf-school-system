package br.edu.waldorf.modules.financeiro.api;

import br.edu.waldorf.modules.financeiro.domain.model.Mensalidade;
import br.edu.waldorf.modules.financeiro.domain.repository.MensalidadeRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller REST para Relatórios Financeiros
 * Base path: /api/v1/financial-reports
 *
 * @author Sistema Waldorf
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/v1/financial-reports")
@RequiredArgsConstructor
@Tag(name = "Relatórios Financeiros", description = "Consolidação financeira mensal e inadimplência")
public class RelatorioFinanceiroController {

    private final MensalidadeRepository mensalidadeRepository;

    @GetMapping("/monthly")
    @PreAuthorize("hasAnyRole('ADMIN','DIRECAO','SECRETARIA')")
    @Operation(summary = "Relatório financeiro de um mês/ano")
    public ResponseEntity<Map<String, Object>> relatorioMensal(
            @RequestParam int ano,
            @RequestParam int mes
    ) {
        List<Mensalidade> mensalidades = mensalidadeRepository
                .findWithFilters(null, ano, mes, org.springframework.data.domain.Pageable.unpaged())
                .getContent();

        BigDecimal totalEsperado  = mensalidades.stream()
                .map(Mensalidade::getValorParcela)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalRecebido  = mensalidades.stream()
                .filter(m -> m.getStatus() == Mensalidade.StatusMensalidade.PAGA)
                .map(m -> m.getValorPago() != null ? m.getValorPago() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalAtrasado  = mensalidades.stream()
                .filter(m -> m.getStatus() == Mensalidade.StatusMensalidade.ATRASADA)
                .map(Mensalidade::calcularValorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long totalMensalidades  = mensalidades.size();
        long quantPagas         = mensalidades.stream().filter(m -> m.getStatus() == Mensalidade.StatusMensalidade.PAGA).count();
        long quantAtrasadas     = mensalidades.stream().filter(m -> m.getStatus() == Mensalidade.StatusMensalidade.ATRASADA).count();

        BigDecimal percentInadimplencia = totalMensalidades > 0
                ? BigDecimal.valueOf(quantAtrasadas)
                          .divide(BigDecimal.valueOf(totalMensalidades), 4, RoundingMode.HALF_UP)
                          .multiply(BigDecimal.valueOf(100))
                          .setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        Map<String, Object> relatorio = new LinkedHashMap<>();
        relatorio.put("anoReferencia",         ano);
        relatorio.put("mesReferencia",         mes);
        relatorio.put("totalMensalidades",     totalMensalidades);
        relatorio.put("valorTotalEsperado",    totalEsperado);
        relatorio.put("valorTotalRecebido",    totalRecebido);
        relatorio.put("valorInadimplente",     totalAtrasado);
        relatorio.put("quantidadePagas",       quantPagas);
        relatorio.put("quantidadeAtrasadas",   quantAtrasadas);
        relatorio.put("percentualInadimplencia", percentInadimplencia);
        relatorio.put("geradoEm",              LocalDateTime.now());

        return ResponseEntity.ok(relatorio);
    }

    @GetMapping("/period")
    @PreAuthorize("hasAnyRole('ADMIN','DIRECAO')")
    @Operation(summary = "Pagamentos confirmados em um período")
    public ResponseEntity<Map<String, Object>> relatorioPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim
    ) {
        // Usa o PagamentoRepository via query nativa seria ideal, mas aqui usamos filter
        List<Mensalidade> mensalidades = mensalidadeRepository
                .findWithFilters(Mensalidade.StatusMensalidade.PAGA, null, null,
                        org.springframework.data.domain.Pageable.unpaged())
                .getContent()
                .stream()
                .filter(m -> m.getDataPagamento() != null
                          && !m.getDataPagamento().toLocalDate().isBefore(inicio)
                          && !m.getDataPagamento().toLocalDate().isAfter(fim))
                .toList();

        BigDecimal totalRecebido = mensalidades.stream()
                .map(m -> m.getValorPago() != null ? m.getValorPago() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> rel = new LinkedHashMap<>();
        rel.put("dataInicio",     inicio);
        rel.put("dataFim",        fim);
        rel.put("quantPagamentos", mensalidades.size());
        rel.put("totalRecebido",  totalRecebido);
        rel.put("geradoEm",       LocalDateTime.now());
        return ResponseEntity.ok(rel);
    }
}
