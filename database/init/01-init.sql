-- Script de inicialização do banco de dados
-- Executado automaticamente pelo Docker ao criar o container

CREATE DATABASE IF NOT EXISTS waldorf_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE waldorf_db;

-- Garantir que usuário tenha todas as permissões
GRANT ALL PRIVILEGES ON waldorf_db.* TO 'waldorf_user'@'%';
FLUSH PRIVILEGES;

-- Configurar variáveis globais
SET GLOBAL time_zone = '+00:00';
SET GLOBAL max_connections = 200;
SET GLOBAL innodb_buffer_pool_size = 268435456; -- 256MB

SELECT 'Database initialized successfully!' AS status;