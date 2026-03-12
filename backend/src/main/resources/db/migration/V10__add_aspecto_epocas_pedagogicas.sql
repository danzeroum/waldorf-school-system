-- Adiciona coluna aspecto que estava faltando na tabela epocas_pedagogicas
-- MySQL 8.0 nao suporta ADD COLUMN IF NOT EXISTS, usando ALTER simples
ALTER TABLE epocas_pedagogicas
    ADD COLUMN aspecto VARCHAR(255) NULL;
