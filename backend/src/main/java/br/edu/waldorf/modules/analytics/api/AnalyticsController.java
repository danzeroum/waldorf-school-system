package br.edu.waldorf.modules.analytics.api;

import br.edu.waldorf.modules.analytics.application.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

/**
 * Controller de Analytics e Relatórios.
 * Módulo 9 do planoAPIs.md — Relatórios Pedagógicos e Métricas do Sistema.
 */
@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics", description = "Relatórios pedagógicos, financeiros e métricas de uso")
@SecurityRequirement(name = "bearerAuth")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    // -------------------------------------------------------------------
    // GET /api/v1/analytics/turmas/{id}/development-report
    // Relatório de desenvolvimento pedagógico da turma
    // -------------------------------------------------------------------
    @GetMapping("/turmas/{turmaId}/development-report")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    @Operation(summary = "Relatório de desenvolvimento pedagógico da turma")
    public ResponseEntity<Map<String, Object>> getClassDevelopmentReport(
            @PathVariable Long turmaId,
            @RequestParam(defaultValue = "LAST_QUARTER") String period,
            @RequestParam(defaultValue = "ALL") String aspect) {
        return ResponseEntity.ok(
                analyticsService.getClassDevelopmentReport(turmaId, period, aspect));
    }

    // -------------------------------------------------------------------
    // GET /api/v1/analytics/alunos/{id}/development-timeline
    // Timeline de desenvolvimento individual do aluno
    // -------------------------------------------------------------------
    @GetMapping("/alunos/{alunoId}/development-timeline")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR', 'PAI')")
    @Operation(summary = "Timeline de desenvolvimento individual do aluno")
    public ResponseEntity<Map<String, Object>> getStudentDevelopmentTimeline(
            @PathVariable Long alunoId,
            @RequestParam(required = false) String aspect,
            @RequestParam(defaultValue = "MONTHLY") String granularity) {
        return ResponseEntity.ok(
                analyticsService.getStudentDevelopmentTimeline(alunoId, aspect, granularity));
    }

    // -------------------------------------------------------------------
    // GET /api/v1/analytics/financeiro/resumo
    // Resumo financeiro mensal (view vw_financeiro_mensal)
    // -------------------------------------------------------------------
    @GetMapping("/financeiro/resumo")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCEIRO')")
    @Operation(summary = "Resumo financeiro mensal — inadimplência, recebimentos, projeção")
    public ResponseEntity<Map<String, Object>> getFinanceiroResumo(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(
                analyticsService.getFinanceiroResumo(startDate, endDate));
    }

    // -------------------------------------------------------------------
    // GET /api/v1/analytics/usage-dashboard
    // Métricas de uso do sistema (Admin)
    // -------------------------------------------------------------------
    @GetMapping("/usage-dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Dashboard de uso do sistema — usuários ativos, notificações, engajamento")
    public ResponseEntity<Map<String, Object>> getUsageDashboard(
            @RequestParam(defaultValue = "LAST_30_DAYS") String period) {
        return ResponseEntity.ok(
                analyticsService.getUsageDashboard(period));
    }

    // -------------------------------------------------------------------
    // GET /api/v1/analytics/dashboard/secretaria
    // Dashboard rápido da secretaria (view vw_dashboard_secretaria)
    // -------------------------------------------------------------------
    @GetMapping("/dashboard/secretaria")
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA')")
    @Operation(summary = "Dashboard da secretaria — alunos ativos, contratos pendentes, LGPD")
    public ResponseEntity<Map<String, Object>> getDashboardSecretaria() {
        return ResponseEntity.ok(analyticsService.getDashboardSecretaria());
    }
}
