package com.waldorf.infrastructure.repository;

import com.waldorf.domain.entity.Mensalidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MensalidadeRepository extends JpaRepository<Mensalidade, Long> {
    List<Mensalidade> findByContratoIdOrderByNumeroParcela(Long contratoId);
    long countByContratoIdAndStatus(Long contratoId, Mensalidade.StatusMensalidade status);
}
