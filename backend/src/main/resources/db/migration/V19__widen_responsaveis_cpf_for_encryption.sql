-- Amplia a coluna cpf de responsaveis para acomodar o valor criptografado
-- (AES-256/GCM em base64). Migração não destrutiva — dados existentes em texto
-- puro permanecem legíveis (converter tolerante) e são re-cifrados na próxima gravação.
ALTER TABLE responsaveis MODIFY COLUMN cpf VARCHAR(255);
