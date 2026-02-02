package br.edu.waldorf.modules.security.domain.repository;

import br.edu.waldorf.modules.security.domain.model.Permissao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PermissaoRepository extends JpaRepository<Permissao, Long> {

    Optional<Permissao> findByNome(String nome);
}
