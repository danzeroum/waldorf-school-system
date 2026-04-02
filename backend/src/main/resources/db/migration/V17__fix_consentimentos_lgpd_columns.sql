-- ==========================================================
-- Migration V17: Add missing columns to consentimentos_lgpd
-- The com.waldorf.domain.entity.ConsentimentoLgpd entity expects
-- these columns which were never added to the table created in V1.
-- ==========================================================

ALTER TABLE consentimentos_lgpd
    ADD COLUMN IF NOT EXISTS aluno_id        BIGINT,
    ADD COLUMN IF NOT EXISTS responsavel_id  BIGINT,
    ADD COLUMN IF NOT EXISTS tipo            VARCHAR(50),
    ADD COLUMN IF NOT EXISTS status          VARCHAR(30),
    ADD COLUMN IF NOT EXISTS data_aceite     DATE,
    ADD COLUMN IF NOT EXISTS ip_aceite       VARCHAR(45),
    ADD COLUMN IF NOT EXISTS updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

ALTER TABLE consentimentos_lgpd
    ADD CONSTRAINT fk_consentimentos_aluno
        FOREIGN KEY (aluno_id) REFERENCES alunos(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_consentimentos_responsavel
        FOREIGN KEY (responsavel_id) REFERENCES responsaveis(id) ON DELETE CASCADE;
