-- =============================================================================
-- Migration V8: Alinha tabela alunos com a entidade Aluno.java
-- A tabela alunos foi criada como subtipo de pessoas (V1),
-- mas a entidade Java espera colunas standalone.
-- =============================================================================

-- Adicionar colunas que a entidade Aluno.java espera e que nao existem na tabela
ALTER TABLE alunos
    ADD COLUMN IF NOT EXISTS nome           VARCHAR(200) NULL,
    ADD COLUMN IF NOT EXISTS data_nascimento DATE         NULL,
    ADD COLUMN IF NOT EXISTS genero         VARCHAR(20)  NULL,
    ADD COLUMN IF NOT EXISTS email          VARCHAR(150) NULL,
    ADD COLUMN IF NOT EXISTS telefone       VARCHAR(20)  NULL,
    ADD COLUMN IF NOT EXISTS ano_ingresso   INT          NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS ativo          BOOLEAN      NOT NULL DEFAULT TRUE,
    ADD COLUMN IF NOT EXISTS temperamento   VARCHAR(100) NULL,
    ADD COLUMN IF NOT EXISTS turma_id       BIGINT       NULL,
    ADD COLUMN IF NOT EXISTS created_at     DATETIME     NULL,
    ADD COLUMN IF NOT EXISTS updated_at     DATETIME     NULL;

-- Adicionar FK para turmas se ainda nao existir
ALTER TABLE alunos
    ADD CONSTRAINT IF NOT EXISTS fk_aluno_turma
        FOREIGN KEY (turma_id) REFERENCES turmas(id) ON DELETE SET NULL;

-- Sincronizar nome a partir de pessoas para registros existentes
UPDATE alunos a
    JOIN pessoas p ON a.id = p.id
SET a.nome        = p.nome_completo,
    a.email       = p.email,
    a.data_nascimento = p.data_nascimento,
    a.ativo       = p.ativo,
    a.created_at  = p.data_cadastro,
    a.updated_at  = p.data_atualizacao
WHERE a.nome IS NULL;
