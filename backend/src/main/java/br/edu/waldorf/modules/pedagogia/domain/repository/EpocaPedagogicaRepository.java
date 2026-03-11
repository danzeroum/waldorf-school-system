package br.edu.waldorf.modules.pedagogia.domain.repository;

import br.edu.waldorf.modules.pedagogia.domain.model.EpocaPedagogica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository de Época Pedagógica
 */
@Repository
public interface EpocaPedagogicaRepository extends JpaRepository<EpocaPedagogica, Long> {

    List<EpocaPedagogica> findByTurmaIdOrderByDataInicio(Long turmaId);

    @Query("SELECT e FROM EpocaPedagogica e WHERE e.turma.id = :turmaId AND e.status = 'EM_ANDAMENTO'")
    Optional<EpocaPedagogica> findEmAndamentoByTurma(@Param("turmaId") Long turmaId);

    @Query("SELECT e FROM EpocaPedagogica e " +
           "WHERE e.turma.id = :turmaId " +
           "AND e.dataInicio <= :data AND e.dataFim >= :data")
    Optional<EpocaPedagogica> findByTurmaAndData(
            @Param("turmaId") Long turmaId,
            @Param("data") LocalDate data
    );

    @Query("SELECT e FROM EpocaPedagogica e WHERE e.status = 'PLANEJADA' AND e.dataInicio <= :hoje")
    List<EpocaPedagogica> findPlanejadas(@Param("hoje") LocalDate hoje);

    List<EpocaPedagogica> findByTurmaIdAndStatus(Long turmaId, EpocaPedagogica.StatusEpoca status);
}
