package br.edu.waldorf.modules.comunidade.domain.repository;

import br.edu.waldorf.modules.comunidade.domain.model.FestivalComunitario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FestivalComunitarioRepository extends JpaRepository<FestivalComunitario, Long> {

    List<FestivalComunitario> findByStatusOrderByDataEvento(FestivalComunitario.StatusEvento status);

    @Query("SELECT f FROM FestivalComunitario f WHERE f.dataEvento BETWEEN :inicio AND :fim ORDER BY f.dataEvento")
    List<FestivalComunitario> findByPeriodo(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);

    @Query("SELECT f FROM FestivalComunitario f WHERE f.dataEvento >= :hoje AND f.status NOT IN ('CANCELADO','CONCLUIDO') ORDER BY f.dataEvento")
    List<FestivalComunitario> findProximos(@Param("hoje") LocalDate hoje);
}
