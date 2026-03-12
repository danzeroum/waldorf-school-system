-- =============================================================================
-- WALDORF SCHOOL SYSTEM - SCHEMA DEFINITIVO v1
-- 100% alinhado com entidades Java (Hibernate validation)
-- Todas as tabelas standalone (sem herança de pessoas)
-- =============================================================================

-- =============================================================================
-- MÓDULO: SEGURANÇA - perfis e usuarios
-- =============================================================================

CREATE TABLE perfis (
    id   BIGINT      PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(50) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE usuarios (
    id         BIGINT       PRIMARY KEY AUTO_INCREMENT,
    nome       VARCHAR(200) NOT NULL,
    email      VARCHAR(150) NOT NULL UNIQUE,
    senha      VARCHAR(255) NOT NULL,
    ativo      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_ativo (ativo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE usuario_perfis (
    usuario_id BIGINT NOT NULL,
    perfil_id  BIGINT NOT NULL,
    PRIMARY KEY (usuario_id, perfil_id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (perfil_id)  REFERENCES perfis(id)   ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- MÓDULO: PESSOAS
-- =============================================================================

CREATE TABLE professores (
    id           BIGINT       PRIMARY KEY AUTO_INCREMENT,
    nome         VARCHAR(200) NOT NULL,
    email        VARCHAR(150) NOT NULL UNIQUE,
    especialidade VARCHAR(100),
    ativo        BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at   DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE responsaveis (
    id              BIGINT       PRIMARY KEY AUTO_INCREMENT,
    nome            VARCHAR(200) NOT NULL,
    data_nascimento DATE,
    genero          VARCHAR(20),
    email           VARCHAR(150) UNIQUE,
    telefone        VARCHAR(20),
    cpf             VARCHAR(14),
    parentesco      VARCHAR(50),
    profissao       VARCHAR(100),
    empresa         VARCHAR(150),
    autorizado      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at      DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- MÓDULO: ESTRUTURA ESCOLAR
-- =============================================================================

CREATE TABLE turmas (
    id                  BIGINT       PRIMARY KEY AUTO_INCREMENT,
    nome                VARCHAR(100) NOT NULL,
    ano_letivo          INT          NOT NULL,
    ano_escolar         INT,
    capacidade_maxima   INT          DEFAULT 25,
    ativa               BOOLEAN      NOT NULL DEFAULT TRUE,
    professor_regente_id BIGINT,
    created_at          DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (professor_regente_id) REFERENCES professores(id) ON DELETE SET NULL,
    INDEX idx_ano_letivo (ano_letivo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE alunos (
    id              BIGINT       PRIMARY KEY AUTO_INCREMENT,
    nome            VARCHAR(200) NOT NULL,
    matricula       VARCHAR(20)  NOT NULL UNIQUE,
    data_nascimento DATE,
    genero          VARCHAR(20),
    email           VARCHAR(150),
    telefone        VARCHAR(20),
    ano_ingresso    INT          NOT NULL DEFAULT 0,
    ativo           BOOLEAN      NOT NULL DEFAULT TRUE,
    temperamento    VARCHAR(100),
    turma_id        BIGINT,
    created_at      DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (turma_id) REFERENCES turmas(id) ON DELETE SET NULL,
    INDEX idx_matricula (matricula)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- MÓDULO: FINANCEIRO
-- =============================================================================

CREATE TABLE contratos (
    id                BIGINT         PRIMARY KEY AUTO_INCREMENT,
    aluno_id          BIGINT         NOT NULL,
    ano_letivo        INT            NOT NULL,
    valor_mensalidade DECIMAL(10,2)  NOT NULL,
    desconto          DECIMAL(10,2),
    valor_matricula   DECIMAL(10,2),
    total_parcelas    INT            NOT NULL DEFAULT 0,
    dia_vencimento    INT            NOT NULL DEFAULT 0,
    data_inicio       DATE,
    data_fim          DATE,
    situacao          VARCHAR(30)    NOT NULL DEFAULT 'ATIVO',
    created_at        DATETIME       DEFAULT CURRENT_TIMESTAMP,
    updated_at        DATETIME       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (aluno_id) REFERENCES alunos(id),
    INDEX idx_aluno (aluno_id),
    INDEX idx_situacao (situacao)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- MÓDULO: PEDAGOGIA WALDORF
-- =============================================================================

CREATE TABLE epocas_pedagogicas (
    id          BIGINT       PRIMARY KEY AUTO_INCREMENT,
    turma_id    BIGINT       NOT NULL,
    titulo      VARCHAR(200) NOT NULL,
    materia     VARCHAR(100) NOT NULL,
    aspecto     VARCHAR(255),
    data_inicio DATE         NOT NULL,
    data_fim    DATE,
    descricao   TEXT,
    objetivos   TEXT,
    created_at  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (turma_id) REFERENCES turmas(id),
    INDEX idx_turma (turma_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE observacoes_desenvolvimento (
    id           BIGINT   PRIMARY KEY AUTO_INCREMENT,
    aluno_id     BIGINT   NOT NULL,
    professor_id BIGINT   NOT NULL,
    aspecto      VARCHAR(255) NOT NULL,
    conteudo     TEXT         NOT NULL,
    privada      BOOLEAN      NOT NULL DEFAULT FALSE,
    data         DATE         NOT NULL,
    created_at   DATETIME     DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (aluno_id)     REFERENCES alunos(id),
    FOREIGN KEY (professor_id) REFERENCES professores(id),
    INDEX idx_aluno_data (aluno_id, data)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- DADOS INICIAIS
-- =============================================================================

INSERT INTO perfis (nome) VALUES
    ('ADMIN'),
    ('DIRETOR'),
    ('SECRETARIA'),
    ('PROFESSOR'),
    ('RESPONSAVEL'),
    ('FINANCEIRO');

-- Usuario admin inicial (senha: Admin@2026 - bcrypt)
INSERT INTO usuarios (nome, email, senha, ativo) VALUES
    ('Administrador', 'admin@waldorf.edu.br', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQyCjAfozC0Q.JcwJ7C4oBFO2', TRUE);

INSERT INTO usuario_perfis (usuario_id, perfil_id)
SELECT u.id, p.id FROM usuarios u, perfis p
WHERE u.email = 'admin@waldorf.edu.br' AND p.nome = 'ADMIN';
