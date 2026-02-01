# 🏫 Sistema Escolar Waldorf

## 📋 Visão Geral

Sistema completo de gestão escolar baseado nos princípios pedagógicos Waldorf, integrando:

- **Backend**: Spring Boot 3.x com DDD e RBAC contextual
- **Frontend Web**: Angular 17+ com Design System Waldorf
- **Mobile**: Flutter com offline-first
- **Banco de Dados**: MySQL 8.0+ com modelagem híbrida (administrativa + pedagógica)

## 🏗️ Arquitetura

```
waldorf-school-system/
├── backend/                 # Spring Boot API
├── frontend-web/            # Angular Portal
├── frontend-mobile/         # Flutter App
├── infrastructure/          # Docker, K8s, Terraform
├── database/                # Migrations, Seeds
└── docs/                    # Documentação
```

## 🚀 Quick Start

### Pré-requisitos

- Docker 24+
- Docker Compose 2.20+
- Java 17+ (para desenvolvimento backend)
- Node 20+ (para desenvolvimento frontend)
- Flutter 3.x (para desenvolvimento mobile)

### Subir ambiente completo

```bash
# Clone o repositório
git clone https://github.com/danzeroum/waldorf-school-system.git
cd waldorf-school-system

# Subir todos os serviços
docker-compose up -d

# Aplicar migrations do banco
cd database
./run-migrations.sh

# Acessar aplicações
# Backend API: http://localhost:8080
# Frontend Web: http://localhost:4200
# Swagger UI: http://localhost:8080/swagger-ui.html
```

## 📦 Serviços Docker

| Serviço | Porta | Descrição |
|---------|-------|------------|
| MySQL | 3306 | Banco de dados principal |
| Redis | 6379 | Cache e sessões |
| RabbitMQ | 5672, 15672 | Fila de mensagens |
| MinIO | 9000, 9001 | Object storage (S3 compatible) |
| Backend | 8080 | API REST Spring Boot |
| Frontend | 4200 | Portal Angular |

## 🧪 Testes

```bash
# Backend
cd backend
./mvnw test

# Frontend
cd frontend-web
npm run test
npm run e2e

# Mobile
cd frontend-mobile
flutter test
```

## 📚 Documentação

- [Plano de Banco de Dados](docs/database-design.md)
- [Arquitetura do Sistema](docs/architecture.md)
- [APIs REST](docs/api-documentation.md)
- [Design System](docs/design-system.md)
- [Guia de Desenvolvimento](docs/development-guide.md)

## 🔒 Segurança

- JWT com refresh tokens
- RBAC contextual
- LGPD compliance
- Criptografia de dados sensíveis
- Rate limiting
- CORS configurado

## 📈 Status do Projeto

- [x] Fase 0: Infraestrutura
- [ ] Fase 1: Core Backend + Banco
- [ ] Fase 2: Pedagogia Waldorf
- [ ] Fase 3: Frontend Web
- [ ] Fase 4: Financeiro + Notificações
- [ ] Fase 5: Mobile App
- [ ] Fase 6: LGPD + Compliance
- [ ] Fase 7: Otimização

## 👥 Contribuindo

Veja [CONTRIBUTING.md](CONTRIBUTING.md) para diretrizes de contribuição.

## 📄 Licença

MIT License - veja [LICENSE](LICENSE) para detalhes.

## 🙏 Agradecimentos

Baseado nos princípios pedagógicos de Rudolf Steiner e na Pedagogia Waldorf.