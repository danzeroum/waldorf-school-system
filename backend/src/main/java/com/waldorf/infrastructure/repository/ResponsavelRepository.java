package com.waldorf.infrastructure.repository;

import com.waldorf.domain.entity.Responsavel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ResponsavelRepository extends JpaRepository<Responsavel, Long> {

    @Query("SELECT r FROM Responsavel r WHERE :nome IS NULL OR LOWER(r.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    Page<Responsavel> findByNomeContainingIgnoreCaseOrNomeIsNull(
            @Param("nome") String nome, Pageable pageable);
}
