package br.edu.waldorf.modules.comunidade.domain.repository;

import br.edu.waldorf.modules.comunidade.domain.model.CanalComunicacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CanalComunicacaoRepository extends JpaRepository<CanalComunicacao, Long> {

    List<CanalComunicacao> findByAtivoTrueOrderByNome();

    List<CanalComunicacao> findByTipo(CanalComunicacao.TipoCanal tipo);

    List<CanalComunicacao> findByTurmaId(Long turmaId);
}
