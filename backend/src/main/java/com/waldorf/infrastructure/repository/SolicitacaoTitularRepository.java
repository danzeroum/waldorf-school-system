package com.waldorf.infrastructure.repository;

import com.waldorf.domain.entity.SolicitacaoTitular;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SolicitacaoTitularRepository extends JpaRepository<SolicitacaoTitular, Long> {
}
