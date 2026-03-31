# 🛠️ Backend - Sistema Waldorf

## Arquitetura

- **Framework**: Spring Boot 3.2.2
- **Java**: 21 LTS
- **Build**: Maven
- **Arquitetura**: DDD (Domain-Driven Design)
- **Banco**: MySQL 8.0+ com Flyway
- **Segurança**: Spring Security + JWT
- **Cache**: Redis
- **Fila**: RabbitMQ

## Estrutura de Pacotes

```
br.edu.waldorf/
├── WaldorfApplication.java    # Main
├── config/                     # Configurações
├── core/                       # Componentes centrais
│   ├── auth/                   # Autenticação/Autorização
│   ├── exception/              # Tratamento de erros
│   ├── audit/                  # Auditoria
│   └── file/                   # Upload de arquivos
├── modules/                    # Módulos de negócio
│   ├── pessoa/
│   │   ├── domain/             # Entidades, Repos
│   │   ├── application/        # DTOs, Services
│   │   └── presentation/       # Controllers
│   ├── pedagogia/
│   ├── gestao/
│   ├── financeiro/
│   └── lgpd/
└── shared/                     # Compartilhado
```

## Executar Local

### 1. Pré-requisitos

```bash
# Subir serviços Docker
cd ..
docker-compose up -d mysql redis rabbitmq minio
```

### 2. Build

```bash
./mvnw clean install
```

### 3. Rodar

```bash
./mvnw spring-boot:run
```

### 4. Acessar

- **API**: http://localhost:8080
- **Swagger**: http://localhost:8080/swagger-ui.html
- **Actuator**: http://localhost:8080/actuator/health

## Testes

```bash
# Unit tests
./mvnw test

# Coverage report
./mvnw jacoco:report
open target/site/jacoco/index.html

# Integration tests
./mvnw verify -P integration-tests
```

## Migrations (Flyway)

```bash
# Info
./mvnw flyway:info

# Migrate
./mvnw flyway:migrate

# Repair (se necessário)
./mvnw flyway:repair
```

## Credenciais Padrão

**ATENÇÃO**: Trocar em produção!

- **Username**: `admin`
- **Password**: `Admin@2024`

## Variáveis de Ambiente

```bash
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=waldorf_db
export DB_USER=waldorf_user
export DB_PASS=waldorf_pass_2024
export JWT_SECRET=your-secret-key-here
```
