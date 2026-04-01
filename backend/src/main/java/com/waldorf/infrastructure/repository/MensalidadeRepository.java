package com.waldorf.infrastructure.repository;

import com.waldorf.domain.entity.Mensalidade;
import com.waldorf.domain.entity.Mensalidade.StatusMensalidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface MensalidadeRepository extends JpaRepository<Mensalidade, Long> {

    List<Mensalidade> findByContratoIdOrderByNumeroAsc(Long contratoId);

    List<Mensalidade> findByStatus(StatusMensalidade status);

    List<Mensalidade> findByDataVencimentoBeforeAndStatus(LocalDate data, StatusMensalidade status);

    // --- queries para resumo financeiro ---

    @Query("SELECT COALESCE(SUM(m.valor), 0) FROM Mensalidade m " +
           "WHERE YEAR(m.dataVencimento) = :ano")
    BigDecimal somarReceitaPrevistaPorAno(@Param("ano") int ano);

    @Query("SELECT COALESCE(SUM(m.valorPago), 0) FROM Mensalidade m " +
           "WHERE m.status IN ('PAGA', 'PARCIAL') AND YEAR(m.dataVencimento) = :ano")
    BigDecimal somarReceitaRecebidaPorAno(@Param("ano") int ano);

    @Query("SELECT COALESCE(SUM(m.valor), 0) FROM Mensalidade m " +
           "WHERE m.status = 'VENCIDA' AND YEAR(m.dataVencimento) = :ano")
    BigDecimal somarVencidosPorAno(@Param("ano") int ano);

    @Query("SELECT COUNT(DISTINCT m.contrato.id) FROM Mensalidade m " +
           "WHERE m.status = 'VENCIDA'")
    long countContratosComVencidos();
}
