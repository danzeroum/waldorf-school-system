package br.edu.waldorf.modules.financeiro.domain.repository;

import br.edu.waldorf.modules.financeiro.domain.model.Mensalidade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MensalidadeRepository extends JpaRepository<Mensalidade, Long> {

    List<Mensalidade> findByContratoIdOrderByNumeroParcela(Long contratoId);

    Optional<Mensalidade> findByContratoIdAndMesReferenciaAndAnoReferencia(
            Long contratoId, Integer mes, Integer ano
    );

    @Query("SELECT m FROM Mensalidade m WHERE m.status = 'ATRASADA' ORDER BY m.dataVencimento")
    List<Mensalidade> findAtrasadas();

    @Query("SELECT m FROM Mensalidade m " +
           "WHERE m.status = 'ABERTA' " +
           "AND m.dataVencimento BETWEEN :inicio AND :fim")
    List<Mensalidade> findAVencerNoPeriodo(
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim
    );

    @Query("SELECT m FROM Mensalidade m " +
           "WHERE m.contrato.aluno.id = :alunoId " +
           "ORDER BY m.anoReferencia DESC, m.mesReferencia DESC")
    List<Mensalidade> findByAluno(@Param("alunoId") Long alunoId);

    @Query("SELECT m FROM Mensalidade m " +
           "WHERE (:status IS NULL OR m.status = :status) " +
           "AND (:anoRef IS NULL OR m.anoReferencia = :anoRef) " +
           "AND (:mesRef IS NULL OR m.mesReferencia = :mesRef)")
    Page<Mensalidade> findWithFilters(
            @Param("status") Mensalidade.StatusMensalidade status,
            @Param("anoRef") Integer anoRef,
            @Param("mesRef") Integer mesRef,
            Pageable pageable
    );

    @Query("SELECT COUNT(m) FROM Mensalidade m WHERE m.status = 'ATRASADA'")
    long countAtrasadas();
}
