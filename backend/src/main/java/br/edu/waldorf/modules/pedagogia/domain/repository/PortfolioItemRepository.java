package br.edu.waldorf.modules.pedagogia.domain.repository;

import br.edu.waldorf.modules.pedagogia.domain.model.PortfolioItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository de Portfolio Item
 */
@Repository
public interface PortfolioItemRepository extends JpaRepository<PortfolioItem, Long> {

    Page<PortfolioItem> findByAlunoIdOrderByDataCriacaoDesc(Long alunoId, Pageable pageable);

    List<PortfolioItem> findByAlunoIdAndTipo(Long alunoId, PortfolioItem.TipoPortfolio tipo);

    @Query("SELECT p FROM PortfolioItem p WHERE p.aluno.id = :alunoId AND p.visivelPais = true ORDER BY p.dataCriacao DESC")
    List<PortfolioItem> findVisivelPaisByAluno(@Param("alunoId") Long alunoId);

    @Query("SELECT p FROM PortfolioItem p WHERE p.visivelGaleriaPublica = true ORDER BY p.dataCriacao DESC")
    Page<PortfolioItem> findGaleriaPublica(Pageable pageable);
}
