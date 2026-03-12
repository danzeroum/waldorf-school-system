-- Adiciona coluna aspecto que estava faltando na tabela epocas_pedagogicas
ALTER TABLE epocas_pedagogicas
    ADD COLUMN IF NOT EXISTS aspecto VARCHAR(255) NULL;
