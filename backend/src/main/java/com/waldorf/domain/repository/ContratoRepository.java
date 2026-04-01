package com.waldorf.domain.repository;

import com.waldorf.domain.entity.Contrato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContratoRepository extends JpaRepository<Contrato, Long> {

    List<Contrato> findByAlunoId(Long alunoId);

    List<Contrato> findByStatus(String status);

    Optional<Contrato> findByAlunoIdAndStatus(Long alunoId, String status);

    long countByStatus(String status);

    boolean existsByAlunoIdAndStatus(Long alunoId, String status);
}
