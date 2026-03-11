package br.edu.waldorf.modules.lgpd.domain.repository;

import br.edu.waldorf.modules.lgpd.domain.model.ConsentimentoLgpd;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConsentimentoLgpdRepository extends JpaRepository<ConsentimentoLgpd, Long> {

    List<ConsentimentoLgpd> findByPessoaIdOrderByDataConsentimentoDesc(Long pessoaId);

    @Query("SELECT c FROM ConsentimentoLgpd c WHERE c.pessoa.id = :pid AND c.consentido = true AND c.dataRevogacao IS NULL")
    List<ConsentimentoLgpd> findAtivosbyPessoa(@Param("pid") Long pessoaId);

    Optional<ConsentimentoLgpd> findByPessoaIdAndFinalidadeAndDataRevogacaoIsNull(Long pessoaId, String finalidade);
}
