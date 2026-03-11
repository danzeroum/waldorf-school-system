package br.edu.waldorf.modules.financeiro.domain.repository;

import br.edu.waldorf.modules.financeiro.domain.model.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {

    List<Pagamento> findByMensalidadeId(Long mensalidadeId);

    Optional<Pagamento> findByGatewayId(String gatewayId);

    @Query("SELECT p FROM Pagamento p " +
           "WHERE p.dataPagamento BETWEEN :inicio AND :fim " +
           "AND p.status = 'CONFIRMADO' " +
           "ORDER BY p.dataPagamento")
    List<Pagamento> findConfirmadosNoPeriodo(
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim
    );
}
