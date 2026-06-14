package com.waldorf.infrastructure.security.crypto;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * Disponibiliza a chave AES (derivada de {@code app.encryption.key}) para o
 * {@link CryptoStringConverter}, que é instanciado pelo provedor JPA e não pelo Spring.
 * A chave é mantida em campo estático preenchido na inicialização do contexto.
 */
@Slf4j
@Component
public class EncryptionKeyProvider {

    private static volatile SecretKeySpec keySpec;

    @Value("${app.encryption.key:}")
    private String configuredKey;

    @PostConstruct
    void init() {
        if (configuredKey == null || configuredKey.isBlank()) {
            log.warn("app.encryption.key não configurada — usando chave de desenvolvimento. "
                    + "NÃO use este padrão em produção.");
            configuredKey = "dev-only-default-key-change-me";
        }
        keySpec = deriveKey(configuredKey);
    }

    static SecretKeySpec key() {
        SecretKeySpec k = keySpec;
        if (k == null) {
            // Fallback defensivo caso o converter seja usado antes do contexto subir.
            k = deriveKey("dev-only-default-key-change-me");
        }
        return k;
    }

    private static SecretKeySpec deriveKey(String secret) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(secret.getBytes(StandardCharsets.UTF_8));
            return new SecretKeySpec(digest, "AES");
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao derivar chave de criptografia", e);
        }
    }
}
