package br.edu.waldorf.modules.notificacao.domain.repository;

import br.edu.waldorf.modules.notificacao.domain.model.PreferenciaNotificacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PreferenciaNotificacaoRepository extends JpaRepository<PreferenciaNotificacao, Long> {

    List<PreferenciaNotificacao> findByUsuarioIdAndAtivoTrue(Long usuarioId);

    Optional<PreferenciaNotificacao> findByUsuarioIdAndCategoria(
            Long usuarioId,
            PreferenciaNotificacao.CategoriaNotificacao categoria
    );
}
