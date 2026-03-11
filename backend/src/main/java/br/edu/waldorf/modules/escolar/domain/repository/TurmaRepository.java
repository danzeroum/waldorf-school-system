package br.edu.waldorf.modules.escolar.domain.repository;

import br.edu.waldorf.modules.escolar.domain.model.Turma;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository de Turma
 */
@Repository
public interface TurmaRepository extends JpaRepository<Turma, Long> {

    Optional<Turma> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

    List<Turma> findByAnoLetivoAndSituacaoIn(Integer anoLetivo, List<Turma.SituacaoTurma> situacoes);

    @Query("SELECT t FROM Turma t WHERE t.anoLetivo = :anoLetivo ORDER BY t.serie, t.nome")
    List<Turma> findByAnoLetivo(@Param("anoLetivo") Integer anoLetivo);

    @Query("SELECT t FROM Turma t WHERE t.professorTitular.id = :professorId")
    List<Turma> findByProfessorTitular(@Param("professorId") Long professorId);

    @Query("SELECT t FROM Turma t WHERE t.curso.id = :cursoId AND t.anoLetivo = :anoLetivo")
    List<Turma> findByCursoAndAnoLetivo(@Param("cursoId") Long cursoId, @Param("anoLetivo") Integer anoLetivo);

    @Query("SELECT t FROM Turma t " +
           "WHERE (:situacao IS NULL OR t.situacao = :situacao) " +
           "AND (:anoLetivo IS NULL OR t.anoLetivo = :anoLetivo) " +
           "AND (:cursoId IS NULL OR t.curso.id = :cursoId)")
    Page<Turma> findWithFilters(
            @Param("situacao") Turma.SituacaoTurma situacao,
            @Param("anoLetivo") Integer anoLetivo,
            @Param("cursoId") Long cursoId,
            Pageable pageable
    );

    @Query("SELECT COUNT(m) FROM Matricula m WHERE m.turma.id = :turmaId AND m.situacao IN ('ATIVA','EM_ANDAMENTO')")
    long countMatriculasAtivas(@Param("turmaId") Long turmaId);
}
