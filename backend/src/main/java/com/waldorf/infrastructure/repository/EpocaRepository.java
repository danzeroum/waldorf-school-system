package com.waldorf.infrastructure.repository;

import com.waldorf.domain.entity.EpocaPedagogica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EpocaRepository extends JpaRepository<EpocaPedagogica, Long> {
    List<EpocaPedagogica> findByTurmaId(Long turmaId);
}
