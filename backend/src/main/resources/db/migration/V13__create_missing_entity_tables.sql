-- ==========================================================
-- Migration V13: Criação das tabelas exigidas pelas @Entity JPA
-- que não existiam no banco após V1-V12
-- ==========================================================

-- =============================================
-- 1. RESPONSAVEIS (Responsavel.java → table=responsaveis)
-- V1 pode ter criado esqueleto, garantimos estrutura correta
-- =============================================
CREATE TABLE IF NOT EXISTS responsaveis (
    id              BIGINT       PRIMARY KEY AUTO_INCREMENT,
    nome            VARCHAR(200) NOT NULL,
    data_nascimento DATE,
    genero          ENUM('MASCULINO','FEMININO','OUTRO','NAO_INFORMADO'),
    email           VARCHAR(150) UNIQUE,
    telefone        VARCHAR(20),
    cpf             VARCHAR(14),
    parentesco      VARCHAR(50),
    profissao       VARCHAR(100),
    empresa         VARCHAR(150),
    autorizado      BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- 2. PROFESSORES (Professor.java → table=professores)
-- =============================================
CREATE TABLE IF NOT EXISTS professores (
    id           BIGINT       PRIMARY KEY AUTO_INCREMENT,
    nome         VARCHAR(200) NOT NULL,
    email        VARCHAR(150) NOT NULL UNIQUE,
    especialidade VARCHAR(150),
    ativo        BOOLEAN NOT NULL DEFAULT TRUE,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- 3. CONTRATOS (Contrato.java → table=contratos)
-- A V7 criou 'contratos_financeiros'; a entity aponta para 'contratos'
-- =============================================
CREATE TABLE IF NOT EXISTS contratos (
    id                 BIGINT         PRIMARY KEY AUTO_INCREMENT,
    aluno_id           BIGINT         NOT NULL,
    ano_letivo         INT            NOT NULL,
    valor_mensalidade  DECIMAL(10,2)  NOT NULL,
    desconto           DECIMAL(10,2),
    valor_matricula    DECIMAL(10,2),
    total_parcelas     INT            NOT NULL DEFAULT 12,
    dia_vencimento     INT            NOT NULL DEFAULT 10,
    data_inicio        DATE,
    data_fim           DATE,
    situacao           ENUM('ATIVO','SUSPENSO','CANCELADO','ENCERRADO') NOT NULL DEFAULT 'ATIVO',
    observacoes        TEXT,
    created_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (aluno_id) REFERENCES alunos(id),
    INDEX idx_aluno_ano (aluno_id, ano_letivo),
    INDEX idx_situacao (situacao)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- 4. SOLICITACOES_TITULARES (SolicitacaoTitular.java → table=solicitacoes_titulares)
-- A V7 criou 'solicitacoes_titulares_lgpd'; a entity aponta para 'solicitacoes_titulares'
-- =============================================
CREATE TABLE IF NOT EXISTS solicitacoes_titulares (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    tipo        ENUM('ACESSO','CORRECAO','EXCLUSAO','PORTABILIDADE','REVOGACAO','INFORMACAO') NOT NULL,
    status      ENUM('ABERTA','EM_ANALISE','EM_ATENDIMENTO','CONCLUIDA','REJEITADA') NOT NULL DEFAULT 'ABERTA',
    descricao   TEXT,
    resposta    TEXT,
    prazo       DATE,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- 5. AVISOS (Aviso.java → table=avisos)
-- =============================================
CREATE TABLE IF NOT EXISTS avisos (
    id               BIGINT  PRIMARY KEY AUTO_INCREMENT,
    titulo           VARCHAR(200) NOT NULL,
    conteudo         TEXT NOT NULL,
    tipo             ENUM('GERAL','TURMA','FINANCEIRO','PEDAGOGICO','URGENTE') NOT NULL,
    turma_id         BIGINT,
    autor_id         BIGINT NOT NULL,
    fixado           BOOLEAN NOT NULL DEFAULT FALSE,
    data_publicacao  DATE NOT NULL,
    data_expiracao   DATE,
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (turma_id) REFERENCES turmas(id) ON DELETE SET NULL,
    FOREIGN KEY (autor_id) REFERENCES usuarios(id),
    INDEX idx_tipo_data (tipo, data_publicacao),
    INDEX idx_turma (turma_id),
    INDEX idx_fixado (fixado)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- 6. COMUNICADOS (Comunicado.java → table=comunicados)
-- =============================================
CREATE TABLE IF NOT EXISTS comunicados (
    id                   BIGINT PRIMARY KEY AUTO_INCREMENT,
    assunto              VARCHAR(300) NOT NULL,
    corpo                TEXT NOT NULL,
    destinatarios        ENUM('TODOS','RESPONSAVEIS','PROFESSORES','FUNCIONARIOS','TURMA_ESPECIFICA') NOT NULL,
    turma_id             BIGINT,
    autor_id             BIGINT NOT NULL,
    data_envio           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total_destinatarios  INT,
    total_lidos          INT DEFAULT 0,
    created_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (turma_id) REFERENCES turmas(id) ON DELETE SET NULL,
    FOREIGN KEY (autor_id) REFERENCES usuarios(id),
    INDEX idx_destinatarios_data (destinatarios, data_envio)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- 7. NOTIFICACOES (Notificacao.java → table=notificacoes)
-- =============================================
CREATE TABLE IF NOT EXISTS notificacoes (
    id             BIGINT PRIMARY KEY AUTO_INCREMENT,
    usuario_id     BIGINT NOT NULL,
    tipo           ENUM('AVISO','COMUNICADO','MENSALIDADE','PEDAGOGICO','SISTEMA','EVENTO') NOT NULL,
    titulo         VARCHAR(200) NOT NULL,
    mensagem       TEXT,
    referencia_id  BIGINT,
    referencia_tipo VARCHAR(50),
    lida           BOOLEAN NOT NULL DEFAULT FALSE,
    lida_em        TIMESTAMP NULL,
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    INDEX idx_usuario_lida (usuario_id, lida),
    INDEX idx_usuario_data (usuario_id, created_at DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- 8. PREFERENCIAS_NOTIFICACAO
-- A V7 criou com estrutura por categoria; a entity PreferenciaNotificacao.java
-- espera uma linha por usuario com campos: email, push, sms, in_app, agregacao,
-- silencio_inicio, silencio_fim. Recriamos com IF NOT EXISTS (não conflita se
-- a V7 já criou com nome diferente de colunas, mas aqui garantimos a estrutura
-- que a entity precisa caso a tabela não exista com esses campos).
-- Se já existe com estrutura da V7 (por usuario+categoria), fazemos ALTER.
-- =============================================
-- Drop e recria apenas se existir com estrutura incompatível (coluna 'categoria' presente)
SET @col_exists = (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME   = 'preferencias_notificacao'
      AND COLUMN_NAME  = 'categoria'
);

SET @sql = IF(@col_exists > 0,
    'RENAME TABLE preferencias_notificacao TO preferencias_notificacao_v7_backup',
    'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

CREATE TABLE IF NOT EXISTS preferencias_notificacao (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    usuario_id      BIGINT NOT NULL UNIQUE,
    email           BOOLEAN NOT NULL DEFAULT TRUE,
    push            BOOLEAN NOT NULL DEFAULT TRUE,
    sms             BOOLEAN NOT NULL DEFAULT FALSE,
    in_app          BOOLEAN NOT NULL DEFAULT TRUE,
    agregacao       ENUM('IMEDIATO','RESUMO_DIARIO','RESUMO_SEMANAL') NOT NULL DEFAULT 'IMEDIATO',
    silencio_inicio TIME,
    silencio_fim    TIME,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- 9. CONSENTIMENTOS_LGPD
-- A V7 criou com usuario_id; a entity ConsentimentoLgpd.java usa
-- aluno_id + responsavel_id. Renomeamos a V7 e recriamos corretamente.
-- =============================================
SET @col2_exists = (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME   = 'consentimentos_lgpd'
      AND COLUMN_NAME  = 'usuario_id'
);

SET @sql2 = IF(@col2_exists > 0,
    'RENAME TABLE consentimentos_lgpd TO consentimentos_lgpd_v7_backup',
    'SELECT 1'
);
PREPARE stmt2 FROM @sql2;
EXECUTE stmt2;
DEALLOCATE PREPARE stmt2;

CREATE TABLE IF NOT EXISTS consentimentos_lgpd (
    id             BIGINT PRIMARY KEY AUTO_INCREMENT,
    aluno_id       BIGINT NOT NULL,
    responsavel_id BIGINT NOT NULL,
    tipo           ENUM('USO_IMAGEM','DADOS_PESSOAIS','COMUNICACAO','SAUDE','OUTRO') NOT NULL,
    status         ENUM('PENDENTE','ACEITO','RECUSADO','REVOGADO') NOT NULL DEFAULT 'PENDENTE',
    versao_termos  VARCHAR(20) NOT NULL,
    data_aceite    DATE,
    data_revogacao DATE,
    ip_aceite      VARCHAR(45),
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (aluno_id)       REFERENCES alunos(id),
    FOREIGN KEY (responsavel_id) REFERENCES responsaveis(id),
    INDEX idx_aluno (aluno_id),
    INDEX idx_responsavel (responsavel_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
