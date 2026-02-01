-- ================================================
-- WALDORF SCHOOL SYSTEM - MIGRATION V3
-- MÓDULO: PEDAGOGIA WALDORF
-- ================================================

-- DESENVOLVIMENTO WALDORF (DADOS PEDAGÓGICOS)
CREATE TABLE desenvolvimento_waldorf (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    aluno_id BIGINT UNIQUE NOT NULL,
    ritmo_sono TEXT,
    alimentacao_observacoes TEXT,
    desenvolvimento_motor TEXT,
    desenvolvimento_social TEXT,
    despertar_cognitivo TEXT,
    despertar_animico TEXT,
    saude_observacoes TEXT,
    anotacoes_importantes TEXT,
    preferencias_brincadeiras TEXT,
    relacionamento_natureza TEXT,
    data_ultima_avaliacao DATE,
    
    FOREIGN KEY (aluno_id) REFERENCES alunos(id) ON DELETE CASCADE,
    INDEX idx_aluno (aluno_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ÉPOCAS PEDAGÓGICAS
CREATE TABLE epocas_pedagogicas (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    turma_id BIGINT NOT NULL,
    titulo VARCHAR(200) NOT NULL,
    descricao TEXT,
    data_inicio DATE NOT NULL,
    data_fim DATE NOT NULL,
    tema_central TEXT,
    narrativa_introdutoria TEXT,
    atividades_principais TEXT,
    materiais_necessarios TEXT,
    objetivos_desenvolvimento TEXT,
    observacoes_diarias TEXT,
    status ENUM('PLANEJADA', 'EM_ANDAMENTO', 'CONCLUIDA', 'CANCELADA') DEFAULT 'PLANEJADA',
    cor_epoca CHAR(7) DEFAULT '#FF9800',
    
    FOREIGN KEY (turma_id) REFERENCES turmas(id),
    INDEX idx_turma_status (turma_id, status),
    INDEX idx_periodo (data_inicio, data_fim)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- RITMO DIÁRIO/SEMANAL
CREATE TABLE ritmo_diario_semanal (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    turma_id BIGINT NOT NULL,
    dia_semana ENUM('SEGUNDA', 'TERCA', 'QUARTA', 'QUINTA', 'SEXTA', 'SABADO') NOT NULL,
    momento VARCHAR(100) NOT NULL,
    descricao TEXT NOT NULL,
    horario_inicio TIME NOT NULL,
    horario_fim TIME NOT NULL,
    tipo_atividade ENUM('VERSO', 'CIRCULO_RITMICO', 'ATIVIDADE_PRINCIPAL', 'TRABALHO_MANUAL', 'BRINCAR_LIVRE', 'ALIMENTACAO', 'DESPEDIDA'),
    responsavel_id BIGINT,
    local VARCHAR(100),
    ativo BOOLEAN DEFAULT TRUE,
    
    FOREIGN KEY (turma_id) REFERENCES turmas(id),
    FOREIGN KEY (responsavel_id) REFERENCES professores(id),
    UNIQUE KEY uk_turma_dia_momento (turma_id, dia_semana, momento),
    INDEX idx_turma_dia (turma_id, dia_semana)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- OBSERVAÇÕES DE DESENVOLVIMENTO
CREATE TABLE observacoes_desenvolvimento (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    aluno_id BIGINT NOT NULL,
    turma_id BIGINT NOT NULL,
    professor_id BIGINT NOT NULL,
    epoca_id BIGINT,
    data_observacao DATE NOT NULL,
    aspecto ENUM('FISICO', 'ANIMICO', 'COGNITIVO', 'SOCIAL', 'ARTISTICO', 'MANUAL', 'LINGUAGEM', 'NATUREZA', 'OUTRO'),
    observacao_temperamento VARCHAR(100),
    titulo VARCHAR(200),
    descricao TEXT NOT NULL,
    evidencias TEXT,
    sugestoes_apoio TEXT,
    privado BOOLEAN DEFAULT FALSE,
    compartilhar_pais BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (aluno_id) REFERENCES alunos(id),
    FOREIGN KEY (turma_id) REFERENCES turmas(id),
    FOREIGN KEY (professor_id) REFERENCES professores(id),
    FOREIGN KEY (epoca_id) REFERENCES epocas_pedagogicas(id),
    INDEX idx_aluno_turma_data (aluno_id, turma_id, data_observacao DESC),
    INDEX idx_professor (professor_id, data_observacao)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- RELATÓRIOS NARRATIVOS
CREATE TABLE relatorios_narrativos (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    aluno_id BIGINT NOT NULL,
    professor_id BIGINT NOT NULL,
    turma_id BIGINT NOT NULL,
    ciclo VARCHAR(50) NOT NULL,
    periodo VARCHAR(50) NOT NULL,
    titulo VARCHAR(200) NOT NULL,
    
    -- Estrutura narrativa Waldorf
    texto_desenvolvimento_fisico TEXT,
    texto_desenvolvimento_animico TEXT,
    texto_desenvolvimento_cognitivo TEXT,
    texto_relacao_social TEXT,
    texto_observacoes_artisticas TEXT,
    texto_trabalhos_manuais TEXT,
    texto_conclusao_convite TEXT,
    
    -- Controle
    data_elaboracao DATE NOT NULL,
    data_entrega_pais DATE,
    status ENUM('RASCUNHO', 'REVISAO', 'APROVADO', 'ENTREGUE') DEFAULT 'RASCUNHO',
    arquivo_pdf_assinado VARCHAR(500),
    confirmacao_leitura_responsavel BOOLEAN DEFAULT FALSE,
    data_confirmacao_leitura DATETIME,
    
    FOREIGN KEY (aluno_id) REFERENCES alunos(id),
    FOREIGN KEY (professor_id) REFERENCES professores(id),
    FOREIGN KEY (turma_id) REFERENCES turmas(id),
    INDEX idx_aluno_ciclo (aluno_id, ciclo),
    INDEX idx_status_data (status, data_elaboracao)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- PORTFÓLIO DIGITAL
CREATE TABLE portfolio_itens (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    aluno_id BIGINT NOT NULL,
    tipo ENUM('MANUAL_WORK', 'ARTWORK', 'MUSIC', 'THEATER', 'WRITING', 'OTHER'),
    titulo VARCHAR(200) NOT NULL,
    descricao TEXT,
    tecnica VARCHAR(100),
    materiais VARCHAR(200),
    data_criacao DATE,
    arquivo_url VARCHAR(500),
    thumbnail_url VARCHAR(500),
    visivel_pais BOOLEAN DEFAULT TRUE,
    visivel_galeria_publica BOOLEAN DEFAULT FALSE,
    permitir_comentarios BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (aluno_id) REFERENCES alunos(id),
    INDEX idx_aluno_tipo (aluno_id, tipo),
    INDEX idx_data_criacao (data_criacao DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;