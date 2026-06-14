package com.waldorf.infrastructure.security.crypto;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Criptografia em repouso (AES-256/GCM) para colunas com dados pessoais sensíveis (ex.: CPF) —
 * conformidade LGPD/segurança por design. O valor persistido é {@code base64(IV ‖ ciphertext‖tag)}.
 *
 * <p>Tolerante a dados legados: se o valor armazenado não for um texto cifrado válido (ex.: linhas
 * pré-existentes em texto puro), é retornado como está e re-cifrado na próxima gravação.
 */
@Slf4j
@Converter
public class CryptoStringConverter implements AttributeConverter<String, String> {

    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int IV_LENGTH = 12;
    private static final int TAG_BITS = 128;
    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return attribute;
        }
        try {
            byte[] iv = new byte[IV_LENGTH];
            RANDOM.nextBytes(iv);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, EncryptionKeyProvider.key(), new GCMParameterSpec(TAG_BITS, iv));
            byte[] ciphertext = cipher.doFinal(attribute.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            ByteBuffer buffer = ByteBuffer.allocate(iv.length + ciphertext.length);
            buffer.put(iv).put(ciphertext);
            return Base64.getEncoder().encodeToString(buffer.array());
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao criptografar dado sensível", e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return dbData;
        }
        try {
            byte[] decoded = Base64.getDecoder().decode(dbData);
            if (decoded.length <= IV_LENGTH) {
                return dbData; // valor legado/curto demais — trata como texto puro
            }
            ByteBuffer buffer = ByteBuffer.wrap(decoded);
            byte[] iv = new byte[IV_LENGTH];
            buffer.get(iv);
            byte[] ciphertext = new byte[buffer.remaining()];
            buffer.get(ciphertext);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, EncryptionKeyProvider.key(), new GCMParameterSpec(TAG_BITS, iv));
            return new String(cipher.doFinal(ciphertext), java.nio.charset.StandardCharsets.UTF_8);
        } catch (IllegalArgumentException | javax.crypto.AEADBadTagException e) {
            // Não é base64/ciphertext válido → dado legado em texto puro.
            log.debug("Valor não cifrado detectado em coluna criptografada; retornando como texto puro");
            return dbData;
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao descriptografar dado sensível", e);
        }
    }
}
