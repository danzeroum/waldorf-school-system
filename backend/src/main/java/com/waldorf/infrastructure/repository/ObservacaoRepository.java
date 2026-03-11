package com.waldorf.infrastructure.repository;

import com.waldorf.domain.entity.ObservacaoDesenvolvimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ObservacaoRepository extends JpaRepository<ObservacaoDesenvolvimento, Long> {
    List<ObservacaoDesenvolvimento> findByAlunoIdOrderByDataDesc(Long alunoId);
    List<ObservacaoDesenvolvimento> findByAlunoIdAndAspecto(Long alunoId, String aspecto);
}
