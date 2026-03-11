package br.edu.waldorf.modules.financeiro.domain.repository;

import br.edu.waldorf.modules.financeiro.domain.model.Contrato;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContratoRepository extends JpaRepository<Contrato, Long> {

    Optional<Contrato> findByNumeroContrato(String numeroContrato);

    List<Contrato> findByAlunoIdOrderByAnoLetivoDesc(Long alunoId);

    Optional<Contrato> findByAlunoIdAndAnoLetivoAndSituacaoNot(
            Long alunoId, Integer anoLetivo, Contrato.SituacaoContrato situacao
    );

    @Query("SELECT c FROM Contrato c WHERE c.situacao = 'PENDENTE' ORDER BY c.createdAt DESC")
    List<Contrato> findPendentes();

    @Query("SELECT c FROM Contrato c WHERE c.situacao = 'ATIVO' AND c.anoLetivo = :ano")
    List<Contrato> findAtivosPorAno(@Param("ano") Integer ano);

    @Query("SELECT c FROM Contrato c " +
           "WHERE (:situacao IS NULL OR c.situacao = :situacao) " +
           "AND (:anoLetivo IS NULL OR c.anoLetivo = :anoLetivo)")
    Page<Contrato> findWithFilters(
            @Param("situacao") Contrato.SituacaoContrato situacao,
            @Param("anoLetivo") Integer anoLetivo,
            Pageable pageable
    );
}
