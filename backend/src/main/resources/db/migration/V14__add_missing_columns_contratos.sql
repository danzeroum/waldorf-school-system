-- ==========================================================
-- Migration V14: Colunas faltantes identificadas pela validação
-- do Hibernate (schema-validation) vs entidades Java
-- ==========================================================

-- A entidade Contrato.java possui o campo `observacoes` (TEXT)
-- que não existe na tabela contratos criada na V1.
ALTER TABLE contratos
    ADD COLUMN IF NOT EXISTS observacoes TEXT NULL AFTER situacao;
