-- ================================================
-- WALDORF SCHOOL SYSTEM - MIGRATION V1
-- MÓDULO: PESSOAS E CADASTROS CENTRAIS
-- ================================================

-- TABELA SUPER TIPO: PESSOAS
CREATE TABLE pessoas (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tipo ENUM('ALUNO', 'RESPONSAVEL', 'PROFESSOR', 'FUNCIONARIO', 'OUTRO') NOT NULL,
    nome_completo VARCHAR(200) NOT NULL,
    cpf VARCHAR(14) UNIQUE,
    rg VARCHAR(20),
    data_nascimento DATE,
    email VARCHAR(150) UNIQUE NOT NULL,
    telefone_principal VARCHAR(20),
    telefone_secundario VARCHAR(20),
    foto_url VARCHAR(500),
    ativo BOOLEAN DEFAULT TRUE,
    data_cadastro DATETIME DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Controle LGPD
    lgpd_consentimento_geral BOOLEAN DEFAULT FALSE,
    lgpd_data_consentimento DATETIME,
    lgpd_base_legal ENUM('CONSENTIMENTO', 'CONTRATO', 'LEGITIMO_INTERESSE', 'OBRIGACAO_LEGAL') DEFAULT 'CONSENTIMENTO',
    classificacao_dados ENUM('PUBLICO', 'INTERNO', 'CONFIDENCIAL', 'SENSIVEL') DEFAULT 'INTERNO',
    data_exclusao_prevista DATE,
    
    INDEX idx_tipo_ativo (tipo, ativo),
    INDEX idx_cpf (cpf),
    INDEX idx_email (email),
    INDEX idx_lgpd (lgpd_consentimento_geral, data_exclusao_prevista)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ENDEREÇOS
CREATE TABLE enderecos (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    pessoa_id BIGINT NOT NULL,
    cep VARCHAR(10),
    logradouro VARCHAR(200),
    numero VARCHAR(10),
    complemento VARCHAR(100),
    bairro VARCHAR(100),
    cidade VARCHAR(100),
    estado CHAR(2),
    tipo ENUM('RESIDENCIAL', 'COMERCIAL') DEFAULT 'RESIDENCIAL',
    principal BOOLEAN DEFAULT TRUE,
    
    FOREIGN KEY (pessoa_id) REFERENCES pessoas(id) ON DELETE CASCADE,
    INDEX idx_pessoa (pessoa_id),
    INDEX idx_cidade_estado (cidade, estado)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ALUNOS (SUBTYPE)
CREATE TABLE alunos (
    id BIGINT PRIMARY KEY,
    matricula VARCHAR(20) UNIQUE NOT NULL,
    nome_social VARCHAR(100),
    nome_pai VARCHAR(200),
    nome_mae VARCHAR(200) NOT NULL,
    naturalidade VARCHAR(100),
    nacionalidade VARCHAR(50) DEFAULT 'Brasileira',
    religiao VARCHAR(50),
    necessidades_especiais TEXT,
    medicamentos_controlados TEXT,
    alergias TEXT,
    plano_saude VARCHAR(100),
    sangue_tipo VARCHAR(5),
    observacoes_medicas TEXT,
    situacao ENUM('ATIVO', 'INATIVO', 'TRANSFERIDO', 'FORMADO', 'DESISTENTE') DEFAULT 'ATIVO',
    data_matricula DATE NOT NULL,
    data_saida DATE,
    motivo_saida TEXT,
    forma_ingresso ENUM('NOVA', 'RENOVACAO', 'TRANSFERENCIA') DEFAULT 'NOVA',
    
    FOREIGN KEY (id) REFERENCES pessoas(id) ON DELETE CASCADE,
    INDEX idx_matricula (matricula),
    INDEX idx_situacao_data (situacao, data_matricula)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- PROFESSORES (SUBTYPE)
CREATE TABLE professores (
    id BIGINT PRIMARY KEY,
    matricula_funcional VARCHAR(20) UNIQUE,
    formacao_academica TEXT,
    area_atuacao VARCHAR(100),
    regime_trabalho ENUM('CLT', 'ESTATUTARIO', 'HORISTA', 'PJ'),
    carga_horaria_semanal INT,
    data_admissao DATE,
    data_demissao DATE,
    titulacao ENUM('GRADUACAO', 'ESPECIALIZACAO', 'MESTRADO', 'DOUTORADO', 'POS_DOUTORADO'),
    pis_pasep VARCHAR(20),
    ctps VARCHAR(20),
    formacao_waldorf TEXT,
    anos_experiencia_waldorf INT,
    tutoria_ativa BOOLEAN DEFAULT FALSE,
    
    FOREIGN KEY (id) REFERENCES pessoas(id) ON DELETE CASCADE,
    INDEX idx_titulacao (titulacao),
    INDEX idx_ativa (tutoria_ativa)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- FUNCIONÁRIOS (SUBTYPE)
CREATE TABLE funcionarios (
    id BIGINT PRIMARY KEY,
    cargo VARCHAR(100) NOT NULL,
    departamento ENUM('SECRETARIA', 'LIMPEZA', 'COZINHA', 'PORTARIA', 'MANUTENCAO', 'ADMINISTRATIVO', 'DIRECAO'),
    data_admissao DATE,
    data_demissao DATE,
    salario_base DECIMAL(10,2),
    banco VARCHAR(50),
    agencia VARCHAR(10),
    conta VARCHAR(20),
    tipo_conta ENUM('CORRENTE', 'POUPANCA'),
    
    FOREIGN KEY (id) REFERENCES pessoas(id) ON DELETE CASCADE,
    INDEX idx_departamento (departamento)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- RESPONSÁVEIS (SUBTYPE)
CREATE TABLE responsaveis (
    id BIGINT PRIMARY KEY,
    profissao VARCHAR(100),
    local_trabalho VARCHAR(150),
    telefone_trabalho VARCHAR(20),
    renda_mensal DECIMAL(10,2),
    estado_civil ENUM('SOLTEIRO', 'CASADO', 'DIVORCIADO', 'VIUVO', 'UNIAO_ESTAVEL'),
    
    FOREIGN KEY (id) REFERENCES pessoas(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- VINCULAÇÃO ALUNO-RESPONSÁVEL
CREATE TABLE responsaveis_alunos (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    aluno_id BIGINT NOT NULL,
    responsavel_id BIGINT NOT NULL,
    tipo_responsabilidade ENUM('PAI', 'MAE', 'RESPONSAVEL_LEGAL', 'AVO', 'TIO', 'OUTRO'),
    principal BOOLEAN DEFAULT FALSE,
    autorizado_buscar BOOLEAN DEFAULT FALSE,
    autorizado_emergencia BOOLEAN DEFAULT TRUE,
    prioridade_contato INT DEFAULT 1,
    guarda_compartilhada BOOLEAN DEFAULT FALSE,
    
    FOREIGN KEY (aluno_id) REFERENCES alunos(id) ON DELETE CASCADE,
    FOREIGN KEY (responsavel_id) REFERENCES responsaveis(id) ON DELETE CASCADE,
    UNIQUE KEY uk_aluno_responsavel (aluno_id, responsavel_id),
    INDEX idx_aluno_principal (aluno_id, principal),
    INDEX idx_responsavel (responsavel_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- REGISTRO DE TRATAMENTO DE DADOS (LGPD - ROPA)
CREATE TABLE registro_tratamento_dados (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    finalidade VARCHAR(200) NOT NULL,
    descricao TEXT,
    base_legal ENUM('CONSENTIMENTO', 'CONTRATO', 'LEGITIMO_INTERESSE', 'OBRIGACAO_LEGAL', 'INTERESSE_VITAL') NOT NULL,
    categorias_dados TEXT,
    compartilhamento TEXT,
    prazo_retencao VARCHAR(100),
    medidas_seguranca TEXT,
    responsavel_id BIGINT,
    data_registro DATE DEFAULT (CURRENT_DATE),
    revisao_anual DATE,
    ativo BOOLEAN DEFAULT TRUE,
    
    FOREIGN KEY (responsavel_id) REFERENCES pessoas(id),
    INDEX idx_finalidade_ativo (finalidade, ativo),
    INDEX idx_base_legal (base_legal)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;