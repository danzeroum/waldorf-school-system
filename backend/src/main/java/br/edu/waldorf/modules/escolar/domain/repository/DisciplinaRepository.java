package br.edu.waldorf.modules.escolar.domain.repository;

import br.edu.waldorf.modules.escolar.domain.model.Disciplina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository de Disciplina
 */
@Repository
public interface DisciplinaRepository extends JpaRepository<Disciplina, Long> {

    List<Disciplina> findByAtivoTrueOrderByNome();

    List<Disciplina> findByAreaConhecimento(Disciplina.AreaConhecimento area);

    boolean existsByCodigo(String codigo);
}
