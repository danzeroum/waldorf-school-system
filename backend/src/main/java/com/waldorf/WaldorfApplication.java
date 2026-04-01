package com.waldorf;

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
 * Autoconfigurações de infraestrutura (RabbitMQ, Redis, Mail) excluídas
 * para evitar falhas de conexão no startup — cada módulo configura seus
 * próprios beans via @Configuration quando necessário.
 */
@SpringBootApplication(exclude = {
        RabbitAutoConfiguration.class,
        RedisAutoConfiguration.class,
        RedisRepositoriesAutoConfiguration.class,
        MailSenderAutoConfiguration.class,
        MailSenderValidatorAutoConfiguration.class
})
@EnableCaching
@EnableAsync
@EnableScheduling
public class WaldorfApplication {
    public static void main(String[] args) {
        SpringApplication.run(WaldorfApplication.class, args);
    }
}
