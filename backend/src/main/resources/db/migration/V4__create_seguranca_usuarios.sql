-- ================================================
-- WALDORF SCHOOL SYSTEM - MIGRATION V4
-- MÓDULO: SEGURANÇA E USUÁRIOS
-- ================================================

-- USUÁRIOS
CREATE TABLE usuarios (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    pessoa_id BIGINT UNIQUE NOT NULL,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    ativo BOOLEAN DEFAULT TRUE,
    bloqueado BOOLEAN DEFAULT FALSE,
    tentativas_login_falhas INT DEFAULT 0,
    ultimo_login DATETIME,
    data_expiracao_senha DATE,
    mfa_habilitado BOOLEAN DEFAULT FALSE,
    mfa_secret VARCHAR(100),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (pessoa_id) REFERENCES pessoas(id) ON DELETE CASCADE,
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_ativo (ativo, bloqueado)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- PERFIS/ROLES
CREATE TABLE perfis (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(50) UNIQUE NOT NULL,
    descricao TEXT,
    nivel_acesso INT NOT NULL DEFAULT 1,
    ativo BOOLEAN DEFAULT TRUE,
    
    INDEX idx_nome (nome),
    INDEX idx_nivel (nivel_acesso)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- USUÁRIOS-PERFIS (Relacionamento N:N)
CREATE TABLE usuarios_perfis (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    usuario_id BIGINT NOT NULL,
    perfil_id BIGINT NOT NULL,
    principal BOOLEAN DEFAULT FALSE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (perfil_id) REFERENCES perfis(id) ON DELETE CASCADE,
    UNIQUE KEY uk_usuario_perfil (usuario_id, perfil_id),
    INDEX idx_usuario (usuario_id),
    INDEX idx_perfil (perfil_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- PERMISSÕES
CREATE TABLE permissoes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) UNIQUE NOT NULL,
    descricao TEXT,
    recurso VARCHAR(50) NOT NULL,
    acao VARCHAR(50) NOT NULL,
    escopo ENUM('GLOBAL', 'ORGANIZACAO', 'TURMA', 'PROPRIO') DEFAULT 'GLOBAL',
    
    INDEX idx_recurso_acao (recurso, acao),
    INDEX idx_nome (nome)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- PERFIS-PERMISSÕES
CREATE TABLE perfis_permissoes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    perfil_id BIGINT NOT NULL,
    permissao_id BIGINT NOT NULL,
    
    FOREIGN KEY (perfil_id) REFERENCES perfis(id) ON DELETE CASCADE,
    FOREIGN KEY (permissao_id) REFERENCES permissoes(id) ON DELETE CASCADE,
    UNIQUE KEY uk_perfil_permissao (perfil_id, permissao_id),
    INDEX idx_perfil (perfil_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- CONTEXTO DE USUÁRIO (Para RBAC Contextual)
CREATE TABLE usuario_contextos (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    usuario_id BIGINT NOT NULL,
    tipo_contexto ENUM('TURMA', 'ALUNO', 'CURSO') NOT NULL,
    entidade_id BIGINT NOT NULL,
    ativo BOOLEAN DEFAULT TRUE,
    
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    INDEX idx_usuario_tipo (usuario_id, tipo_contexto),
    INDEX idx_entidade (tipo_contexto, entidade_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- TOKENS DE REFRESH
CREATE TABLE refresh_tokens (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    usuario_id BIGINT NOT NULL,
    token VARCHAR(500) UNIQUE NOT NULL,
    device_id VARCHAR(100),
    device_type ENUM('WEB', 'MOBILE_IOS', 'MOBILE_ANDROID'),
    expiracao DATETIME NOT NULL,
    revogado BOOLEAN DEFAULT FALSE,
    data_revogacao DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    INDEX idx_token (token),
    INDEX idx_usuario (usuario_id),
    INDEX idx_expiracao (expiracao)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- LOGS DO SISTEMA
CREATE TABLE logs_sistema (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    usuario_id BIGINT,
    acao VARCHAR(100) NOT NULL,
    entidade VARCHAR(50),
    entidade_id BIGINT,
    detalhes TEXT,
    ip_address VARCHAR(45),
    user_agent VARCHAR(255),
    metodo_http VARCHAR(10),
    endpoint VARCHAR(255),
    status_code INT,
    duracao_ms INT,
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE SET NULL,
    INDEX idx_usuario_timestamp (usuario_id, timestamp DESC),
    INDEX idx_acao (acao, timestamp DESC),
    INDEX idx_entidade (entidade, entidade_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;