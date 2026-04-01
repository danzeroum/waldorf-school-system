package br.edu.waldorf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.boot.autoconfigure.mail.MailSenderValidatorAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Aplicação principal do Sistema Escolar Waldorf.
 *
 * scanBasePackages inclui com.waldorf para registrar os beans desse pacote
 * no contexto Spring (DTOs, Services, Repositories, Controllers).
 *
 * exclude: autoconfiguracões de infraestrutura opcional desativadas no
 * startup — cada módulo configura seus próprios beans via @Configuration
 * quando a infraestrutura estiver disponível.
 *
 * @author Daniel Lau
 * @version 1.0.0
 * @since 2026-01-31
 */
@SpringBootApplication(
    scanBasePackages = {"br.edu.waldorf", "com.waldorf"},
    exclude = {
        RabbitAutoConfiguration.class,
        RedisAutoConfiguration.class,
        RedisRepositoriesAutoConfiguration.class,
        MailSenderAutoConfiguration.class,
        MailSenderValidatorAutoConfiguration.class
    }
)
@EnableCaching
@EnableAsync
@EnableScheduling
public class WaldorfApplication {

    public static void main(String[] args) {
        SpringApplication.run(WaldorfApplication.class, args);
    }
}
