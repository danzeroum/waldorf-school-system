-- =============================================================================
-- WALDORF SCHOOL SYSTEM - SCHEMA COMPLETO v1
-- Migration única e definitiva - todos os módulos consolidados
-- Tipos alinhados com as entidades Java (Hibernate validation)
-- =============================================================================

-- =============================================================================
-- MÓDULO 1: PESSOAS (super-tipo + sub-tipos)
-- =============================================================================

CREATE TABLE pessoas (
    id                       BIGINT       PRIMARY KEY AUTO_INCREMENT,
    tipo                     ENUM('ALUNO','RESPONSAVEL','PROFESSOR','FUNCIONARIO','OUTRO') NOT NULL,
    nome_completo            VARCHAR(200) NOT NULL,
    cpf                      VARCHAR(14)  UNIQUE,
    rg                       VARCHAR(20),
    data_nascimento          DATE,
    email                    VARCHAR(150) UNIQUE NOT NULL,
    telefone_principal       VARCHAR(20),
    telefone_secundario      VARCHAR(20),
    foto_url                 VARCHAR(500),
    ativo                    BOOLEAN      DEFAULT TRUE,
    data_cadastro            DATETIME     DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao         DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    lgpd_consentimento_geral BOOLEAN      DEFAULT FALSE,
    lgpd_data_consentimento  DATETIME,
    lgpd_base_legal          ENUM('CONSENTIMENTO','CONTRATO','LEGITIMO_INTERESSE','OBRIGACAO_LEGAL') DEFAULT 'CONSENTIMENTO',
    classificacao_dados      ENUM('PUBLICO','INTERNO','CONFIDENCIAL','SENSIVEL') DEFAULT 'INTERNO',
    data_exclusao_prevista   DATE,
    INDEX idx_tipo_ativo (tipo, ativo),
    INDEX idx_cpf        (cpf),
    INDEX idx_email      (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE enderecos (
    id          BIGINT       PRIMARY KEY AUTO_INCREMENT,
    pessoa_id   BIGINT       NOT NULL,
    cep         VARCHAR(10),
    logradouro  VARCHAR(200),
    numero      VARCHAR(10),
    complemento VARCHAR(100),
    bairro      VARCHAR(100),
    cidade      VARCHAR(100),
    estado      CHAR(2),
    tipo        ENUM('RESIDENCIAL','COMERCIAL') DEFAULT 'RESIDENCIAL',
    principal   BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (pessoa_id) REFERENCES pessoas(id) ON DELETE CASCADE,
    INDEX idx_pessoa (pessoa_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- alunos: colunas alinhadas com Aluno.java
CREATE TABLE alunos (
    id                      BIGINT       PRIMARY KEY,
    matricula               VARCHAR(20)  UNIQUE NOT NULL,
    nome                    VARCHAR(200),
    data_nascimento         DATE,
    genero                  ENUM('MASCULINO','FEMININO','OUTRO','NAO_INFORMADO'),
    email                   VARCHAR(150),
    telefone                VARCHAR(20),
    ano_ingresso            INT          NOT NULL DEFAULT 0,
    ativo                   BOOLEAN      NOT NULL DEFAULT TRUE,
    temperamento            VARCHAR(100),
    turma_id                BIGINT,
    -- campos legados V1 original
    nome_social             VARCHAR(100),
    nome_pai                VARCHAR(200),
    nome_mae                VARCHAR(200),
    naturalidade            VARCHAR(100),
    nacionalidade           VARCHAR(50)  DEFAULT 'Brasileira',
    religiao                VARCHAR(50),
    necessidades_especiais  TEXT,
    medicamentos_controlados TEXT,
    alergias                TEXT,
    plano_saude             VARCHAR(100),
    sangue_tipo             VARCHAR(5),
    observacoes_medicas     TEXT,
    situacao                ENUM('ATIVO','INATIVO','TRANSFERIDO','FORMADO','DESISTENTE') DEFAULT 'ATIVO',
    data_matricula          DATE,
    data_saida              DATE,
    motivo_saida            TEXT,
    forma_ingresso          ENUM('NOVA','RENOVACAO','TRANSFERENCIA') DEFAULT 'NOVA',
    created_at              DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at              DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (id) REFERENCES pessoas(id) ON DELETE CASCADE,
    INDEX idx_matricula (matricula),
    INDEX idx_situacao   (situacao)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE professores (
    id                        BIGINT      PRIMARY KEY,
    matricula_funcional       VARCHAR(20) UNIQUE,
    formacao_academica        TEXT,
    area_atuacao              VARCHAR(100),
    regime_trabalho           ENUM('CLT','ESTATUTARIO','HORISTA','PJ'),
    carga_horaria_semanal     INT,
    data_admissao             DATE,
    data_demissao             DATE,
    titulacao                 ENUM('GRADUACAO','ESPECIALIZACAO','MESTRADO','DOUTORADO','POS_DOUTORADO'),
    pis_pasep                 VARCHAR(20),
    ctps                      VARCHAR(20),
    formacao_waldorf          TEXT,
    anos_experiencia_waldorf  INT,
    tutoria_ativa             BOOLEAN DEFAULT FALSE,
    created_at                DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at                DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (id) REFERENCES pessoas(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE funcionarios (
    id            BIGINT       PRIMARY KEY,
    cargo         VARCHAR(100) NOT NULL,
    departamento  ENUM('SECRETARIA','LIMPEZA','COZINHA','PORTARIA','MANUTENCAO','ADMINISTRATIVO','DIRECAO') NOT NULL,
    data_admissao DATE,
    data_demissao DATE,
    salario_base  DECIMAL(10,2),
    banco         VARCHAR(50),
    agencia       VARCHAR(10),
    conta         VARCHAR(20),
    tipo_conta    ENUM('CORRENTE','POUPANCA'),
    created_at    DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (id) REFERENCES pessoas(id) ON DELETE CASCADE,
    INDEX idx_departamento (departamento)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE responsaveis (
    id               BIGINT      PRIMARY KEY,
    profissao        VARCHAR(100),
    local_trabalho   VARCHAR(150),
    telefone_trabalho VARCHAR(20),
    renda_mensal     DECIMAL(10,2),
    estado_civil     ENUM('SOLTEIRO','CASADO','DIVORCIADO','VIUVO','UNIAO_ESTAVEL'),
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (id) REFERENCES pessoas(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE responsaveis_alunos (
    id                    BIGINT PRIMARY KEY AUTO_INCREMENT,
    aluno_id              BIGINT NOT NULL,
    responsavel_id        BIGINT NOT NULL,
    tipo_responsabilidade ENUM('PAI','MAE','RESPONSAVEL_LEGAL','AVO','TIO','OUTRO'),
    principal             BOOLEAN DEFAULT FALSE,
    autorizado_buscar     BOOLEAN DEFAULT FALSE,
    autorizado_emergencia BOOLEAN DEFAULT TRUE,
    prioridade_contato    INT     DEFAULT 1,
    guarda_compartilhada  BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (aluno_id)      REFERENCES alunos(id)      ON DELETE CASCADE,
    FOREIGN KEY (responsavel_id) REFERENCES responsaveis(id) ON DELETE CASCADE,
    UNIQUE KEY uk_aluno_responsavel (aluno_id, responsavel_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE registro_tratamento_dados (
    id                 BIGINT       PRIMARY KEY AUTO_INCREMENT,
    finalidade         VARCHAR(200) NOT NULL,
    descricao          TEXT,
    base_legal         ENUM('CONSENTIMENTO','CONTRATO','LEGITIMO_INTERESSE','OBRIGACAO_LEGAL','INTERESSE_VITAL') NOT NULL,
    categorias_dados   TEXT,
    compartilhamento   TEXT,
    prazo_retencao     VARCHAR(100),
    medidas_seguranca  TEXT,
    responsavel_id     BIGINT,
    data_registro      DATE DEFAULT (CURRENT_DATE),
    revisao_anual      DATE,
    ativo              BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (responsavel_id) REFERENCES pessoas(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- MÓDULO 2: ESTRUTURA ESCOLAR
-- =============================================================================

CREATE TABLE anos_letivos (
    id          BIGINT       PRIMARY KEY AUTO_INCREMENT,
    ano         INT          NOT NULL UNIQUE,
    descricao   VARCHAR(100),
    data_inicio DATE         NOT NULL,
    data_fim    DATE         NOT NULL,
    ativo       BOOLEAN      DEFAULT TRUE,
    created_at  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE turmas (
    id                BIGINT       PRIMARY KEY AUTO_INCREMENT,
    nome              VARCHAR(100) NOT NULL,
    ano_letivo        INT          NOT NULL,
    ciclo             VARCHAR(50),
    nivel             VARCHAR(50),
    capacidade_maxima INT          DEFAULT 25,
    vagas_disponiveis INT          DEFAULT 25,
    situacao          ENUM('ABERTA','EM_ANDAMENTO','ENCERRADA','CANCELADA') DEFAULT 'ABERTA',
    professor_id      BIGINT,
    sala              VARCHAR(20),
    turno             ENUM('MANHA','TARDE','INTEGRAL'),
    created_at        DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at        DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (professor_id) REFERENCES professores(id),
    INDEX idx_ano_situacao (ano_letivo, situacao)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- adiciona FK de alunos.turma_id agora que turmas existe
ALTER TABLE alunos
    ADD CONSTRAINT fk_aluno_turma FOREIGN KEY (turma_id) REFERENCES turmas(id) ON DELETE SET NULL;

CREATE TABLE matriculas (
    id               BIGINT   PRIMARY KEY AUTO_INCREMENT,
    aluno_id         BIGINT   NOT NULL,
    turma_id         BIGINT   NOT NULL,
    ano_letivo       INT      NOT NULL,
    situacao         ENUM('ATIVA','EM_ANDAMENTO','TRANCADA','CANCELADA','CONCLUIDA') DEFAULT 'ATIVA',
    data_matricula   DATE     NOT NULL,
    data_cancelamento DATE,
    motivo_cancelamento TEXT,
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (aluno_id) REFERENCES alunos(id),
    FOREIGN KEY (turma_id) REFERENCES turmas(id),
    INDEX idx_aluno_ano  (aluno_id, ano_letivo),
    INDEX idx_turma_situacao (turma_id, situacao)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- MÓDULO 3: PEDAGOGIA WALDORF
-- =============================================================================

CREATE TABLE epocas_pedagogicas (
    id             BIGINT       PRIMARY KEY AUTO_INCREMENT,
    turma_id       BIGINT       NOT NULL,
    titulo         VARCHAR(200) NOT NULL,
    materia        VARCHAR(100) NOT NULL,
    data_inicio    DATE         NOT NULL,
    data_fim       DATE         NOT NULL,
    status         ENUM('PLANEJADA','EM_ANDAMENTO','CONCLUIDA','CANCELADA') DEFAULT 'PLANEJADA',
    descricao      TEXT,
    objetivos      TEXT,
    created_at     DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (turma_id) REFERENCES turmas(id),
    INDEX idx_turma_status (turma_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE observacoes_desenvolvimento (
    id           BIGINT   PRIMARY KEY AUTO_INCREMENT,
    aluno_id     BIGINT   NOT NULL,
    professor_id BIGINT   NOT NULL,
    turma_id     BIGINT,
    data_observacao DATE  NOT NULL,
    tipo         ENUM('PEDAGOGICA','COMPORTAMENTAL','SOCIAL','ARTISTICA','MOTORA','OUTRA') DEFAULT 'PEDAGOGICA',
    conteudo     TEXT     NOT NULL,
    privada      BOOLEAN  DEFAULT FALSE,
    created_at   DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (aluno_id)     REFERENCES alunos(id),
    FOREIGN KEY (professor_id) REFERENCES professores(id),
    FOREIGN KEY (turma_id)     REFERENCES turmas(id),
    INDEX idx_aluno_data (aluno_id, data_observacao)
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
    created_at                      DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at                      DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (aluno_id)     REFERENCES alunos(id),
    FOREIGN KEY (professor_id) REFERENCES professores(id),
    FOREIGN KEY (turma_id)     REFERENCES turmas(id),
    INDEX idx_aluno_ciclo (aluno_id, ciclo)
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
    compartilhar_portfolio BOOLEAN  DEFAULT TRUE,
    created_at             DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at             DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (aluno_id) REFERENCES alunos(id),
    FOREIGN KEY (turma_id) REFERENCES turmas(id)
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
    exposicao_publica BOOLEAN  DEFAULT FALSE,
    created_at        DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at        DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (aluno_id) REFERENCES alunos(id),
    FOREIGN KEY (turma_id) REFERENCES turmas(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- MÓDULO 4: SEGURANÇA E USUÁRIOS
-- =============================================================================

CREATE TABLE perfis (
    id   BIGINT       PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(50)  UNIQUE NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE usuarios (
    id                    BIGINT       PRIMARY KEY AUTO_INCREMENT,
    pessoa_id             BIGINT       UNIQUE,
    login                 VARCHAR(100) UNIQUE NOT NULL,
    senha_hash            VARCHAR(255) NOT NULL,
    ativo                 BOOLEAN      DEFAULT TRUE,
    ultimo_acesso         DATETIME,
    tentativas_falhas     INT          DEFAULT 0,
    bloqueado_ate         DATETIME,
    token_reset_senha     VARCHAR(255),
    token_reset_expira    DATETIME,
    mfa_ativo             BOOLEAN      DEFAULT FALSE,
    mfa_secret            VARCHAR(100),
    created_at            DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at            DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (pessoa_id) REFERENCES pessoas(id),
    INDEX idx_login (login),
    INDEX idx_ativo (ativo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE usuarios_perfis (
    usuario_id BIGINT NOT NULL,
    perfil_id  BIGINT NOT NULL,
    PRIMARY KEY (usuario_id, perfil_id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (perfil_id)  REFERENCES perfis(id)   ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE logs_acesso (
    id          BIGINT       PRIMARY KEY AUTO_INCREMENT,
    usuario_id  BIGINT,
    acao        VARCHAR(200) NOT NULL,
    entidade    VARCHAR(100),
    entidade_id BIGINT,
    ip          VARCHAR(45),
    user_agent  TEXT,
    sucesso     BOOLEAN      DEFAULT TRUE,
    detalhes    JSON,
    created_at  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    INDEX idx_usuario_data (usuario_id, created_at),
    INDEX idx_entidade     (entidade, entidade_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- MÓDULO 5: FINANCEIRO
-- =============================================================================

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
    ano_vigencia              INT           NOT NULL,
    created_at                DATETIME      DEFAULT CURRENT_TIMESTAMP,
    updated_at                DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_ano_ativo (ano_vigencia, ativo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE contratos (
    id                   BIGINT        PRIMARY KEY AUTO_INCREMENT,
    aluno_id             BIGINT        NOT NULL,
    responsavel_id       BIGINT        NOT NULL,
    plano_id             BIGINT        NOT NULL,
    numero_contrato      VARCHAR(30)   UNIQUE NOT NULL,
    ano_letivo           INT           NOT NULL,
    valor_base           DECIMAL(10,2) NOT NULL,
    desconto_total       DECIMAL(10,2) DEFAULT 0,
    valor_final          DECIMAL(10,2) NOT NULL,
    data_assinatura      DATE,
    data_vencimento      DATE,
    data_inicio_vigencia DATE          NOT NULL,
    data_fim_vigencia    DATE          NOT NULL,
    situacao             ENUM('PENDENTE','ATIVO','SUSPENSO','CANCELADO','ENCERRADO') DEFAULT 'PENDENTE',
    forma_pagamento      ENUM('BOLETO','PIX','CARTAO_CREDITO','DEBITO_AUTOMATICO')   DEFAULT 'BOLETO',
    -- campos Contrato.java adicionais
    valor_mensalidade    DECIMAL(10,2),
    desconto             DECIMAL(10,2),
    valor_matricula      DECIMAL(10,2),
    total_parcelas       INT,
    dia_vencimento       INT,
    data_inicio          DATE,
    data_fim             DATE,
    termos_especiais     JSON,
    observacoes          TEXT,
    created_at           DATETIME      DEFAULT CURRENT_TIMESTAMP,
    updated_at           DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (aluno_id)       REFERENCES alunos(id),
    FOREIGN KEY (responsavel_id) REFERENCES responsaveis(id),
    FOREIGN KEY (plano_id)       REFERENCES planos_mensalidade(id),
    INDEX idx_contrato_aluno    (aluno_id),
    INDEX idx_contrato_situacao (situacao, ano_letivo),
    INDEX idx_contrato_numero   (numero_contrato)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE mensalidades (
    id                     BIGINT        PRIMARY KEY AUTO_INCREMENT,
    contrato_id            BIGINT        NOT NULL,
    numero_parcela         INT           NOT NULL,
    mes_referencia         INT           NOT NULL,
    ano_referencia         INT           NOT NULL,
    valor_parcela          DECIMAL(10,2) NOT NULL,
    valor_desconto         DECIMAL(10,2) DEFAULT 0,
    valor_juros            DECIMAL(10,2) DEFAULT 0,
    valor_multa            DECIMAL(10,2) DEFAULT 0,
    valor_pago             DECIMAL(10,2),
    data_vencimento        DATE          NOT NULL,
    data_pagamento         DATETIME,
    status                 ENUM('ABERTA','PAGA','ATRASADA','CANCELADA','NEGOCIADA') DEFAULT 'ABERTA',
    nosso_numero           VARCHAR(50),
    codigo_barras          VARCHAR(100),
    pix_qr_code            TEXT,
    gateway_transaction_id VARCHAR(100),
    created_at             DATETIME      DEFAULT CURRENT_TIMESTAMP,
    updated_at             DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (contrato_id) REFERENCES contratos(id),
    INDEX idx_mensalidade_vencimento (data_vencimento, status),
    INDEX idx_mensalidade_mes_ano    (ano_referencia, mes_referencia)
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
    created_at      DATETIME      DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (mensalidade_id) REFERENCES mensalidades(id),
    FOREIGN KEY (registrado_por) REFERENCES usuarios(id),
    INDEX idx_pagamento_data    (data_pagamento),
    INDEX idx_pagamento_gateway (gateway_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- MÓDULO 6: COMUNIDADE E COMUNICAÇÃO
-- =============================================================================

CREATE TABLE canais_comunicacao (
    id                 BIGINT       PRIMARY KEY AUTO_INCREMENT,
    nome               VARCHAR(100) NOT NULL,
    tipo               ENUM('TURMA','COMISSAO','FESTIVAL','GERAL','DIRETORIA','PAIS','PROFESSORES') NOT NULL,
    descricao          TEXT,
    regras_engajamento TEXT,
    publico            BOOLEAN  DEFAULT TRUE,
    moderado           BOOLEAN  DEFAULT TRUE,
    turma_id           BIGINT,
    ativo              BOOLEAN  DEFAULT TRUE,
    cor_canal          CHAR(7)  DEFAULT '#9C27B0',
    created_at         DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at         DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (turma_id) REFERENCES turmas(id),
    INDEX idx_canal_tipo_ativo (tipo, ativo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE mensagens_canal (
    id          BIGINT   PRIMARY KEY AUTO_INCREMENT,
    canal_id    BIGINT   NOT NULL,
    autor_id    BIGINT   NOT NULL,
    conteudo    TEXT     NOT NULL,
    tipo        ENUM('TEXTO','AVISO','ANUNCIO','ENQUETE') DEFAULT 'TEXTO',
    prioridade  ENUM('BAIXA','NORMAL','ALTA','URGENTE')   DEFAULT 'NORMAL',
    fixada      BOOLEAN  DEFAULT FALSE,
    editada     BOOLEAN  DEFAULT FALSE,
    data_edicao DATETIME,
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (canal_id) REFERENCES canais_comunicacao(id),
    FOREIGN KEY (autor_id) REFERENCES usuarios(id)
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
    aberto_comunidade    BOOLEAN  DEFAULT TRUE,
    status               ENUM('PLANEJADO','CONFIRMADO','EM_ANDAMENTO','CONCLUIDO','CANCELADO') DEFAULT 'PLANEJADO',
    created_at           DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at           DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (responsavel_id) REFERENCES pessoas(id)
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
    permite_criancas      BOOLEAN  DEFAULT TRUE,
    status                ENUM('PLANEJADO','CONFIRMADO','EM_ANDAMENTO','CONCLUIDO','CANCELADO') DEFAULT 'PLANEJADO',
    created_at            DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at            DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE inscricoes_eventos (
    id                 BIGINT    PRIMARY KEY AUTO_INCREMENT,
    tipo_evento        ENUM('FESTIVAL','MUTIRAO') NOT NULL,
    evento_id          BIGINT    NOT NULL,
    pessoa_id          BIGINT    NOT NULL,
    numero_pessoas     INT       DEFAULT 1,
    criancas_incluidas INT       DEFAULT 0,
    materiais_trazidos TEXT,
    confirmado         BOOLEAN   DEFAULT FALSE,
    created_at         DATETIME  DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (pessoa_id) REFERENCES pessoas(id),
    UNIQUE KEY uk_evento_pessoa (tipo_evento, evento_id, pessoa_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- MÓDULO 7: NOTIFICAÇÕES
-- =============================================================================

CREATE TABLE preferencias_notificacao (
    id              BIGINT   PRIMARY KEY AUTO_INCREMENT,
    usuario_id      BIGINT   NOT NULL,
    categoria       ENUM('PEDAGOGICO','ADMINISTRATIVO','FINANCEIRO','COMUNIDADE','EMERGENCIA','SISTEMA') NOT NULL,
    canal_email     BOOLEAN  DEFAULT TRUE,
    canal_push      BOOLEAN  DEFAULT TRUE,
    canal_sms       BOOLEAN  DEFAULT FALSE,
    agregacao       ENUM('IMEDIATO','RESUMO_DIARIO','RESUMO_SEMANAL') DEFAULT 'IMEDIATO',
    horario_resumo  TIME     DEFAULT '18:00:00',
    silencio_inicio TIME     DEFAULT '20:00:00',
    silencio_fim    TIME     DEFAULT '07:00:00',
    ativo           BOOLEAN  DEFAULT TRUE,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    UNIQUE KEY uk_usuario_categoria (usuario_id, categoria)
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
    created_at                DATETIME     DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- MÓDULO 8: LGPD
-- =============================================================================

CREATE TABLE consentimentos_lgpd (
    id                 BIGINT       PRIMARY KEY AUTO_INCREMENT,
    pessoa_id          BIGINT       NOT NULL,
    finalidade         VARCHAR(200) NOT NULL,
    descricao          TEXT,
    consentido         BOOLEAN      NOT NULL,
    data_consentimento DATETIME     NOT NULL,
    data_revogacao     DATETIME,
    ip_consentimento   VARCHAR(45),
    versao_termos      VARCHAR(20),
    coletado_por       BIGINT,
    created_at         DATETIME     DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (pessoa_id)    REFERENCES pessoas(id)   ON DELETE CASCADE,
    FOREIGN KEY (coletado_por) REFERENCES usuarios(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE solicitacoes_titulares (
    id                     BIGINT       PRIMARY KEY AUTO_INCREMENT,
    pessoa_id              BIGINT       NOT NULL,
    tipo_solicitacao       ENUM('ACESSO','CORRECAO','EXCLUSAO','PORTABILIDADE','REVOGACAO','INFORMACAO') NOT NULL,
    descricao              TEXT         NOT NULL,
    status                 ENUM('ABERTA','EM_ANALISE','EM_ATENDIMENTO','CONCLUIDA','REJEITADA') DEFAULT 'ABERTA',
    data_solicitacao       DATETIME     DEFAULT CURRENT_TIMESTAMP,
    prazo_resposta         DATE         NOT NULL,
    data_conclusao         DATETIME,
    resposta               TEXT,
    atendido_por           BIGINT,
    justificativa_rejeicao TEXT,
    created_at             DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at             DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (pessoa_id)    REFERENCES pessoas(id),
    FOREIGN KEY (atendido_por) REFERENCES usuarios(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- DADOS INICIAIS
-- =============================================================================

INSERT INTO perfis (nome) VALUES
    ('ROLE_ADMIN'),
    ('ROLE_DIRETOR'),
    ('ROLE_SECRETARIA'),
    ('ROLE_PROFESSOR'),
    ('ROLE_RESPONSAVEL'),
    ('ROLE_FINANCEIRO');

-- Usuario admin inicial (senha: Admin@2026 - bcrypt)
INSERT INTO pessoas (tipo, nome_completo, email, ativo) VALUES
    ('FUNCIONARIO', 'Administrador do Sistema', 'admin@waldorf.edu.br', TRUE);

INSERT INTO usuarios (pessoa_id, login, senha_hash, ativo)
SELECT id, 'admin', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQyCjAfozC0Q.JcwJ7C4oBFO2', TRUE
FROM pessoas WHERE email = 'admin@waldorf.edu.br';

INSERT INTO usuarios_perfis (usuario_id, perfil_id)
SELECT u.id, p.id FROM usuarios u, perfis p
WHERE u.login = 'admin' AND p.nome = 'ROLE_ADMIN';

-- =============================================================================
-- VIEWS
-- =============================================================================

CREATE OR REPLACE VIEW vw_dashboard_secretaria AS
SELECT
    (SELECT COUNT(*) FROM alunos WHERE ativo = TRUE)                                       AS total_alunos_ativos,
    (SELECT COUNT(*) FROM matriculas WHERE situacao = 'EM_ANDAMENTO'
        AND ano_letivo = YEAR(CURDATE()))                                                   AS matriculas_ativas,
    (SELECT COUNT(*) FROM contratos WHERE situacao = 'PENDENTE')                           AS contratos_pendentes,
    (SELECT COUNT(*) FROM mensalidades WHERE status = 'ATRASADA')                          AS mensalidades_atrasadas,
    (SELECT COUNT(*) FROM solicitacoes_titulares WHERE status IN ('ABERTA','EM_ANALISE'))  AS lgpd_pendentes;

CREATE OR REPLACE VIEW vw_financeiro_mensal AS
SELECT
    m.ano_referencia,
    m.mes_referencia,
    COUNT(*)                                                                          AS total_mensalidades,
    SUM(m.valor_parcela)                                                              AS valor_total_esperado,
    SUM(CASE WHEN m.status = 'PAGA'     THEN m.valor_pago    ELSE 0 END)             AS valor_total_recebido,
    SUM(CASE WHEN m.status = 'ATRASADA' THEN m.valor_parcela ELSE 0 END)             AS valor_inadimplente,
    ROUND(SUM(CASE WHEN m.status = 'ATRASADA' THEN 1 ELSE 0 END)
        / COUNT(*) * 100, 2)                                                          AS percentual_inadimplencia
FROM mensalidades m
GROUP BY m.ano_referencia, m.mes_referencia
ORDER BY m.ano_referencia DESC, m.mes_referencia DESC;

-- =============================================================================
-- TRIGGERS
-- =============================================================================

DROP TRIGGER IF EXISTS trg_atualizar_vagas_after_matricula;
CREATE TRIGGER trg_atualizar_vagas_after_matricula
AFTER INSERT ON matriculas
FOR EACH ROW
BEGIN
    UPDATE turmas
    SET vagas_disponiveis = capacidade_maxima - (
        SELECT COUNT(*) FROM matriculas
        WHERE turma_id = NEW.turma_id
          AND situacao IN ('ATIVA','EM_ANDAMENTO')
    )
    WHERE id = NEW.turma_id;
END;
