package br.edu.waldorf.modules.escolar.domain.repository;

import br.edu.waldorf.modules.escolar.domain.model.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository de Curso
 */
@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {

    List<Curso> findByAtivoTrueOrderByOrdemExibicao();

    List<Curso> findByNivelEnsino(Curso.NivelEnsino nivelEnsino);
}
