package br.edu.waldorf.modules.security.domain.repository;

import br.edu.waldorf.modules.security.domain.model.Perfil;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PerfilRepository extends JpaRepository<Perfil, Long> {

    Optional<Perfil> findByNome(String nome);
}
