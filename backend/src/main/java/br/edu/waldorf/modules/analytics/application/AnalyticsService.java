package br.edu.waldorf.modules.analytics.application;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

/**
 * Serviço de Analytics.
 * Usa EntityManager para queries nativas sobre as views e tabelas criadas na V7.
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class AnalyticsService {

    @PersistenceContext
    private EntityManager em;

    // -------------------------------------------------------------------
    // Relatório de desenvolvimento da turma
    // -------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public Map<String, Object> getClassDevelopmentReport(
            Long turmaId, String period, String aspect) {

        // Total de observações por aspecto na turma
        List<Object[]> aspectCounts = em.createNativeQuery("""
                SELECT aspecto, COUNT(*) as total
                FROM observacoes_desenvolvimento
                WHERE turma_id = :turmaId
                GROUP BY aspecto
                ORDER BY total DESC
                """)
                .setParameter("turmaId", turmaId)
                .getResultList();

        // Alunos sem observações nos últimos 14 dias
        List<Object[]> alunosSemObs = em.createNativeQuery("""
                SELECT a.id, p.nome_completo
                FROM alunos a
                JOIN pessoas p ON p.id = a.id
                JOIN matriculas m ON m.aluno_id = a.id AND m.turma_id = :turmaId
                    AND m.situacao IN ('ATIVA','EM_ANDAMENTO')
                WHERE a.id NOT IN (
                    SELECT DISTINCT aluno_id FROM observacoes_desenvolvimento
                    WHERE turma_id = :turmaId
                      AND data_observacao >= DATE_SUB(CURDATE(), INTERVAL 14 DAY)
                )
                """)
                .setParameter("turmaId", turmaId)
                .getResultList();

        Map<String, Long> aspectDistribution = new LinkedHashMap<>();
        long totalObs = 0L;
        for (Object[] row : aspectCounts) {
            long count = ((Number) row[1]).longValue();
            aspectDistribution.put(String.valueOf(row[0]), count);
            totalObs += count;
        }

        List<Map<String, Object>> alertas = new ArrayList<>();
        for (Object[] row : alunosSemObs) {
            alertas.add(Map.of(
                    "alunoId", row[0],
                    "nome",    row[1],
                    "tipo",    "SEM_OBSERVACAO_14_DIAS"
            ));
        }

        return Map.of(
                "turmaId",             turmaId,
                "period",              period,
                "totalObservacoes",    totalObs,
                "aspectDistribution",  aspectDistribution,
                "alertas",             alertas,
                "geradoEm",            LocalDate.now().toString()
        );
    }

    // -------------------------------------------------------------------
    // Timeline de desenvolvimento do aluno
    // -------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public Map<String, Object> getStudentDevelopmentTimeline(
            Long alunoId, String aspect, String granularity) {

        String aspectFilter = (aspect != null && !aspect.isBlank())
                ? " AND aspecto = '" + aspect.replace("'", "") + "'" : "";

        List<Object[]> rows = em.createNativeQuery("""
                SELECT
                    DATE_FORMAT(data_observacao, '%Y-%m') AS mes,
                    aspecto,
                    COUNT(*) AS total
                FROM observacoes_desenvolvimento
                WHERE aluno_id = :alunoId
                """ + aspectFilter + """
                GROUP BY mes, aspecto
                ORDER BY mes ASC
                """)
                .setParameter("alunoId", alunoId)
                .getResultList();

        List<Map<String, Object>> timeline = new ArrayList<>();
        for (Object[] row : rows) {
            timeline.add(Map.of(
                    "mes",     row[0],
                    "aspecto", row[1],
                    "total",   ((Number) row[2]).longValue()
            ));
        }

        return Map.of(
                "alunoId",    alunoId,
                "granularity", granularity,
                "timeline",   timeline
        );
    }

    // -------------------------------------------------------------------
    // Resumo financeiro (view vw_financeiro_mensal)
    // -------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public Map<String, Object> getFinanceiroResumo(LocalDate startDate, LocalDate endDate) {
        List<Object[]> rows = em.createNativeQuery("""
                SELECT
                    ano_referencia,
                    mes_referencia,
                    total_mensalidades,
                    valor_total_esperado,
                    valor_total_recebido,
                    valor_inadimplente,
                    percentual_inadimplencia
                FROM vw_financeiro_mensal
                ORDER BY ano_referencia DESC, mes_referencia DESC
                LIMIT 24
                """)
                .getResultList();

        List<Map<String, Object>> meses = new ArrayList<>();
        double totalRecebido   = 0;
        double totalInadimpl   = 0;
        for (Object[] row : rows) {
            double recebido   = row[4] != null ? ((Number) row[4]).doubleValue() : 0;
            double inadimpl   = row[5] != null ? ((Number) row[5]).doubleValue() : 0;
            totalRecebido    += recebido;
            totalInadimpl    += inadimpl;
            meses.add(Map.of(
                    "ano",                   row[0],
                    "mes",                   row[1],
                    "totalMensalidades",     ((Number) row[2]).longValue(),
                    "valorEsperado",         ((Number) row[3]).doubleValue(),
                    "valorRecebido",         recebido,
                    "valorInadimplente",     inadimpl,
                    "percentualInadimplencia", row[6] != null ? ((Number) row[6]).doubleValue() : 0.0
            ));
        }

        return Map.of(
                "resumoMensal",       meses,
                "totalRecebido24m",   totalRecebido,
                "totalInadimpl24m",   totalInadimpl,
                "geradoEm",           LocalDate.now().toString()
        );
    }

    // -------------------------------------------------------------------
    // Dashboard de uso do sistema
    // -------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public Map<String, Object> getUsageDashboard(String period) {
        // Usuários ativos (com login nos últimos 30 dias)
        Object totalUsuarios = em.createNativeQuery(
                "SELECT COUNT(*) FROM usuarios WHERE ativo = true")
                .getSingleResult();

        // Observações criadas nos últimos 30 dias
        Object obsUltimos30 = em.createNativeQuery("""
                SELECT COUNT(*) FROM observacoes_desenvolvimento
                WHERE created_at >= DATE_SUB(NOW(), INTERVAL 30 DAY)
                """)
                .getSingleResult();

        // Notificações enviadas
        Object notifEnviadas = em.createNativeQuery("""
                SELECT COUNT(*) FROM logs_envio_notificacoes
                WHERE status_envio = 'ENVIADO'
                  AND created_at >= DATE_SUB(NOW(), INTERVAL 30 DAY)
                """)
                .getSingleResult();

        return Map.of(
                "period",             period,
                "totalUsuariosAtivos", ((Number) totalUsuarios).longValue(),
                "observacoes30d",      ((Number) obsUltimos30).longValue(),
                "notificacoesEnviadas30d", ((Number) notifEnviadas).longValue(),
                "geradoEm",            LocalDate.now().toString()
        );
    }

    // -------------------------------------------------------------------
    // Dashboard da secretaria (view vw_dashboard_secretaria)
    // -------------------------------------------------------------------
    public Map<String, Object> getDashboardSecretaria() {
        Object[] row = (Object[]) em.createNativeQuery("""
                SELECT
                    total_alunos_ativos,
                    matriculas_ativas,
                    contratos_pendentes,
                    mensalidades_atrasadas,
                    lgpd_pendentes
                FROM vw_dashboard_secretaria
                """)
                .getSingleResult();

        return Map.of(
                "totalAlunosAtivos",      ((Number) row[0]).longValue(),
                "matriculasAtivas",       ((Number) row[1]).longValue(),
                "contratosPendentes",     ((Number) row[2]).longValue(),
                "mensalidadesAtrasadas",  ((Number) row[3]).longValue(),
                "lgpdPendentes",          ((Number) row[4]).longValue()
        );
    }
}
