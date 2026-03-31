package br.edu.waldorf.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Configuração isolada do JPA Auditing.
 * Separada de WaldorfApplication para que testes com @WebMvcTest
 * não falhem com "JPA metamodel must not be empty".
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}
