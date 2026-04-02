-- ==========================================================
-- Migration V16: Criar tabelas para entidades JPA sem tabela no banco
-- Entidades: Aviso, Comunicado, Notificacao, PreferenciaNotificacao,
--            SolicitacaoTitular, Responsavel
-- ==========================================================

-- =============================================
-- 1. TABELA avisos (Aviso.java)
-- =============================================
CREATE TABLE IF NOT EXISTS avisos (
    id               BIGINT PRIMARY KEY AUTO_INCREMENT,
    titulo           VARCHAR(255) NOT NULL,
    conteudo         TEXT NOT NULL,
    tipo             ENUM('GERAL','TURMA','URGENTE','EVENTO','CARDAPIO','FESTIVAL','MUTIRAO') NOT NULL,
    turma_id         BIGINT,
    autor_id         BIGINT NOT NULL,
    fixado           TINYINT(1) NOT NULL DEFAULT 0,
    data_publicacao  DATE NOT NULL,
    data_expiracao   DATE,
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (turma_id) REFERENCES turmas(id) ON DELETE SET NULL,
    FOREIGN KEY (autor_id) REFERENCES usuarios(id) ON DELETE RESTRICT,
    INDEX idx_avisos_tipo (tipo),
    INDEX idx_avisos_turma (turma_id),
    INDEX idx_avisos_data_publicacao (data_publicacao)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- 2. TABELA comunicados (Comunicado.java)
-- =============================================
CREATE TABLE IF NOT EXISTS comunicados (
    id                   BIGINT PRIMARY KEY AUTO_INCREMENT,
    assunto              VARCHAR(255) NOT NULL,
    corpo                TEXT NOT NULL,
    destinatarios        ENUM('TODOS','TURMA','RESPONSAVEIS','PROFESSORES') NOT NULL,
    turma_id             BIGINT,
    autor_id             BIGINT NOT NULL,
    data_envio           DATETIME NOT NULL,
    total_destinatarios  INT,
    total_lidos          INT NOT NULL DEFAULT 0,
    created_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (turma_id) REFERENCES turmas(id) ON DELETE SET NULL,
    FOREIGN KEY (autor_id) REFERENCES usuarios(id) ON DELETE RESTRICT,
    INDEX idx_comunicados_destinatarios (destinatarios),
    INDEX idx_comunicados_turma (turma_id),
    INDEX idx_comunicados_data_envio (data_envio)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- 3. TABELA notificacoes (Notificacao.java)
-- =============================================
CREATE TABLE IF NOT EXISTS notificacoes (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    usuario_id      BIGINT NOT NULL,
    tipo            ENUM('MENSALIDADE_VENCENDO','MENSALIDADE_VENCIDA','NOVA_OBSERVACAO','COMUNICADO','EVENTO','SOLICITACAO_LGPD','SISTEMA') NOT NULL,
    titulo          VARCHAR(255) NOT NULL,
    mensagem        TEXT,
    referencia_id   BIGINT,
    referencia_tipo VARCHAR(100),
    lida            TINYINT(1) NOT NULL DEFAULT 0,
    lida_em         DATETIME,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    INDEX idx_notificacoes_usuario (usuario_id),
    INDEX idx_notificacoes_lida (lida),
    INDEX idx_notificacoes_tipo (tipo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- 4. TABELA preferencias_notificacao (PreferenciaNotificacao.java)
-- =============================================
CREATE TABLE IF NOT EXISTS preferencias_notificacao (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    usuario_id      BIGINT NOT NULL UNIQUE,
    email           TINYINT(1) NOT NULL DEFAULT 1,
    push            TINYINT(1) NOT NULL DEFAULT 1,
    sms             TINYINT(1) NOT NULL DEFAULT 0,
    in_app          TINYINT(1) NOT NULL DEFAULT 1,
    agregacao       ENUM('IMEDIATO','RESUMO_DIARIO','RESUMO_SEMANAL') NOT NULL DEFAULT 'IMEDIATO',
    silencio_inicio TIME,
    silencio_fim    TIME,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- 5. TABELA solicitacoes_titulares (SolicitacaoTitular.java)
-- =============================================
CREATE TABLE IF NOT EXISTS solicitacoes_titulares (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    tipo        ENUM('ACESSO','CORRECAO','EXCLUSAO','PORTABILIDADE','OPOSICAO') NOT NULL,
    status      ENUM('ABERTA','EM_ANALISE','CONCLUIDA','REJEITADA') NOT NULL DEFAULT 'ABERTA',
    descricao   TEXT,
    resposta    TEXT,
    prazo       DATE,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_solicitacoes_status (status),
    INDEX idx_solicitacoes_tipo (tipo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- 6. TABELA responsaveis (Responsavel.java)
-- =============================================
CREATE TABLE IF NOT EXISTS responsaveis (
    id               BIGINT PRIMARY KEY AUTO_INCREMENT,
    nome             VARCHAR(255) NOT NULL,
    data_nascimento  DATE,
    genero           ENUM('MASCULINO','FEMININO','OUTRO','NAO_INFORMADO'),
    email            VARCHAR(255) UNIQUE,
    telefone         VARCHAR(30),
    cpf              VARCHAR(14),
    parentesco       VARCHAR(50),
    profissao        VARCHAR(100),
    empresa          VARCHAR(100),
    autorizado       TINYINT(1) NOT NULL DEFAULT 1,
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_responsaveis_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
