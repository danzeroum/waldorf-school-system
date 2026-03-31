package com.waldorf.infrastructure.repository;

import com.waldorf.domain.entity.ConsentimentoLgpd;
import com.waldorf.domain.enums.StatusConsentimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsentimentoLgpdRepository extends JpaRepository<ConsentimentoLgpd, Long> {

    List<ConsentimentoLgpd> findByOrderByCreatedAtDesc();

    List<ConsentimentoLgpd> findByStatusOrderByCreatedAtDesc(StatusConsentimento status);

    List<ConsentimentoLgpd> findByAlunoId(Long alunoId);

    long countByStatus(StatusConsentimento status);

    @Query("SELECT COUNT(c) FROM ConsentimentoLgpd c WHERE c.status = 'ACEITO'")
    long countAtivos();
}
