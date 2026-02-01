# 📊 Database - Sistema Waldorf

## Estrutura

```
database/
├── migrations/          # Flyway migrations
│   ├── V1__create_pessoas.sql
│   ├── V2__create_alunos.sql
│   └── ...
├── seeds/               # Dados iniciais
│   ├── 01_usuarios.sql
│   └── 02_permissoes.sql
├── init/                # Scripts de inicialização
│   └── 01-init.sql
└── docs/                # Documentação do modelo
    └── schema-diagram.png
```

## Executar Migrations

```bash
# Local (com Docker)
docker-compose exec mysql mysql -u waldorf_user -p waldorf_db < migrations/V1__*.sql

# Usando Flyway (via backend)
cd backend
./mvnw flyway:migrate
```

## Backup e Restore

```bash
# Backup
docker-compose exec mysql mysqldump -u root -p waldorf_db > backup.sql

# Restore
docker-compose exec -T mysql mysql -u root -p waldorf_db < backup.sql
```