package com.waldorf.infrastructure.repository;

import com.waldorf.domain.entity.Notificacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {

    List<Notificacao> findByUsuarioIdOrderByCreatedAtDesc(Long usuarioId);

    List<Notificacao> findByUsuarioIdAndLidaFalseOrderByCreatedAtDesc(Long usuarioId);

    long countByUsuarioIdAndLidaFalse(Long usuarioId);

    @Modifying
    @Query("UPDATE Notificacao n SET n.lida = true, n.lidaEm = CURRENT_TIMESTAMP WHERE n.usuario.id = :usuarioId AND n.lida = false")
    int marcarTodasComoLidas(Long usuarioId);
}
