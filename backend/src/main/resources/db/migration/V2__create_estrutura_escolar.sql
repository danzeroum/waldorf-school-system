-- ================================================
-- WALDORF SCHOOL SYSTEM - MIGRATION V2
-- MÓDULO: ESTRUTURA ESCOLAR
-- ================================================

-- CURSOS/ETAPAS WALDORF
CREATE TABLE cursos (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    nivel_ensino ENUM('JARDIM', 'INFANTIL', 'FUNDAMENTAL_I', 'FUNDAMENTAL_II', 'ENSINO_MEDIO', 'EJA'),
    serie_inicial INT,
    serie_final INT,
    idade_recomendada_inicial INT,
    idade_recomendada_final INT,
    descricao TEXT,
    objetivos_pedagogicos TEXT,
    ativo BOOLEAN DEFAULT TRUE,
    ordem_exibicao INT,
    cor_identificacao CHAR(7) DEFAULT '#4CAF50',
    
    INDEX idx_nivel (nivel_ensino)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- TURMAS
CREATE TABLE turmas (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(20) UNIQUE NOT NULL,
    nome VARCHAR(100) NOT NULL,
    curso_id BIGINT NOT NULL,
    ano_letivo YEAR NOT NULL,
    serie INT NOT NULL,
    turno ENUM('MATUTINO', 'VESPERTINO', 'NOTURNO', 'INTEGRAL'),
    sala VARCHAR(50),
    capacidade_maxima INT DEFAULT 25,
    vagas_disponiveis INT,
    professor_titular_id BIGINT,
    professor_auxiliar_id BIGINT,
    data_inicio DATE,
    data_fim DATE,
    situacao ENUM('ABERTA', 'EM_ANDAMENTO', 'CONCLUIDA', 'CANCELADA') DEFAULT 'ABERTA',
    cor_turma CHAR(7) DEFAULT '#2196F3',
    
    FOREIGN KEY (curso_id) REFERENCES cursos(id),
    FOREIGN KEY (professor_titular_id) REFERENCES professores(id),
    FOREIGN KEY (professor_auxiliar_id) REFERENCES professores(id),
    INDEX idx_ano_curso (ano_letivo, curso_id),
    INDEX idx_professor (professor_titular_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- MATRÍCULAS
CREATE TABLE matriculas (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    aluno_id BIGINT NOT NULL,
    turma_id BIGINT NOT NULL,
    numero_matricula VARCHAR(30) UNIQUE,
    ano_letivo YEAR NOT NULL,
    data_matricula DATE NOT NULL,
    data_cancelamento DATE,
    motivo_cancelamento TEXT,
    forma_ingresso ENUM('NOVA', 'RENOVACAO', 'TRANSFERENCIA'),
    tipo_ensino ENUM('REGULAR', 'EJA', 'SUPLETIVO'),
    situacao ENUM('ATIVA', 'TRANCADA', 'CANCELADA', 'TRANSFERIDA', 'APROVADA', 'REPROVADA', 'EM_ANDAMENTO') DEFAULT 'EM_ANDAMENTO',
    media_final DECIMAL(5,2),
    frequencia_final DECIMAL(5,2),
    observacoes TEXT,
    
    FOREIGN KEY (aluno_id) REFERENCES alunos(id),
    FOREIGN KEY (turma_id) REFERENCES turmas(id),
    UNIQUE KEY uk_aluno_turma_ano (aluno_id, turma_id, ano_letivo),
    INDEX idx_turma_ano (turma_id, ano_letivo),
    INDEX idx_aluno_situacao (aluno_id, situacao)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- DISCIPLINAS
CREATE TABLE disciplinas (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(20) UNIQUE NOT NULL,
    nome VARCHAR(100) NOT NULL,
    descricao TEXT,
    carga_horaria_total INT,
    area_conhecimento ENUM('LINGUAGENS', 'MATEMATICA', 'CIENCIAS_NATUREZA', 'CIENCIAS_HUMANAS', 'ARTES', 'TRABALHOS_MANUAIS'),
    ativo BOOLEAN DEFAULT TRUE,
    
    INDEX idx_area (area_conhecimento)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- DISCIPLINAS POR TURMA
CREATE TABLE turma_disciplinas (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    turma_id BIGINT NOT NULL,
    disciplina_id BIGINT NOT NULL,
    professor_id BIGINT,
    carga_horaria_semanal INT,
    dia_semana ENUM('SEGUNDA', 'TERCA', 'QUARTA', 'QUINTA', 'SEXTA', 'SABADO'),
    horario_inicio TIME,
    horario_fim TIME,
    
    FOREIGN KEY (turma_id) REFERENCES turmas(id),
    FOREIGN KEY (disciplina_id) REFERENCES disciplinas(id),
    FOREIGN KEY (professor_id) REFERENCES professores(id),
    UNIQUE KEY uk_turma_disciplina (turma_id, disciplina_id),
    INDEX idx_professor (professor_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;