package br.edu.waldorf.modules.pedagogia.domain.repository;

import br.edu.waldorf.modules.pedagogia.domain.model.ObservacaoDesenvolvimento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository de Observação de Desenvolvimento
 */
@Repository
public interface ObservacaoDesenvolvimentoRepository extends JpaRepository<ObservacaoDesenvolvimento, Long> {

    Page<ObservacaoDesenvolvimento> findByAlunoIdOrderByDataObservacaoDesc(Long alunoId, Pageable pageable);

    List<ObservacaoDesenvolvimento> findByAlunoIdAndAspectoOrderByDataObservacaoDesc(
            Long alunoId,
            ObservacaoDesenvolvimento.AspectoDensenvolvimento aspecto
    );

    @Query("SELECT o FROM ObservacaoDesenvolvimento o " +
           "WHERE o.professor.id = :professorId " +
           "AND o.dataObservacao >= :desde " +
           "ORDER BY o.dataObservacao DESC")
    List<ObservacaoDesenvolvimento> findRecentesByProfessor(
            @Param("professorId") Long professorId,
            @Param("desde") LocalDate desde
    );

    @Query("SELECT o FROM ObservacaoDesenvolvimento o " +
           "WHERE o.turma.id = :turmaId " +
           "AND o.dataObservacao BETWEEN :inicio AND :fim " +
           "ORDER BY o.dataObservacao DESC")
    List<ObservacaoDesenvolvimento> findByTurmaAndPeriodo(
            @Param("turmaId") Long turmaId,
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim
    );

    @Query("SELECT o FROM ObservacaoDesenvolvimento o " +
           "WHERE o.aluno.id = :alunoId " +
           "AND (:aspecto IS NULL OR o.aspecto = :aspecto) " +
           "AND (:professorId IS NULL OR o.professor.id = :professorId)")
    Page<ObservacaoDesenvolvimento> findWithFilters(
            @Param("alunoId") Long alunoId,
            @Param("aspecto") ObservacaoDesenvolvimento.AspectoDensenvolvimento aspecto,
            @Param("professorId") Long professorId,
            Pageable pageable
    );

    @Query("SELECT COUNT(o) FROM ObservacaoDesenvolvimento o " +
           "WHERE o.aluno.id = :alunoId " +
           "AND o.dataObservacao >= :desde")
    long countRecentesByAluno(@Param("alunoId") Long alunoId, @Param("desde") LocalDate desde);
}
