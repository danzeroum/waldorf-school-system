-- V11__add_epoca_id_observacoes.sql
-- Adiciona epoca_id e updated_at em observacoes_desenvolvimento
-- Colunas novas sao nullable para nao impactar dados existentes

ALTER TABLE observacoes_desenvolvimento
    ADD COLUMN epoca_id    BIGINT   NULL                                        AFTER professor_id,
    ADD COLUMN updated_at  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP AFTER created_at,
    ADD CONSTRAINT fk_obs_epoca
        FOREIGN KEY (epoca_id) REFERENCES epocas_pedagogicas(id) ON DELETE SET NULL;

CREATE INDEX idx_obs_epoca ON observacoes_desenvolvimento (epoca_id);
