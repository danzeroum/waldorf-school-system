package br.edu.waldorf.modules.comunidade.domain.repository;

import br.edu.waldorf.modules.comunidade.domain.model.MensagemCanal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MensagemCanalRepository extends JpaRepository<MensagemCanal, Long> {

    Page<MensagemCanal> findByCanalIdOrderByCreatedAtDesc(Long canalId, Pageable pageable);

    @Query("SELECT m FROM MensagemCanal m WHERE m.canal.id = :canalId AND m.fixada = true ORDER BY m.createdAt DESC")
    List<MensagemCanal> findFixadasByCanal(@Param("canalId") Long canalId);

    @Query("SELECT m FROM MensagemCanal m WHERE m.canal.id = :canalId AND m.prioridade IN ('ALTA','URGENTE') ORDER BY m.createdAt DESC")
    List<MensagemCanal> findPrioritariasbyCanal(@Param("canalId") Long canalId);
}
