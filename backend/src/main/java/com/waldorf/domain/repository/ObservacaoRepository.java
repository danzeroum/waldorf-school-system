package com.waldorf.domain.repository;

import com.waldorf.domain.entity.ObservacaoDesenvolvimento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ObservacaoRepository extends JpaRepository<ObservacaoDesenvolvimento, Long> {

    List<ObservacaoDesenvolvimento> findByAlunoIdOrderByDataDesc(Long alunoId);

    Page<ObservacaoDesenvolvimento> findByAlunoId(Long alunoId, Pageable pageable);

    List<ObservacaoDesenvolvimento> findByProfessorId(Long professorId);

    List<ObservacaoDesenvolvimento> findByAlunoIdAndAspecto(Long alunoId, String aspecto);

    List<ObservacaoDesenvolvimento> findByDataBetween(LocalDate inicio, LocalDate fim);

    long countByAlunoId(Long alunoId);
}
