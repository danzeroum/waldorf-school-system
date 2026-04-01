package com.waldorf.domain.repository;

import com.waldorf.domain.entity.Professor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfessorRepository extends JpaRepository<Professor, Long> {

    List<Professor> findByAtivoTrue();

    List<Professor> findByAtivoFalse();

    Page<Professor> findByAtivo(boolean ativo, Pageable pageable);

    Optional<Professor> findByEmail(String email);

    boolean existsByEmail(String email);
}
