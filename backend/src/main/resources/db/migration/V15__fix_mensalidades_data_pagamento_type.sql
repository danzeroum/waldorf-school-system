-- ==========================================================
-- Migration V15: Corrige tipo da coluna data_pagamento
-- na tabela mensalidades.
--
-- Problema: banco criado com DATETIME (versão antiga da V12),
-- mas a entity Mensalidade.java mapeia como LocalDate (DATE).
-- O Hibernate schema-validation rejeita a divergência de tipo.
-- ==========================================================

ALTER TABLE mensalidades
    MODIFY COLUMN data_pagamento DATE NULL;
