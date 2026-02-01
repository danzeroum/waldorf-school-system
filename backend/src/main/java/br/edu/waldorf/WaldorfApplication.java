package br.edu.waldorf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Aplicação principal do Sistema Escolar Waldorf
 * 
 * @author Daniel Lau
 * @version 1.0.0
 * @since 2026-01-31
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableCaching
@EnableAsync
@EnableScheduling
public class WaldorfApplication {

    public static void main(String[] args) {
        SpringApplication.run(WaldorfApplication.class, args);
    }
}