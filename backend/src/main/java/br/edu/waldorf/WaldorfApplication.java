package br.edu.waldorf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ARQUIVO LEGADO — NÃO É A MAINCLASS ATIVA.
 *
 * A aplicação principal está em com.waldorf.WaldorfApplication.
 * Este arquivo será removido junto com todo o pacote br.edu.waldorf
 * no próximo cleanup local (git rm -rf src/main/java/br).
 *
 * NÃO adicionar @SpringBootApplication com scanBasePackages aqui —
 * causaria 107+ BeanDefinitionOverrideException com com.waldorf.*
 */
@SpringBootApplication
public class WaldorfApplication {
    public static void main(String[] args) {
        SpringApplication.run(WaldorfApplication.class, args);
    }
}
