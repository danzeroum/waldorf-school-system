-- ==========================================================
-- Migration V7: Tabelas faltantes identificadas na análise de gap
-- Comparação planoBancoDadosRelacionais.md vs Migrations V1-V6
-- CORRIGIDO: removidas referências à tabela 'pessoas' inexistente
-- CORRIGIDO: observacoes_desenvolvimento não tem turma_id (join via alunos)
-- CORRIGIDO: removidos TRIGGER e EVENT (requerem SUPER privilege com binlog)
-- A V1 criou: usuarios, professores, responsaveis, alunos, turmas
-- ==========================================================

-- =============================================
-- 1. MÓDULO PESSOAS (complemento V1)
-- =============================================
CREATE TABLE IF NOT EXISTS funcionarios (
    id            BIGINT       PRIMARY KEY AUTO_INCREMENT,
    usuario_id    BIGINT,
    cargo         VARCHAR(100) NOT NULL,
    departamento  ENUM('SECRETARIA','LIMPEZA','COZINHA','PORTARIA','MANUTENCAO','ADMINISTRATIVO','DIRECAO') NOT NULL,
    nome          VARCHAR(200) NOT NULL,
    email         VARCHAR(150),
    cpf           VARCHAR(14),
    data_admissao DATE,
    data_demissao DATE,
    salario_base  DECIMAL(10,2),
    banco         VARCHAR(50),
    agencia       VARCHAR(10),
    conta         VARCHAR(20),
    tipo_conta    ENUM('CORRENTE','POUPANCA'),
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE SET NULL,
    INDEX idx_departamento (departamento)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- 2. MÓDULO PEDAGÓGICO (complemento V3)
-- =============================================
CREATE TABLE IF NOT EXISTS relatorios_narrativos (
    id                              BIGINT PRIMARY KEY AUTO_INCREMENT,
    aluno_id                        BIGINT NOT NULL,
    professor_id                    BIGINT NOT NULL,
    turma_id                        BIGINT NOT NULL,
    ciclo                           VARCHAR(50) NOT NULL,
    periodo                         VARCHAR(50) NOT NULL,
    titulo                          VARCHAR(200) NOT NULL,
    texto_desenvolvimento_fisico    TEXT,
    texto_desenvolvimento_animico   TEXT,
    texto_desenvolvimento_cognitivo TEXT,
    texto_relacao_social            TEXT,
    texto_observacoes_artisticas    TEXT,
    texto_trabalhos_manuais         TEXT,
    texto_conclusao_convite         TEXT,
    data_elaboracao                 DATE NOT NULL,
    data_entrega_pais               DATE,
    status ENUM('RASCUNHO','REVISAO','APROVADO','ENTREGUE') DEFAULT 'RASCUNHO',
    arquivo_pdf_assinado            VARCHAR(500),
    confirmacao_leitura_responsavel BOOLEAN DEFAULT FALSE,
    data_confirmacao_leitura        DATETIME,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (aluno_id)     REFERENCES alunos(id),
    FOREIGN KEY (professor_id) REFERENCES professores(id),
    FOREIGN KEY (turma_id)     REFERENCES turmas(id),
    INDEX idx_aluno_ciclo  (aluno_id, ciclo),
    INDEX idx_status_data  (status, data_elaboracao)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS trabalhos_manuais (
    id                     BIGINT PRIMARY KEY AUTO_INCREMENT,
    aluno_id               BIGINT NOT NULL,
    turma_id               BIGINT,
    titulo                 VARCHAR(200) NOT NULL,
    tecnica                VARCHAR(100),
    descricao              TEXT,
    data_inicio            DATE,
    data_conclusao         DATE,
    dificuldade            ENUM('BAIXA','MEDIA','ALTA'),
    materiais_utilizados   TEXT,
    processo_criativo      TEXT,
    aprendizagens          TEXT,
    foto1_url              VARCHAR(500),
    foto2_url              VARCHAR(500),
    foto3_url              VARCHAR(500),
    observacao_professor   TEXT,
    compartilhar_portfolio BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (aluno_id) REFERENCES alunos(id),
    FOREIGN KEY (turma_id) REFERENCES turmas(id),
    INDEX idx_aluno (aluno_id),
    INDEX idx_turma (turma_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS portfolio_artistico (
    id                BIGINT PRIMARY KEY AUTO_INCREMENT,
    aluno_id          BIGINT NOT NULL,
    turma_id          BIGINT,
    tipo ENUM('DESENHO','AQUARELA','MODELAGEM','ESCULTURA','TEATRO','MUSICA','DANCA','OUTRO') NOT NULL,
    titulo            VARCHAR(200),
    descricao         TEXT,
    data_criacao      DATE,
    tecnica           VARCHAR(100),
    dimensoes         VARCHAR(50),
    midia_url         VARCHAR(500),
    thumbnail_url     VARCHAR(500),
    observacoes       TEXT,
    exposicao_publica BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (aluno_id) REFERENCES alunos(id),
    FOREIGN KEY (turma_id) REFERENCES turmas(id),
    INDEX idx_aluno_tipo (aluno_id, tipo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- 3. MÓDULO FINANCEIRO
-- =============================================
CREATE TABLE IF NOT EXISTS planos_mensalidade (
    id                        BIGINT PRIMARY KEY AUTO_INCREMENT,
    nome                      VARCHAR(100) NOT NULL,
    descricao                 TEXT,
    valor_base                DECIMAL(10,2) NOT NULL,
    numero_parcelas           INT DEFAULT 12,
    desconto_anual_percentual DECIMAL(5,2) DEFAULT 0,
    desconto_irmao_percentual DECIMAL(5,2) DEFAULT 0,
    taxa_matricula            DECIMAL(10,2) DEFAULT 0,
    ativo                     BOOLEAN DEFAULT TRUE,
    ano_vigencia              YEAR NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_ano_ativo (ano_vigencia, ativo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- NOTA: tabela contratos já existe na V1 com estrutura diferente.
-- Esta tabela estende o financeiro com a estrutura completa de contrato novo.
CREATE TABLE IF NOT EXISTS contratos_financeiros (
    id                   BIGINT PRIMARY KEY AUTO_INCREMENT,
    aluno_id             BIGINT NOT NULL,
    responsavel_id       BIGINT NOT NULL,
    plano_id             BIGINT NOT NULL,
    numero_contrato      VARCHAR(30) UNIQUE NOT NULL,
    ano_letivo           YEAR NOT NULL,
    valor_base           DECIMAL(10,2) NOT NULL,
    desconto_total       DECIMAL(10,2) DEFAULT 0,
    valor_final          DECIMAL(10,2) NOT NULL,
    data_assinatura      DATE,
    data_vencimento      DATE,
    data_inicio_vigencia DATE NOT NULL,
    data_fim_vigencia    DATE NOT NULL,
    situacao ENUM('PENDENTE','ATIVO','SUSPENSO','CANCELADO','ENCERRADO') DEFAULT 'PENDENTE',
    forma_pagamento ENUM('BOLETO','PIX','CARTAO_CREDITO','DEBITO_AUTOMATICO') DEFAULT 'BOLETO',
    termos_especiais     JSON,
    observacoes          TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (aluno_id)       REFERENCES alunos(id),
    FOREIGN KEY (responsavel_id) REFERENCES responsaveis(id),
    FOREIGN KEY (plano_id)       REFERENCES planos_mensalidade(id),
    INDEX idx_aluno           (aluno_id),
    INDEX idx_situacao_ano    (situacao, ano_letivo),
    INDEX idx_numero_contrato (numero_contrato)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- NOTA: tabela mensalidades pode já existir na V1. Criada apenas se não existir.
CREATE TABLE IF NOT EXISTS mensalidades (
    id                     BIGINT PRIMARY KEY AUTO_INCREMENT,
    contrato_id            BIGINT NOT NULL,
    numero_parcela         INT NOT NULL,
    mes_referencia         INT NOT NULL,
    ano_referencia         YEAR NOT NULL,
    valor_parcela          DECIMAL(10,2) NOT NULL,
    valor_desconto         DECIMAL(10,2) DEFAULT 0,
    valor_juros            DECIMAL(10,2) DEFAULT 0,
    valor_multa            DECIMAL(10,2) DEFAULT 0,
    valor_pago             DECIMAL(10,2),
    data_vencimento        DATE NOT NULL,
    data_pagamento         DATETIME,
    status ENUM('ABERTA','PAGA','ATRASADA','CANCELADA','NEGOCIADA') DEFAULT 'ABERTA',
    nosso_numero           VARCHAR(50),
    codigo_barras          VARCHAR(100),
    pix_qr_code            TEXT,
    gateway_transaction_id VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (contrato_id) REFERENCES contratos_financeiros(id),
    INDEX idx_contrato_parcela  (contrato_id, numero_parcela),
    INDEX idx_vencimento_status (data_vencimento, status),
    INDEX idx_mes_ano           (ano_referencia, mes_referencia)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS pagamentos (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    mensalidade_id  BIGINT NOT NULL,
    valor_pago      DECIMAL(10,2) NOT NULL,
    data_pagamento  DATETIME NOT NULL,
    forma_pagamento ENUM('BOLETO','PIX','CARTAO_CREDITO','DEBITO_AUTOMATICO','DINHEIRO','TRANSFERENCIA') NOT NULL,
    gateway_id      VARCHAR(100),
    comprovante_url VARCHAR(500),
    status ENUM('PENDENTE','CONFIRMADO','ESTORNADO','FALHA') DEFAULT 'PENDENTE',
    observacoes     TEXT,
    registrado_por  BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (mensalidade_id) REFERENCES mensalidades(id),
    FOREIGN KEY (registrado_por) REFERENCES usuarios(id),
    INDEX idx_mensalidade (mensalidade_id),
    INDEX idx_data        (data_pagamento),
    INDEX idx_gateway     (gateway_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- 4. MÓDULO COMUNIDADE
-- =============================================
CREATE TABLE IF NOT EXISTS canais_comunicacao (
    id                 BIGINT PRIMARY KEY AUTO_INCREMENT,
    nome               VARCHAR(100) NOT NULL,
    tipo ENUM('TURMA','COMISSAO','FESTIVAL','GERAL','DIRETORIA','PAIS','PROFESSORES') NOT NULL,
    descricao          TEXT,
    regras_engajamento TEXT,
    publico            BOOLEAN DEFAULT TRUE,
    moderado           BOOLEAN DEFAULT TRUE,
    turma_id           BIGINT,
    ativo              BOOLEAN DEFAULT TRUE,
    cor_canal          CHAR(7) DEFAULT '#9C27B0',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (turma_id) REFERENCES turmas(id),
    INDEX idx_tipo_ativo (tipo, ativo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS mensagens_canal (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    canal_id    BIGINT NOT NULL,
    autor_id    BIGINT NOT NULL,
    conteudo    TEXT NOT NULL,
    tipo        ENUM('TEXTO','AVISO','ANUNCIO','ENQUETE') DEFAULT 'TEXTO',
    prioridade  ENUM('BAIXA','NORMAL','ALTA','URGENTE') DEFAULT 'NORMAL',
    fixada      BOOLEAN DEFAULT FALSE,
    editada     BOOLEAN DEFAULT FALSE,
    data_edicao DATETIME,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    FOREIGN KEY (canal_id) REFERENCES canais_comunicacao(id),
    FOREIGN KEY (autor_id) REFERENCES usuarios(id),
    INDEX idx_canal_data (canal_id, created_at DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS festivais_comunitarios (
    id                   BIGINT PRIMARY KEY AUTO_INCREMENT,
    nome                 VARCHAR(200) NOT NULL,
    tipo ENUM('FESTIVAL_SAZONAL','BAZAR','APRESENTACAO','REUNIAO_PAIS','FEIRA','OUTRO') NOT NULL,
    data_evento          DATE NOT NULL,
    horario_inicio       TIME,
    horario_fim          TIME,
    local_evento         VARCHAR(200),
    descricao            TEXT,
    responsavel_id       BIGINT,
    limite_participantes INT,
    aberto_comunidade    BOOLEAN DEFAULT TRUE,
    status ENUM('PLANEJADO','CONFIRMADO','EM_ANDAMENTO','CONCLUIDO','CANCELADO') DEFAULT 'PLANEJADO',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (responsavel_id) REFERENCES usuarios(id) ON DELETE SET NULL,
    INDEX idx_data_status (data_evento, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS mutiroes (
    id                    BIGINT PRIMARY KEY AUTO_INCREMENT,
    nome                  VARCHAR(200) NOT NULL,
    descricao             TEXT,
    data_mutirao          DATE NOT NULL,
    horario_inicio        TIME,
    horario_fim           TIME,
    local_mutirao         VARCHAR(200),
    materiais_necessarios TEXT,
    limite_participantes  INT,
    permite_criancas      BOOLEAN DEFAULT TRUE,
    status ENUM('PLANEJADO','CONFIRMADO','EM_ANDAMENTO','CONCLUIDO','CANCELADO') DEFAULT 'PLANEJADO',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_data_status (data_mutirao, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS inscricoes_eventos (
    id                 BIGINT PRIMARY KEY AUTO_INCREMENT,
    tipo_evento        ENUM('FESTIVAL','MUTIRAO') NOT NULL,
    evento_id          BIGINT NOT NULL,
    usuario_id         BIGINT NOT NULL,
    numero_pessoas     INT DEFAULT 1,
    criancas_incluidas INT DEFAULT 0,
    materiais_trazidos TEXT,
    confirmado         BOOLEAN DEFAULT FALSE,
    created_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    UNIQUE KEY uk_evento_usuario (tipo_evento, evento_id, usuario_id),
    INDEX idx_evento (tipo_evento, evento_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- 5. MÓDULO NOTIFICAÇÕES
-- =============================================
CREATE TABLE IF NOT EXISTS preferencias_notificacao (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    usuario_id      BIGINT NOT NULL,
    categoria ENUM('PEDAGOGICO','ADMINISTRATIVO','FINANCEIRO','COMUNIDADE','EMERGENCIA','SISTEMA') NOT NULL,
    canal_email     BOOLEAN DEFAULT TRUE,
    canal_push      BOOLEAN DEFAULT TRUE,
    canal_sms       BOOLEAN DEFAULT FALSE,
    agregacao ENUM('IMEDIATO','RESUMO_DIARIO','RESUMO_SEMANAL') DEFAULT 'IMEDIATO',
    horario_resumo  TIME DEFAULT '18:00:00',
    silencio_inicio TIME DEFAULT '20:00:00',
    silencio_fim    TIME DEFAULT '07:00:00',
    ativo           BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    UNIQUE KEY uk_usuario_categoria (usuario_id, categoria),
    INDEX idx_usuario (usuario_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS logs_envio_notificacoes (
    id                        BIGINT PRIMARY KEY AUTO_INCREMENT,
    usuario_id                BIGINT NOT NULL,
    tipo_conteudo ENUM('OBSERVACAO_NOVA','RELATORIO_PRONTO','MENSALIDADE_GERADA','MENSALIDADE_ATRASADA','EVENTO_PROXIMO','COMUNICADO_GERAL','EMERGENCIA_LOGISTICA') NOT NULL,
    canal ENUM('EMAIL','PUSH','SMS','IN_APP') NOT NULL,
    titulo                    VARCHAR(200) NOT NULL,
    conteudo                  TEXT,
    status_envio ENUM('PENDENTE','ENVIADO','ENTREGUE','LIDO','FALHA','SUPRIMIDO') DEFAULT 'PENDENTE',
    motivo_supressao          VARCHAR(200),
    data_hora_envio_planejado DATETIME NOT NULL,
    data_hora_envio_real      DATETIME,
    data_hora_leitura         DATETIME,
    tentativas                INT DEFAULT 0,
    erro_detalhes             TEXT,
    referencia_tipo           VARCHAR(50),
    referencia_id             BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    INDEX idx_usuario_status (usuario_id, status_envio),
    INDEX idx_tipo_data       (tipo_conteudo, data_hora_envio_planejado),
    INDEX idx_referencia      (referencia_tipo, referencia_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- 6. MÓDULO LGPD (complemento V4)
-- =============================================
CREATE TABLE IF NOT EXISTS consentimentos_lgpd (
    id                 BIGINT PRIMARY KEY AUTO_INCREMENT,
    usuario_id         BIGINT NOT NULL,
    finalidade         VARCHAR(200) NOT NULL,
    descricao          TEXT,
    consentido         BOOLEAN NOT NULL,
    data_consentimento DATETIME NOT NULL,
    data_revogacao     DATETIME,
    ip_consentimento   VARCHAR(45),
    versao_termos      VARCHAR(20),
    coletado_por       BIGINT,
    created_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    FOREIGN KEY (usuario_id)   REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (coletado_por) REFERENCES usuarios(id),
    INDEX idx_usuario_finalidade (usuario_id, finalidade),
    INDEX idx_consentido_data    (consentido, data_consentimento)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- NOTA: solicitacoes_titulares já existe na V1 com estrutura diferente.
-- Esta tabela estende a original com campos LGPD completos.
CREATE TABLE IF NOT EXISTS solicitacoes_titulares_lgpd (
    id                     BIGINT PRIMARY KEY AUTO_INCREMENT,
    usuario_id             BIGINT NOT NULL,
    tipo_solicitacao ENUM('ACESSO','CORRECAO','EXCLUSAO','PORTABILIDADE','REVOGACAO','INFORMACAO') NOT NULL,
    descricao              TEXT NOT NULL,
    status ENUM('ABERTA','EM_ANALISE','EM_ATENDIMENTO','CONCLUIDA','REJEITADA') DEFAULT 'ABERTA',
    data_solicitacao       DATETIME DEFAULT CURRENT_TIMESTAMP,
    prazo_resposta         DATE NOT NULL,
    data_conclusao         DATETIME,
    resposta               TEXT,
    atendido_por           BIGINT,
    justificativa_rejeicao TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id)   REFERENCES usuarios(id),
    FOREIGN KEY (atendido_por) REFERENCES usuarios(id),
    INDEX idx_usuario_tipo (usuario_id, tipo_solicitacao),
    INDEX idx_status_prazo (status, prazo_resposta)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- 7. VIEWS ESTRATÉGICAS
-- =============================================

-- Dashboard secretaria: usa tabela contratos (V1) para matriculas_ativas
-- e contratos_financeiros (V7) para contratos_pendentes
CREATE OR REPLACE VIEW vw_dashboard_secretaria AS
SELECT
    (SELECT COUNT(*) FROM alunos WHERE ativo = TRUE)                                        AS total_alunos_ativos,
    (SELECT COUNT(*) FROM contratos WHERE situacao = 'ATIVO'
        AND YEAR(data_inicio) = YEAR(CURDATE()))                                             AS matriculas_ativas,
    (SELECT COUNT(*) FROM contratos_financeiros WHERE situacao = 'PENDENTE')                AS contratos_pendentes,
    (SELECT COUNT(*) FROM mensalidades WHERE status = 'ATRASADA')                           AS mensalidades_atrasadas,
    (SELECT COUNT(*) FROM solicitacoes_titulares WHERE status IN ('ABERTA','EM_ANALISE'))   AS lgpd_pendentes;

-- CORRIGIDO: observacoes_desenvolvimento (V1) não tem turma_id.
-- O join correto é via alunos: o -> a -> t
CREATE OR REPLACE VIEW vw_resumo_pedagogico_turma AS
SELECT
    t.id                        AS turma_id,
    t.nome                      AS turma_nome,
    t.ano_letivo,
    COUNT(DISTINCT a.id)        AS total_alunos,
    COUNT(DISTINCT o.id)        AS total_observacoes,
    COUNT(DISTINCT CASE WHEN o.created_at >= DATE_SUB(CURDATE(), INTERVAL 30 DAY) THEN o.id END) AS observacoes_ultimo_mes,
    COUNT(DISTINCT r.id)        AS total_relatorios,
    COUNT(DISTINCT CASE WHEN r.status = 'RASCUNHO' THEN r.id END) AS relatorios_rascunho,
    (SELECT ep.titulo FROM epocas_pedagogicas ep
     WHERE ep.turma_id = t.id ORDER BY ep.data_inicio DESC LIMIT 1) AS epoca_atual
FROM turmas t
LEFT JOIN alunos a                      ON a.turma_id = t.id AND a.ativo = TRUE
LEFT JOIN observacoes_desenvolvimento o ON o.aluno_id = a.id
LEFT JOIN relatorios_narrativos r       ON r.turma_id = t.id
WHERE t.ativa = TRUE
GROUP BY t.id, t.nome, t.ano_letivo;

CREATE OR REPLACE VIEW vw_financeiro_mensal AS
SELECT
    m.ano_referencia,
    m.mes_referencia,
    COUNT(*)                                                               AS total_mensalidades,
    SUM(m.valor_parcela)                                                   AS valor_total_esperado,
    SUM(CASE WHEN m.status = 'PAGA'     THEN m.valor_pago  ELSE 0 END)    AS valor_total_recebido,
    SUM(CASE WHEN m.status = 'ATRASADA' THEN m.valor_parcela ELSE 0 END)  AS valor_inadimplente,
    ROUND(
        SUM(CASE WHEN m.status = 'ATRASADA' THEN 1 ELSE 0 END)
        / COUNT(*) * 100, 2
    ) AS percentual_inadimplencia
FROM mensalidades m
GROUP BY m.ano_referencia, m.mes_referencia
ORDER BY m.ano_referencia DESC, m.mes_referencia DESC;

-- =============================================
-- NOTA: TRIGGERS E EVENTS foram removidos desta migration.
-- MySQL requer privilégio SUPER para criar triggers/events com
-- binary logging ativo (log_bin=ON), o que não é disponível ao
-- usuário da aplicação em ambientes containerizados.
-- A lógica de atualização de vagas e status de mensalidades
-- será implementada na camada de serviço (Java/Spring).
-- =============================================
