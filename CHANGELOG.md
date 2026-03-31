# Changelog

Todas as mudanças notáveis deste projeto serão documentadas neste arquivo.

Formato baseado em [Keep a Changelog](https://keepachangelog.com/pt-BR/1.0.0/),
e este projeto adere ao [Versionamento Semântico](https://semver.org/lang/pt-BR/).

---

## [0.1.0-rc1] — 2026-03-31

### ✅ Adicionado

#### Backend (Spring Boot 3 / Java 21) — ~75%
- Autenticação JWT com refresh token e logout
- RBAC completo: ADMIN, DIRETOR, SECRETARIA, PROFESSOR, PAIS
- CRUD de Alunos com matrícula automática e soft delete
- CRUD de Responsáveis com vínculo a alunos
- Módulo Pedagógico: Turmas, Épocas Pedagógicas, Observações por aluno
- Módulo Financeiro: Contratos, mensalidades com geração automática, desconto de irmãos
- Módulo Comunidade: Mural de avisos, comunicados, portal dos pais
- Módulo Notificações: in-app, e-mail, push (FCM), janela de silêncio
- Módulo LGPD: Consentimentos, solicitações Art. 18, relatório de conformidade
- Documentação OpenAPI / Swagger UI em `/swagger-ui.html`
- Migrações Flyway versionadas
- Auditoria JPA (`createdAt`, `updatedAt`) em todas as entidades
- Configuração isolada `JpaAuditingConfig` (compatível com `@WebMvcTest`)

#### Frontend Web (Angular 17+ / Tailwind CSS) — ~95%
- Design system Waldorf completo
- Autenticação com guarda de rotas JWT
- Dashboards por perfil de acesso
- Módulos: Alunos, Turmas, Financeiro, Comunidade, Notificações, LGPD

#### Infra — 100%
- Docker Compose completo: Backend + Frontend + MySQL 8 + Redis + RabbitMQ + MinIO + Nginx
- CI/CD com GitHub Actions: Backend (Java 21 + H2), Frontend (Node 20), Docker Build & Push
- Imagens publicadas no GitHub Container Registry (`ghcr.io`)

#### Mobile Flutter — ~60%
- Autenticação e navegação base
- Telas de alunos e responsáveis
- Push notifications (FCM)

### 🔧 Corrigido
- `ContratoServiceTest`: `UnnecessaryStubbingException` resolvido com `@MockitoSettings(LENIENT)`
- `AuthControllerTest`: `JPA metamodel must not be empty` resolvido movendo `@EnableJpaAuditing` para `JpaAuditingConfig`
- `AlunoServiceTest.criarAluno`: `NullPointerException` resolvido mockando `saveAndFlush` corretamente

### ⚠️ Conhecido / Pendente
- Backend ~25% de funcionalidades ainda em implementação
- Mobile Flutter ~40% de funcionalidades pendentes
- Cobertura de testes automatizados abaixo de 60%
- Branch `main` sem proteção configurada

---

## [Não lançado]

### Planejado para v0.2.0
- Completar módulos backend (relatórios, agenda, portfólio pedagógico)
- Cobertura de testes ≥ 60%
- App Mobile Flutter completo (v1.0)
- Notificações em tempo real via WebSocket
- Integração com gateway de pagamento (Asaas / Pagar.me)
