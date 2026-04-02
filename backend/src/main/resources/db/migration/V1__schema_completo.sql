-- =============================================================================
-- WALDORF SCHOOL SYSTEM - SCHEMA DEFINITIVO v1
-- Estado final consolidado: equivale às antigas V1-V15
-- Hibernate 6 + MySQL 8.0 + @Enumerated(EnumType.STRING)
--
-- Entidades mapeadas:
--   Perfil, Usuario, Professor, Responsavel, Aluno, Turma,
--   Contrato, Mensalidade, Funcionario, EpocaPedagogica,
--   ObservacaoDesenvolvimento, RelatorioNarrativo, TrabalhoManual,
--   PortfolioArtistico, PlanoMensalidade, ContratoFinanceiro,
--   Pagamento, CanalComunicacao, MensagemCanal, FestivalComunitario,
--   Mutirao, InscricaoEvento, PreferenciaNotificacao,
--   LogEnvioNotificacao, ConsentimentoLgpd, SolicitacaoTitular
--
-- Credenciais iniciais: admin@waldorf.edu.br / admin123
-- TROCAR A SENHA APOS O PRIMEIRO LOGIN!
-- =============================================================================

-- =============================================================================
-- MÓDULO: SEGURANÇA
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
    id            BIGINT       PRIMARY KEY AUTO_INCREMENT,
    nome          VARCHAR(200) NOT NULL,
    email         VARCHAR(150) NOT NULL UNIQUE,
    especialidade VARCHAR(100),
    ativo         BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at    DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE responsaveis (
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
    autorizado      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at      DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE funcionarios (
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
    created_at    TIMESTAMP    DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at    TIMESTAMP    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE SET NULL,
    INDEX idx_departamento (departamento)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- MÓDULO: ESTRUTURA ESCOLAR
-- =============================================================================

CREATE TABLE turmas (
    id                   BIGINT       PRIMARY KEY AUTO_INCREMENT,
    nome                 VARCHAR(100) NOT NULL,
    ano_letivo           INT          NOT NULL,
    ano_escolar          INT,
    capacidade_maxima    INT          DEFAULT 25,
    ativa                BOOLEAN      NOT NULL DEFAULT TRUE,
    professor_regente_id BIGINT,
    created_at           DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at           DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (professor_regente_id) REFERENCES professores(id) ON DELETE SET NULL,
    INDEX idx_ano_letivo (ano_letivo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE alunos (
    id              BIGINT       PRIMARY KEY AUTO_INCREMENT,
    nome            VARCHAR(200) NOT NULL,
    matricula       VARCHAR(20)  NOT NULL UNIQUE,
    data_nascimento DATE,
    genero          ENUM('MASCULINO','FEMININO','OUTRO','NAO_INFORMADO'),
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

CREATE TABLE aluno_responsaveis (
    aluno_id       BIGINT NOT NULL,
    responsavel_id BIGINT NOT NULL,
    PRIMARY KEY (aluno_id, responsavel_id),
    FOREIGN KEY (aluno_id)       REFERENCES alunos(id)       ON DELETE CASCADE,
    FOREIGN KEY (responsavel_id) REFERENCES responsaveis(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- MÓDULO: FINANCEIRO
-- =============================================================================

CREATE TABLE contratos (
    id                BIGINT        PRIMARY KEY AUTO_INCREMENT,
    aluno_id          BIGINT        NOT NULL,
    ano_letivo        INT           NOT NULL,
    valor_mensalidade DECIMAL(10,2) NOT NULL,
    desconto          DECIMAL(10,2),
    valor_matricula   DECIMAL(10,2),
    total_parcelas    INT           NOT NULL DEFAULT 0,
    dia_vencimento    INT           NOT NULL DEFAULT 0,
    data_inicio       DATE,
    data_fim          DATE,
    situacao          ENUM('ATIVO','ENCERRADO','SUSPENSO','CANCELADO') NOT NULL DEFAULT 'ATIVO',
    observacoes       TEXT,
    tipo_contrato     VARCHAR(50),
    created_at        DATETIME      DEFAULT CURRENT_TIMESTAMP,
    updated_at        DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (aluno_id) REFERENCES alunos(id),
    INDEX idx_aluno    (aluno_id),
    INDEX idx_situacao (situacao)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE planos_mensalidade (
    id                        BIGINT        PRIMARY KEY AUTO_INCREMENT,
    nome                      VARCHAR(100)  NOT NULL,
    descricao                 TEXT,
    valor_base                DECIMAL(10,2) NOT NULL,
    numero_parcelas           INT           DEFAULT 12,
    desconto_anual_percentual DECIMAL(5,2)  DEFAULT 0,
    desconto_irmao_percentual DECIMAL(5,2)  DEFAULT 0,
    taxa_matricula            DECIMAL(10,2) DEFAULT 0,
    ativo                     BOOLEAN       DEFAULT TRUE,
    ano_vigencia              YEAR          NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_ano_ativo (ano_vigencia, ativo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE contratos_financeiros (
    id                   BIGINT        PRIMARY KEY AUTO_INCREMENT,
    aluno_id             BIGINT        NOT NULL,
    responsavel_id       BIGINT        NOT NULL,
    plano_id             BIGINT        NOT NULL,
    numero_contrato      VARCHAR(30)   NOT NULL UNIQUE,
    ano_letivo           YEAR          NOT NULL,
    valor_base           DECIMAL(10,2) NOT NULL,
    desconto_total       DECIMAL(10,2) DEFAULT 0,
    valor_final          DECIMAL(10,2) NOT NULL,
    data_assinatura      DATE,
    data_vencimento      DATE,
    data_inicio_vigencia DATE          NOT NULL,
    data_fim_vigencia    DATE          NOT NULL,
    situacao             ENUM('PENDENTE','ATIVO','SUSPENSO','CANCELADO','ENCERRADO') DEFAULT 'PENDENTE',
    forma_pagamento      ENUM('BOLETO','PIX','CARTAO_CREDITO','DEBITO_AUTOMATICO')  DEFAULT 'BOLETO',
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

CREATE TABLE mensalidades (
    id                     BIGINT        PRIMARY KEY AUTO_INCREMENT,
    contrato_id            BIGINT        NOT NULL,
    numero_parcela         INT           NOT NULL,
    mes_referencia         INT           NOT NULL,
    ano_referencia         YEAR          NOT NULL,
    valor_parcela          DECIMAL(10,2) NOT NULL,
    valor_desconto         DECIMAL(10,2) DEFAULT 0,
    valor_juros            DECIMAL(10,2) DEFAULT 0,
    valor_multa            DECIMAL(10,2) DEFAULT 0,
    valor_pago             DECIMAL(10,2),
    data_vencimento        DATE          NOT NULL,
    data_pagamento         DATE,
    status                 ENUM('ABERTA','PAGA','ATRASADA','CANCELADA','NEGOCIADA') DEFAULT 'ABERTA',
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

CREATE TABLE pagamentos (
    id              BIGINT        PRIMARY KEY AUTO_INCREMENT,
    mensalidade_id  BIGINT        NOT NULL,
    valor_pago      DECIMAL(10,2) NOT NULL,
    data_pagamento  DATETIME      NOT NULL,
    forma_pagamento ENUM('BOLETO','PIX','CARTAO_CREDITO','DEBITO_AUTOMATICO','DINHEIRO','TRANSFERENCIA') NOT NULL,
    gateway_id      VARCHAR(100),
    comprovante_url VARCHAR(500),
    status          ENUM('PENDENTE','CONFIRMADO','ESTORNADO','FALHA') DEFAULT 'PENDENTE',
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
    id           BIGINT       PRIMARY KEY AUTO_INCREMENT,
    aluno_id     BIGINT       NOT NULL,
    professor_id BIGINT       NOT NULL,
    epoca_id     BIGINT,
    aspecto      VARCHAR(255) NOT NULL,
    conteudo     TEXT         NOT NULL,
    privada      BOOLEAN      NOT NULL DEFAULT FALSE,
    data         DATE         NOT NULL,
    created_at   DATETIME     DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (aluno_id)     REFERENCES alunos(id),
    FOREIGN KEY (professor_id) REFERENCES professores(id),
    FOREIGN KEY (epoca_id)     REFERENCES epocas_pedagogicas(id) ON DELETE SET NULL,
    INDEX idx_aluno_data (aluno_id, data)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE relatorios_narrativos (
    id                              BIGINT       PRIMARY KEY AUTO_INCREMENT,
    aluno_id                        BIGINT       NOT NULL,
    professor_id                    BIGINT       NOT NULL,
    turma_id                        BIGINT       NOT NULL,
    ciclo                           VARCHAR(50)  NOT NULL,
    periodo                         VARCHAR(50)  NOT NULL,
    titulo                          VARCHAR(200) NOT NULL,
    texto_desenvolvimento_fisico    TEXT,
    texto_desenvolvimento_animico   TEXT,
    texto_desenvolvimento_cognitivo TEXT,
    texto_relacao_social            TEXT,
    texto_observacoes_artisticas    TEXT,
    texto_trabalhos_manuais         TEXT,
    texto_conclusao_convite         TEXT,
    data_elaboracao                 DATE         NOT NULL,
    data_entrega_pais               DATE,
    status                          ENUM('RASCUNHO','REVISAO','APROVADO','ENTREGUE') DEFAULT 'RASCUNHO',
    arquivo_pdf_assinado            VARCHAR(500),
    confirmacao_leitura_responsavel BOOLEAN      DEFAULT FALSE,
    data_confirmacao_leitura        DATETIME,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (aluno_id)     REFERENCES alunos(id),
    FOREIGN KEY (professor_id) REFERENCES professores(id),
    FOREIGN KEY (turma_id)     REFERENCES turmas(id),
    INDEX idx_aluno_ciclo (aluno_id, ciclo),
    INDEX idx_status_data (status, data_elaboracao)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE trabalhos_manuais (
    id                     BIGINT       PRIMARY KEY AUTO_INCREMENT,
    aluno_id               BIGINT       NOT NULL,
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

CREATE TABLE portfolio_artistico (
    id                BIGINT       PRIMARY KEY AUTO_INCREMENT,
    aluno_id          BIGINT       NOT NULL,
    turma_id          BIGINT,
    tipo              ENUM('DESENHO','AQUARELA','MODELAGEM','ESCULTURA','TEATRO','MUSICA','DANCA','OUTRO') NOT NULL,
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

-- =============================================================================
-- MÓDULO: COMUNIDADE
-- =============================================================================

CREATE TABLE canais_comunicacao (
    id                 BIGINT       PRIMARY KEY AUTO_INCREMENT,
    nome               VARCHAR(100) NOT NULL,
    tipo               ENUM('TURMA','COMISSAO','FESTIVAL','GERAL','DIRETORIA','PAIS','PROFESSORES') NOT NULL,
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

CREATE TABLE mensagens_canal (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    canal_id    BIGINT NOT NULL,
    autor_id    BIGINT NOT NULL,
    conteudo    TEXT   NOT NULL,
    tipo        ENUM('TEXTO','AVISO','ANUNCIO','ENQUETE') DEFAULT 'TEXTO',
    prioridade  ENUM('BAIXA','NORMAL','ALTA','URGENTE')   DEFAULT 'NORMAL',
    fixada      BOOLEAN  DEFAULT FALSE,
    editada     BOOLEAN  DEFAULT FALSE,
    data_edicao DATETIME,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    FOREIGN KEY (canal_id) REFERENCES canais_comunicacao(id),
    FOREIGN KEY (autor_id) REFERENCES usuarios(id),
    INDEX idx_canal_data (canal_id, created_at DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE festivais_comunitarios (
    id                   BIGINT       PRIMARY KEY AUTO_INCREMENT,
    nome                 VARCHAR(200) NOT NULL,
    tipo                 ENUM('FESTIVAL_SAZONAL','BAZAR','APRESENTACAO','REUNIAO_PAIS','FEIRA','OUTRO') NOT NULL,
    data_evento          DATE         NOT NULL,
    horario_inicio       TIME,
    horario_fim          TIME,
    local_evento         VARCHAR(200),
    descricao            TEXT,
    responsavel_id       BIGINT,
    limite_participantes INT,
    aberto_comunidade    BOOLEAN DEFAULT TRUE,
    status               ENUM('PLANEJADO','CONFIRMADO','EM_ANDAMENTO','CONCLUIDO','CANCELADO') DEFAULT 'PLANEJADO',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (responsavel_id) REFERENCES usuarios(id) ON DELETE SET NULL,
    INDEX idx_data_status (data_evento, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE mutiroes (
    id                    BIGINT       PRIMARY KEY AUTO_INCREMENT,
    nome                  VARCHAR(200) NOT NULL,
    descricao             TEXT,
    data_mutirao          DATE         NOT NULL,
    horario_inicio        TIME,
    horario_fim           TIME,
    local_mutirao         VARCHAR(200),
    materiais_necessarios TEXT,
    limite_participantes  INT,
    permite_criancas      BOOLEAN DEFAULT TRUE,
    status                ENUM('PLANEJADO','CONFIRMADO','EM_ANDAMENTO','CONCLUIDO','CANCELADO') DEFAULT 'PLANEJADO',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_data_status (data_mutirao, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE inscricoes_eventos (
    id                 BIGINT PRIMARY KEY AUTO_INCREMENT,
    tipo_evento        ENUM('FESTIVAL','MUTIRAO') NOT NULL,
    evento_id          BIGINT NOT NULL,
    usuario_id         BIGINT NOT NULL,
    numero_pessoas     INT    DEFAULT 1,
    criancas_incluidas INT    DEFAULT 0,
    materiais_trazidos TEXT,
    confirmado         BOOLEAN DEFAULT FALSE,
    created_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    UNIQUE KEY uk_evento_usuario (tipo_evento, evento_id, usuario_id),
    INDEX idx_evento (tipo_evento, evento_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- MÓDULO: NOTIFICAÇÕES
-- =============================================================================

CREATE TABLE preferencias_notificacao (
    id             BIGINT PRIMARY KEY AUTO_INCREMENT,
    usuario_id     BIGINT NOT NULL,
    categoria      ENUM('PEDAGOGICO','ADMINISTRATIVO','FINANCEIRO','COMUNIDADE','EMERGENCIA','SISTEMA') NOT NULL,
    canal_email    BOOLEAN DEFAULT TRUE,
    canal_push     BOOLEAN DEFAULT TRUE,
    canal_sms      BOOLEAN DEFAULT FALSE,
    agregacao      ENUM('IMEDIATO','RESUMO_DIARIO','RESUMO_SEMANAL') DEFAULT 'IMEDIATO',
    horario_resumo TIME    DEFAULT '18:00:00',
    silencio_inicio TIME   DEFAULT '20:00:00',
    silencio_fim    TIME   DEFAULT '07:00:00',
    ativo           BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    UNIQUE KEY uk_usuario_categoria (usuario_id, categoria),
    INDEX idx_usuario (usuario_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE logs_envio_notificacoes (
    id                        BIGINT       PRIMARY KEY AUTO_INCREMENT,
    usuario_id                BIGINT       NOT NULL,
    tipo_conteudo             ENUM('OBSERVACAO_NOVA','RELATORIO_PRONTO','MENSALIDADE_GERADA','MENSALIDADE_ATRASADA','EVENTO_PROXIMO','COMUNICADO_GERAL','EMERGENCIA_LOGISTICA') NOT NULL,
    canal                     ENUM('EMAIL','PUSH','SMS','IN_APP') NOT NULL,
    titulo                    VARCHAR(200) NOT NULL,
    conteudo                  TEXT,
    status_envio              ENUM('PENDENTE','ENVIADO','ENTREGUE','LIDO','FALHA','SUPRIMIDO') DEFAULT 'PENDENTE',
    motivo_supressao          VARCHAR(200),
    data_hora_envio_planejado DATETIME     NOT NULL,
    data_hora_envio_real      DATETIME,
    data_hora_leitura         DATETIME,
    tentativas                INT          DEFAULT 0,
    erro_detalhes             TEXT,
    referencia_tipo           VARCHAR(50),
    referencia_id             BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    INDEX idx_usuario_status (usuario_id, status_envio),
    INDEX idx_tipo_data       (tipo_conteudo, data_hora_envio_planejado),
    INDEX idx_referencia      (referencia_tipo, referencia_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- MÓDULO: LGPD
-- =============================================================================

CREATE TABLE solicitacoes_titulares (
    id         BIGINT   PRIMARY KEY AUTO_INCREMENT,
    tipo       ENUM('ACESSO','CORRECAO','EXCLUSAO','PORTABILIDADE','OPOSICAO') NOT NULL,
    status     ENUM('ABERTA','EM_ANALISE','CONCLUIDA','REJEITADA')             NOT NULL DEFAULT 'ABERTA',
    descricao  TEXT,
    resposta   TEXT,
    prazo      DATE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE consentimentos_lgpd (
    id                 BIGINT       PRIMARY KEY AUTO_INCREMENT,
    usuario_id         BIGINT       NOT NULL,
    finalidade         VARCHAR(200) NOT NULL,
    descricao          TEXT,
    consentido         BOOLEAN      NOT NULL,
    data_consentimento DATETIME     NOT NULL,
    data_revogacao     DATETIME,
    ip_consentimento   VARCHAR(45),
    versao_termos      VARCHAR(20),
    coletado_por       BIGINT,
    created_at         TIMESTAMP    DEFAULT CURRENT_TIMESTAMP NOT NULL,
    FOREIGN KEY (usuario_id)   REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (coletado_por) REFERENCES usuarios(id),
    INDEX idx_usuario_finalidade (usuario_id, finalidade),
    INDEX idx_consentido_data    (consentido, data_consentimento)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE solicitacoes_titulares_lgpd (
    id                     BIGINT   PRIMARY KEY AUTO_INCREMENT,
    usuario_id             BIGINT   NOT NULL,
    tipo_solicitacao       ENUM('ACESSO','CORRECAO','EXCLUSAO','PORTABILIDADE','REVOGACAO','INFORMACAO') NOT NULL,
    descricao              TEXT     NOT NULL,
    status                 ENUM('ABERTA','EM_ANALISE','EM_ATENDIMENTO','CONCLUIDA','REJEITADA') DEFAULT 'ABERTA',
    data_solicitacao       DATETIME DEFAULT CURRENT_TIMESTAMP,
    prazo_resposta         DATE     NOT NULL,
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

-- =============================================================================
-- VIEWS ESTRATÉGICAS
-- =============================================================================

CREATE OR REPLACE VIEW vw_dashboard_secretaria AS
SELECT
    (SELECT COUNT(*) FROM alunos WHERE ativo = TRUE)                                       AS total_alunos_ativos,
    (SELECT COUNT(*) FROM contratos WHERE situacao = 'ATIVO'
        AND YEAR(data_inicio) = YEAR(CURDATE()))                                            AS matriculas_ativas,
    (SELECT COUNT(*) FROM contratos_financeiros WHERE situacao = 'PENDENTE')               AS contratos_pendentes,
    (SELECT COUNT(*) FROM mensalidades WHERE status = 'ATRASADA')                          AS mensalidades_atrasadas,
    (SELECT COUNT(*) FROM solicitacoes_titulares WHERE status IN ('ABERTA','EM_ANALISE'))  AS lgpd_pendentes;

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
    COUNT(*)                                                              AS total_mensalidades,
    SUM(m.valor_parcela)                                                  AS valor_total_esperado,
    SUM(CASE WHEN m.status = 'PAGA'     THEN m.valor_pago  ELSE 0 END)   AS valor_total_recebido,
    SUM(CASE WHEN m.status = 'ATRASADA' THEN m.valor_parcela ELSE 0 END) AS valor_inadimplente,
    ROUND(
        SUM(CASE WHEN m.status = 'ATRASADA' THEN 1 ELSE 0 END)
        / COUNT(*) * 100, 2
    ) AS percentual_inadimplencia
FROM mensalidades m
GROUP BY m.ano_referencia, m.mes_referencia
ORDER BY m.ano_referencia DESC, m.mes_referencia DESC;

-- =============================================================================
-- DADOS INICIAIS
-- Senha: admin123 (bcrypt $2a$10 - gerado e testado)
-- IMPORTANTE: trocar a senha apos o primeiro login!
-- =============================================================================

INSERT INTO perfis (nome) VALUES
    ('ADMIN'),
    ('DIRETOR'),
    ('SECRETARIA'),
    ('PROFESSOR'),
    ('RESPONSAVEL'),
    ('FINANCEIRO');

INSERT INTO usuarios (nome, email, senha, ativo) VALUES
    ('Administrador', 'admin@waldorf.edu.br', '$2a$10$N33ZjTYY7cmDZyhpSrU0wekqZD2vX/ARTivf9tU6qrxrsDA1uVVHS', TRUE);

INSERT INTO usuario_perfis (usuario_id, perfil_id)
SELECT u.id, p.id FROM usuarios u, perfis p
WHERE u.email = 'admin@waldorf.edu.br' AND p.nome = 'ADMIN';
