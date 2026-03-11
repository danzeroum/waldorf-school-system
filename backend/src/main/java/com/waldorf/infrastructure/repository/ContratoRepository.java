package com.waldorf.infrastructure.repository;

import com.waldorf.domain.entity.Contrato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContratoRepository extends JpaRepository<Contrato, Long> {
    List<Contrato> findByAlunoId(Long alunoId);
}
