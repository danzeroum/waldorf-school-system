package br.edu.waldorf.modules.pessoa.domain.repository;

import br.edu.waldorf.modules.pessoa.domain.model.Professor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository de Professor
 *
 * @author Sistema Waldorf
 * @version 1.0.0
 */
@Repository
public interface ProfessorRepository extends JpaRepository<Professor, Long> {

    Optional<Professor> findByRegistroProfissional(String registroProfissional);

    boolean existsByRegistroProfissional(String registroProfissional);

    Page<Professor> findBySituacao(Professor.SituacaoProfessor situacao, Pageable pageable);

    @Query("SELECT p FROM Professor p WHERE p.situacao = 'ATIVO' ORDER BY p.nomeCompleto")
    List<Professor> findAllAtivos();

    @Query("SELECT p FROM Professor p JOIN p.turmasTitular t WHERE t.id = :turmaId")
    Optional<Professor> findByTurma(@Param("turmaId") Long turmaId);
}
