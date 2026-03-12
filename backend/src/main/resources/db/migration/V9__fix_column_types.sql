-- =============================================================================
-- Migration V9: Corrige divergências de tipo entre entidades Java e banco
-- Problemas identificados pelo Hibernate schema-validation:
--   1. contratos.ano_letivo       YEAR -> INT  (Contrato.java: int anoLetivo)
--   2. mensalidades.ano_referencia YEAR -> INT  (se houver entidade)
--   3. planos_mensalidade.ano_vigencia YEAR -> INT (se houver entidade)
-- =============================================================================

-- 1. Corrige contratos.ano_letivo: YEAR -> INT
ALTER TABLE contratos
    MODIFY COLUMN ano_letivo INT NOT NULL;

-- 2. Corrige mensalidades.ano_referencia: YEAR -> INT
ALTER TABLE mensalidades
    MODIFY COLUMN ano_referencia INT NOT NULL;

-- 3. Corrige planos_mensalidade.ano_vigencia: YEAR -> INT
ALTER TABLE planos_mensalidade
    MODIFY COLUMN ano_vigencia INT NOT NULL;
