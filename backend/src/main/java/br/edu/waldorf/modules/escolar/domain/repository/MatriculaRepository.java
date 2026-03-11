package br.edu.waldorf.modules.escolar.domain.repository;

import br.edu.waldorf.modules.escolar.domain.model.Matricula;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository de Matrícula
 */
@Repository
public interface MatriculaRepository extends JpaRepository<Matricula, Long> {

    boolean existsByAlunoIdAndTurmaIdAndAnoLetivo(Long alunoId, Long turmaId, Integer anoLetivo);

    Optional<Matricula> findByNumeroMatricula(String numeroMatricula);

    List<Matricula> findByAlunoIdOrderByAnoLetivoDesc(Long alunoId);

    @Query("SELECT m FROM Matricula m WHERE m.turma.id = :turmaId AND m.situacao IN ('ATIVA','EM_ANDAMENTO') ORDER BY m.aluno.nomeCompleto")
    List<Matricula> findAtivasByTurma(@Param("turmaId") Long turmaId);

    @Query("SELECT m FROM Matricula m WHERE m.aluno.id = :alunoId AND m.anoLetivo = :anoLetivo")
    Optional<Matricula> findByAlunoAndAnoLetivo(@Param("alunoId") Long alunoId, @Param("anoLetivo") Integer anoLetivo);

    @Query("SELECT m FROM Matricula m " +
           "WHERE (:turmaId IS NULL OR m.turma.id = :turmaId) " +
           "AND (:anoLetivo IS NULL OR m.anoLetivo = :anoLetivo) " +
           "AND (:situacao IS NULL OR m.situacao = :situacao)")
    Page<Matricula> findWithFilters(
            @Param("turmaId") Long turmaId,
            @Param("anoLetivo") Integer anoLetivo,
            @Param("situacao") Matricula.SituacaoMatricula situacao,
            Pageable pageable
    );
}
