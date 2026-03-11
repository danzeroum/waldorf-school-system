package br.edu.waldorf.modules.comunidade.domain.repository;

import br.edu.waldorf.modules.comunidade.domain.model.Mutirao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MutiraoRepository extends JpaRepository<Mutirao, Long> {

    List<Mutirao> findByStatusOrderByDataMutirao(Mutirao.StatusMutirao status);

    @Query("SELECT m FROM Mutirao m WHERE m.dataMutirao >= :hoje AND m.status NOT IN ('CANCELADO','CONCLUIDO') ORDER BY m.dataMutirao")
    List<Mutirao> findProximos(@Param("hoje") LocalDate hoje);
}
