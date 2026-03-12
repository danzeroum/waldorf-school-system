-- =============================================================================
-- Migration V8: Alinha tabela alunos com a entidade Aluno.java
-- Colunas created_at/updated_at ja foram adicionadas pela V6.
-- Colunas abaixo sao as que ainda faltam para a entidade Aluno.java funcionar.
-- =============================================================================

ALTER TABLE alunos
    ADD COLUMN nome            VARCHAR(200) NULL,
    ADD COLUMN data_nascimento DATE         NULL,
    ADD COLUMN genero          VARCHAR(20)  NULL,
    ADD COLUMN email           VARCHAR(150) NULL,
    ADD COLUMN telefone        VARCHAR(20)  NULL,
    ADD COLUMN ano_ingresso    INT          NOT NULL DEFAULT 0,
    ADD COLUMN ativo           BOOLEAN      NOT NULL DEFAULT TRUE,
    ADD COLUMN temperamento    VARCHAR(100) NULL,
    ADD COLUMN turma_id        BIGINT       NULL;

ALTER TABLE alunos
    ADD CONSTRAINT fk_aluno_turma
        FOREIGN KEY (turma_id) REFERENCES turmas(id) ON DELETE SET NULL;

-- Sincronizar nome/email a partir de pessoas para registros existentes
UPDATE alunos a
    JOIN pessoas p ON a.id = p.id
SET a.nome            = p.nome_completo,
    a.email           = p.email,
    a.data_nascimento = p.data_nascimento,
    a.ativo           = p.ativo
WHERE a.nome IS NULL;
