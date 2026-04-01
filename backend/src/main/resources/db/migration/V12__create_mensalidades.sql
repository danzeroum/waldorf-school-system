-- V12__create_mensalidades.sql
-- Tabela de mensalidades (parcelas dos contratos)
-- Geradas automaticamente pelo MensalidadeService.gerarMensalidades() ao ativar contrato

CREATE TABLE IF NOT EXISTS mensalidades (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    contrato_id     BIGINT          NOT NULL,
    numero          INT             NOT NULL,
    descricao       VARCHAR(100)    NOT NULL,
    valor           DECIMAL(10,2)   NOT NULL,
    valor_pago      DECIMAL(10,2)   NULL,
    data_vencimento DATE            NOT NULL,
    data_pagamento  DATE            NULL,
    status          VARCHAR(20)     NOT NULL DEFAULT 'PENDENTE',
    forma_pagamento VARCHAR(50)     NULL,
    observacao      VARCHAR(500)    NULL,
    created_at      DATETIME        DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    CONSTRAINT fk_mens_contrato FOREIGN KEY (contrato_id) REFERENCES contratos(id) ON DELETE CASCADE
);

CREATE INDEX idx_mens_contrato   ON mensalidades (contrato_id);
CREATE INDEX idx_mens_status     ON mensalidades (status);
CREATE INDEX idx_mens_vencimento ON mensalidades (data_vencimento);
