package br.edu.waldorf.modules.financeiro.domain.repository;

import br.edu.waldorf.modules.financeiro.domain.model.PlanoMensalidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanoMensalidadeRepository extends JpaRepository<PlanoMensalidade, Long> {

    List<PlanoMensalidade> findByAnoVigenciaAndAtivoTrue(Integer anoVigencia);

    List<PlanoMensalidade> findByAtivoTrueOrderByNome();
}
