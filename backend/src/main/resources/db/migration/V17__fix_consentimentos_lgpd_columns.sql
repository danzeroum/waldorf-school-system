DROP TABLE IF EXISTS consentimentos_lgpd;
CREATE TABLE consentimentos_lgpd (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    aluno_id        BIGINT NOT NULL,
    responsavel_id  BIGINT NOT NULL,
    tipo            VARCHAR(50) NOT NULL,
    status          VARCHAR(50) NOT NULL,
    versao_termos   VARCHAR(255) NOT NULL,
    data_aceite     DATE,
    data_revogacao  DATE,
    ip_aceite       VARCHAR(255),
    created_at      DATETIME(6),
    updated_at      DATETIME(6),
    FOREIGN KEY (aluno_id)       REFERENCES alunos(id),
    FOREIGN KEY (responsavel_id) REFERENCES responsaveis(id),
    INDEX idx_aluno (aluno_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
