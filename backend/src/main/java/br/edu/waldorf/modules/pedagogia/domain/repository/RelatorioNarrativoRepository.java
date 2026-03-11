package br.edu.waldorf.modules.pedagogia.domain.repository;

import br.edu.waldorf.modules.pedagogia.domain.model.RelatorioNarrativo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository de Relatório Narrativo
 */
@Repository
public interface RelatorioNarrativoRepository extends JpaRepository<RelatorioNarrativo, Long> {

    List<RelatorioNarrativo> findByAlunoIdOrderByDataElaboracaoDesc(Long alunoId);

    Optional<RelatorioNarrativo> findByAlunoIdAndCicloAndPeriodo(Long alunoId, String ciclo, String periodo);

    Page<RelatorioNarrativo> findByStatus(RelatorioNarrativo.StatusRelatorio status, Pageable pageable);

    @Query("SELECT r FROM RelatorioNarrativo r " +
           "WHERE r.turma.id = :turmaId " +
           "AND r.ciclo = :ciclo " +
           "ORDER BY r.aluno.nomeCompleto")
    List<RelatorioNarrativo> findByTurmaECiclo(
            @Param("turmaId") Long turmaId,
            @Param("ciclo") String ciclo
    );

    @Query("SELECT COUNT(r) FROM RelatorioNarrativo r " +
           "WHERE r.turma.id = :turmaId " +
           "AND r.ciclo = :ciclo " +
           "AND r.status = 'RASCUNHO'")
    long countRascunhosByTurmaECiclo(@Param("turmaId") Long turmaId, @Param("ciclo") String ciclo);
}
