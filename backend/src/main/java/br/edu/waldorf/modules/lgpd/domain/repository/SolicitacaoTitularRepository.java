package br.edu.waldorf.modules.lgpd.domain.repository;

import br.edu.waldorf.modules.lgpd.domain.model.SolicitacaoTitular;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SolicitacaoTitularRepository extends JpaRepository<SolicitacaoTitular, Long> {

    Page<SolicitacaoTitular> findByPessoaIdOrderByDataSolicitacaoDesc(Long pessoaId, Pageable pageable);

    @Query("SELECT s FROM SolicitacaoTitular s WHERE s.status IN ('ABERTA','EM_ANALISE','EM_ATENDIMENTO') ORDER BY s.prazoResposta")
    List<SolicitacaoTitular> findEmAberto();

    @Query("SELECT s FROM SolicitacaoTitular s WHERE s.prazoResposta < :hoje AND s.status IN ('ABERTA','EM_ANALISE','EM_ATENDIMENTO')")
    List<SolicitacaoTitular> findAtrasadas(@Param("hoje") LocalDate hoje);

    @Query("SELECT COUNT(s) FROM SolicitacaoTitular s WHERE s.status IN ('ABERTA','EM_ANALISE')")
    long countPendentes();
}
