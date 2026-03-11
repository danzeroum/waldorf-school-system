# 🌿 Waldorf School System

Sistema de gestão escolar para **Escolas Waldorf** — cobrindo pedagogia, financeiro, comunidade e conformidade LGPD.

[![CI](https://github.com/danzeroum/waldorf-school-system/actions/workflows/ci.yml/badge.svg)](https://github.com/danzeroum/waldorf-school-system/actions/workflows/ci.yml)
[![Docker](https://github.com/danzeroum/waldorf-school-system/actions/workflows/docker.yml/badge.svg)](https://github.com/danzeroum/waldorf-school-system/actions/workflows/docker.yml)

---

## 📌 Visão Geral

| Camada | Tecnologia | Status |
|--------|-----------|--------|
| Backend | Spring Boot 3.x • Java 21 • MySQL 8 | ✅ ~75% |
| Frontend Web | Angular 17+ • Tailwind CSS | ✅ ~95% |
| Mobile | Flutter 3.x (iOS/Android) | ✅ ~60% |
| Infra | Docker • GitHub Actions • Nginx | ✅ 100% |

---

## 🏗️ Arquitetura

```
┌─────────────────────────────────────────────────────────────────┐
│  Clientes                                                        │
│  ┌───────────────┐   ┌─────────────────┐   ┌────────────┐  │
│  │ Angular 17  │   │ Flutter 3 (iOS/  │   │ Swagger UI │  │
│  │ (Web App)   │   │ Android)        │   │ /swagger-ui│  │
│  └─────┬─────┘   └──────┬──────┘   └─────┬────┘  │
└───────────────┴──────────────┬────────────┴────────────┘
                              │ HTTPS / REST
               ┌──────────────┴─────────────┐
               │   Nginx (reverse proxy)  │
               └────────────┬────────────┘
                            │
               ┌────────────┴────────────┐
               │  Spring Boot 3 API       │
               │  JWT • RBAC • OpenAPI    │
               └─────┬─────┬─────┬─────┘
                    │         │         │
              MySQL 8   Redis   RabbitMQ
```

---

## 🚀 Setup Rápido

### Pré-requisitos
- Docker 24+ e Docker Compose v2
- Java 21 (para desenvolvimento local)
- Node 20+ (para desenvolvimento frontend)
- Flutter 3.3+ (para desenvolvimento mobile)

### 1. Clonar e configurar variáveis

```bash
git clone https://github.com/danzeroum/waldorf-school-system.git
cd waldorf-school-system
cp .env.example .env
# Edite .env com suas configurações
```

### 2. Subir a infraestrutura completa

```bash
docker-compose up -d
```

Será iniciado:
| Serviço | Porta | URL |
|---------|-------|-----|
| Backend API | 8080 | http://localhost:8080 |
| Frontend Web | 4200 | http://localhost:4200 |
| Swagger UI | 8080 | http://localhost:8080/swagger-ui.html |
| MySQL | 3306 | localhost:3306 |
| Redis | 6379 | localhost:6379 |
| RabbitMQ UI | 15672 | http://localhost:15672 |
| MinIO | 9000 | http://localhost:9000 |

### 3. Rodar migrations Flyway

```bash
cd backend
./mvnw flyway:migrate
```

### 4. Credenciais padrão (seed)

| Usuário | E-mail | Senha | Perfil |
|---------|--------|-------|--------|
| Administrador | admin@waldorf.edu.br | admin123 | ADMIN |
| Secretaria | secretaria@waldorf.edu.br | waldorf123 | SECRETARIA |
| Diretor | diretor@waldorf.edu.br | waldorf123 | DIRETOR |

---

## 🔧 Desenvolvimento Local

### Backend

```bash
cd backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Frontend Web

```bash
cd frontend-web
npm install
npm start          # http://localhost:4200
```

### Mobile Flutter

```bash
cd frontend-mobile
flutter pub get
flutter run --dart-define=API_URL=http://10.0.2.2:8080/api/v1
```

### Testes

```bash
# Backend (unitários + integração)
cd backend && ./mvnw test

# Frontend (lint + build)
cd frontend-web && npm run lint && npm run build
```

---

## 📚 Endpoints Principais

### Autenticação
```
POST /api/v1/auth/login          → { accessToken, refreshToken }
POST /api/v1/auth/refresh         → { accessToken, refreshToken }
POST /api/v1/auth/logout
```

### Pessoas
```
GET/POST   /api/v1/alunos
GET/PUT    /api/v1/alunos/{id}
DELETE     /api/v1/alunos/{id}         (soft delete)
GET/POST   /api/v1/responsaveis
GET/PUT    /api/v1/responsaveis/{id}
```

### Pedagogia
```
GET/POST   /api/v1/turmas
GET        /api/v1/turmas/{id}/alunos
GET/POST   /api/v1/epocas
POST       /api/v1/epocas/{id}/encerrar
GET/POST   /api/v1/observacoes
GET        /api/v1/observacoes/aluno/{id}
```

### Financeiro
```
GET/POST   /api/v1/finance/contracts
GET        /api/v1/finance/invoices
POST       /api/v1/finance/webhooks/payment
```

### Comunidade
```
GET/POST   /api/v1/community/channels
GET/POST   /api/v1/community/messages
GET/POST   /api/v1/community/events
```

### LGPD
```
GET/POST   /api/v1/lgpd/consents
PATCH      /api/v1/lgpd/consents/{id}/revoke
GET/POST   /api/v1/lgpd/requests
PATCH      /api/v1/lgpd/requests/{id}/advance
PATCH      /api/v1/lgpd/requests/{id}/conclude
PATCH      /api/v1/lgpd/requests/{id}/reject
```

### Notificações
```
GET        /api/v1/notifications/user/{id}
GET        /api/v1/notifications/user/{id}/unread-count
PATCH      /api/v1/notifications/{id}/read
GET/PUT    /api/v1/notifications/preferences
```

> 📖 Documentação interativa completa: **http://localhost:8080/swagger-ui.html**

---

## 🗂️ Módulos do Sistema

| Módulo | Funcionalidades |
|--------|----------------|
| **Auth** | Login JWT, RBAC por perfil, refresh token, logout |
| **Pessoas** | CRUD Aluno + Responsável, vínculo, soft delete |
| **Pedagogia** | Turmas, Épocas Pedagógicas, Observações por aluno |
| **Financeiro** | Contratos, parcelas mensais, baixa de pagamento |
| **Comunidade** | Mural de avisos, comunicados, portal dos pais |
| **Notificações** | In-app, e-mail, push (FCM), janela de silêncio |
| **LGPD** | Consentimentos, solicitações de titulares (Art. 18), relatório |

---

## 🔐 Perfis de Acesso (RBAC)

| Perfil | Acesso |
|--------|--------|
| `ADMIN` | Total |
| `DIRETOR` | Todos exceto configurações do sistema |
| `SECRETARIA` | Pessoas, financeiro, comunidade |
| `PROFESSOR` | Pedagogia, observações, turmas |
| `PAIS` | Filhos, financeiro (leitura), comunicados |

---

## 🧪 Testes

```
backend/src/test/
├── application/service/
│   ├── AuthServiceTest.java
│   ├── AlunoServiceTest.java
│   ├── LgpdServiceTest.java
│   └── FinanceiroServiceTest.java
└── presentation/controller/
    ├── AlunoControllerIT.java
    └── FinanceiroControllerIT.java
```

---

## 📄 Licença

MIT License — veja [LICENSE](LICENSE)
