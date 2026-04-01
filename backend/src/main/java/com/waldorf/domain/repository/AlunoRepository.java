package com.waldorf.domain.repository;

import com.waldorf.domain.entity.Aluno;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long> {

    int countByTurmaIdAndAtivoTrue(Long turmaId);

    Page<Aluno> findByAtivoTrue(Pageable pageable);

    Page<Aluno> findByAtivoFalse(Pageable pageable);

    List<Aluno> findByTurmaId(Long turmaId);

    @Query("SELECT a FROM Aluno a WHERE " +
           "(:nome IS NULL OR LOWER(a.nome) LIKE LOWER(CONCAT('%', :nome, '%'))) AND " +
           "(:turmaId IS NULL OR a.turma.id = :turmaId) AND " +
           "(:ativo IS NULL OR a.ativo = :ativo)")
    Page<Aluno> filtrar(
            @Param("nome") String nome,
            @Param("turmaId") Long turmaId,
            @Param("ativo") Boolean ativo,
            Pageable pageable);

    boolean existsByMatricula(String matricula);
}
