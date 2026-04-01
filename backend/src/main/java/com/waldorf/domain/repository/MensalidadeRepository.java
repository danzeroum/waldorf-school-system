package com.waldorf.domain.repository;

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

    List<Mensalidade> findByContratoIdAndStatus(Long contratoId, StatusMensalidade status);

    @Query("SELECT COUNT(m) FROM Mensalidade m WHERE m.status = 'PENDENTE' OR m.status = 'VENCIDA'")
    long countPendentesEVencidas();

    @Query("SELECT COALESCE(SUM(m.valor), 0) FROM Mensalidade m WHERE m.contrato.anoLetivo = :ano")
    BigDecimal somarReceitaPrevistaPorAno(@Param("ano") Integer ano);

    @Query("SELECT COALESCE(SUM(m.valorPago), 0) FROM Mensalidade m WHERE m.status = 'PAGA' AND m.contrato.anoLetivo = :ano")
    BigDecimal somarReceitaRecebidaPorAno(@Param("ano") Integer ano);

    @Query("SELECT COALESCE(SUM(m.valor), 0) FROM Mensalidade m WHERE m.status = 'VENCIDA' AND m.contrato.anoLetivo = :ano")
    BigDecimal somarVencidosPorAno(@Param("ano") Integer ano);

    @Query("SELECT COUNT(DISTINCT m.contrato.id) FROM Mensalidade m WHERE m.status = 'VENCIDA'")
    long countContratosComVencidos();
}
