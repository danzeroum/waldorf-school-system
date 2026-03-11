package br.edu.waldorf.modules.notificacao.domain.repository;

import br.edu.waldorf.modules.notificacao.domain.model.LogEnvioNotificacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogEnvioNotificacaoRepository extends JpaRepository<LogEnvioNotificacao, Long> {

    Page<LogEnvioNotificacao> findByUsuarioIdOrderByCreatedAtDesc(Long usuarioId, Pageable pageable);

    @Query("SELECT l FROM LogEnvioNotificacao l WHERE l.usuario.id = :uid AND l.statusEnvio = 'PENDENTE' ORDER BY l.dataHoraEnvioPlanejado")
    List<LogEnvioNotificacao> findPendentesByUsuario(@Param("uid") Long usuarioId);

    @Query("SELECT l FROM LogEnvioNotificacao l WHERE l.statusEnvio = 'PENDENTE' AND l.dataHoraEnvioPlanejado <= CURRENT_TIMESTAMP")
    List<LogEnvioNotificacao> findPendentesParaEnvio();

    long countByUsuarioIdAndStatusEnvio(
            Long usuarioId,
            LogEnvioNotificacao.StatusEnvio statusEnvio
    );
}
