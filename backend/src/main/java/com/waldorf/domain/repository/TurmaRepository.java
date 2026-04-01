package com.waldorf.domain.repository;

import com.waldorf.domain.entity.Turma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TurmaRepository extends JpaRepository<Turma, Long> {

    List<Turma> findByAnoLetivo(int anoLetivo);

    List<Turma> findByAtivaTrue();

    List<Turma> findByAtivaFalse();

    List<Turma> findByProfessorRegenteId(Long professorId);

    boolean existsByNomeAndAnoLetivo(String nome, int anoLetivo);
}
