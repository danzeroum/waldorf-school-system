package br.edu.waldorf.modules.comunidade.domain.repository;

import br.edu.waldorf.modules.comunidade.domain.model.InscricaoEvento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InscricaoEventoRepository extends JpaRepository<InscricaoEvento, Long> {

    List<InscricaoEvento> findByTipoEventoAndEventoId(InscricaoEvento.TipoEvento tipo, Long eventoId);

    Optional<InscricaoEvento> findByTipoEventoAndEventoIdAndPessoaId(
            InscricaoEvento.TipoEvento tipo, Long eventoId, Long pessoaId
    );

    boolean existsByTipoEventoAndEventoIdAndPessoaId(
            InscricaoEvento.TipoEvento tipo, Long eventoId, Long pessoaId
    );
}
