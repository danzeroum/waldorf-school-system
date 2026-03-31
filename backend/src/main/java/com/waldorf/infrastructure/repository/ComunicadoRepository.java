package com.waldorf.infrastructure.repository;

import com.waldorf.domain.entity.Comunicado;
import com.waldorf.domain.enums.DestinatarioComunicado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComunicadoRepository extends JpaRepository<Comunicado, Long> {

    List<Comunicado> findByOrderByDataEnvioDesc();

    List<Comunicado> findByDestinatariosOrderByDataEnvioDesc(DestinatarioComunicado destinatarios);

    List<Comunicado> findByTurmaIdOrderByDataEnvioDesc(Long turmaId);
}
