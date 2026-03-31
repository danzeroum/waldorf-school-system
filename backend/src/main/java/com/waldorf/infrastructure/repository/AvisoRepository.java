package com.waldorf.infrastructure.repository;

import com.waldorf.domain.entity.Aviso;
import com.waldorf.domain.enums.TipoAviso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AvisoRepository extends JpaRepository<Aviso, Long> {

    List<Aviso> findByOrderByFixadoDescDataPublicacaoDesc();

    List<Aviso> findByTurmaIdOrderByDataPublicacaoDesc(Long turmaId);

    List<Aviso> findByTipoOrderByDataPublicacaoDesc(TipoAviso tipo);

    @Query("SELECT a FROM Aviso a WHERE a.dataExpiracao IS NULL OR a.dataExpiracao >= CURRENT_DATE ORDER BY a.fixado DESC, a.dataPublicacao DESC")
    List<Aviso> findAtivos();
}
