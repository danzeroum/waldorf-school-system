-- ====================================================================================================
-- Migration V6: Adicionar campos de auditoria em todas as tabelas
-- Autor: Daniel Lau
-- Data: 2026-01-31
-- Descricao: Adiciona created_at e updated_at apenas nas tabelas que NAO possuem esses campos
-- ====================================================================================================

-- ====================
-- MODULO PESSOA (V1)
-- ====================

-- Tabela PESSOAS
ALTER TABLE pessoas 
    ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- Tabela ENDERECOS
ALTER TABLE enderecos 
    ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- ===============================
-- MODULO ESTRUTURA ESCOLAR (V2)
-- ===============================

-- Tabela ALUNOS
ALTER TABLE alunos 
    ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- Tabela RESPONSAVEIS
ALTER TABLE responsaveis 
    ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- Tabela PROFESSORES
ALTER TABLE professores 
    ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- Tabela CURSOS
ALTER TABLE cursos 
    ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- Tabela TURMAS
ALTER TABLE turmas 
    ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- Tabela MATRICULAS
ALTER TABLE matriculas 
    ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- ===============================
-- MODULO PEDAGOGIA WALDORF (V3)
-- ===============================
-- NOTA: observacoes_desenvolvimento e portfolio_itens JA possuem created_at/updated_at

-- Tabela DESENVOLVIMENTO_WALDORF
ALTER TABLE desenvolvimento_waldorf 
    ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- Tabela EPOCAS_PEDAGOGICAS
ALTER TABLE epocas_pedagogicas 
    ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- Tabela RITMO_DIARIO_SEMANAL
ALTER TABLE ritmo_diario_semanal 
    ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- Tabela RELATORIOS_NARRATIVOS
ALTER TABLE relatorios_narrativos 
    ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- ==================================
-- MODULO SEGURANCA/USUARIOS (V4)
-- ==================================

-- Tabela USUARIOS
ALTER TABLE usuarios 
    ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- Tabela ROLES
ALTER TABLE roles 
    ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- Tabela PERMISSOES
ALTER TABLE permissoes 
    ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- ===============================
-- INDICES PARA OTIMIZACAO
-- ===============================

CREATE INDEX idx_pessoas_created ON pessoas(created_at);
CREATE INDEX idx_pessoas_updated ON pessoas(updated_at);
CREATE INDEX idx_enderecos_created ON enderecos(created_at);
CREATE INDEX idx_matriculas_created ON matriculas(created_at);
CREATE INDEX idx_alunos_created ON alunos(created_at);
