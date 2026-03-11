# 📋 ATUALIZAÇÕES DO SISTEMA ESCOLAR WALDORF

> Documento de rastreamento de iterações e status de implementação.
> Atualizado em: 11/03/2026

---

## 🗂️ ARQUIVOS DE PLANO DISPONÍVEIS

| Arquivo | Conteúdo |
|---------|----------|
| `planoArquitetura.md` | Arquitetura geral, tecnologias, infraestrutura |
| `planoBancoDadosRelacionais.md` | Modelo relacional completo (MySQL) |
| `planoAPIs.md` | Endpoints REST, DTOs, contratos de API |
| `planoFrontend.md` | Angular, estrutura de módulos, design system |
| `planoWireframe.md` | Wireframes, fluxos por persona, componentes |
| `matrizRastreabilidade.md` | Mapeamento completo tabela→entity→API→componente |

---

## ✅ HISTÓRICO DE ITERAÇÕES

### Iteração 1 — Setup inicial do projeto
- Estrutura de diretórios do monorepo (backend, frontend-web, frontend-mobile, infra, docs)
- `docker-compose.yml` com MySQL 8, Redis, RabbitMQ, MinIO
- `pom.xml` Spring Boot 3.x com dependências base
- README inicial

### Iteração 2 — Migration V1: Módulo Pessoas
- `V1__create_pessoas.sql`: tabelas `pessoas`, `enderecos`, `alunos`, `responsaveis`, `professores`, `responsaveis_alunos`, `registro_tratamento_dados`
- Hierarquia de herança TABLE_PER_CLASS

### Iteração 3 — Migration V2 + V3: Estrutura Escolar e Pedagogia
- `V2__create_estrutura_escolar.sql`: `cursos`, `turmas`, `matriculas`, `disciplinas`, `turma_disciplinas`
- `V3__create_pedagogia_waldorf.sql`: `desenvolvimento_waldorf`, `epocas_pedagogicas`, `ritmo_diario_semanal`, `observacoes_desenvolvimento`

### Iteração 4 — Migration V4 + V5: Segurança e Dados Seed
- `V4__create_seguranca_usuarios.sql`: `usuarios`, `perfis`, `permissoes`, `usuarios_perfis`, `perfis_permissoes`, `usuario_contextos`, `refresh_tokens`, `logs_sistema`
- `V5__insert_data_inicial.sql`: perfis padrão (ADMIN, SECRETARIA, PROFESSOR, PAIS), permissões base, cursos Waldorf, admin inicial

### Iteração 5 — Migration V6: Audit Columns
- `V6__add_audit_columns.sql`: adiciona `created_at`/`updated_at` em tabelas que ainda não tinham auditoria

### Iteração 6 — Entity `Pessoa` + DDD base
- `Pessoa.java` com herança, `PessoaRepository`, `PessoaService`
- `PessoaRequestDTO`, `PessoaResponseDTO`, `PessoaMapper`
- `PessoaController` com endpoints `GET/POST /api/v1/pessoas`
- Configuração do pacote DDD: `domain/`, `application/`, `infrastructure/`, `presentation/`

### Iteração 7 — Segurança JWT + RBAC
- `Usuario.java`, `Perfil.java`, `Permissao.java` (entities)
- `AuthController` com `POST /api/v1/auth/login`
- `JwtService`, `JwtAuthenticationFilter`
- `SecurityConfig` (Spring Security 6 + JWT stateless)
- `LoginRequestDTO`, `LoginResponseDTO`

### Iteração 8 — Entity `ObservacaoDesenvolvimento`
- `ObservacaoDesenvolvimento.java` (entity JPA completa)
- ENUMs: `AspectoDesenvolvimento`, `Temperamento`
- Relacionamentos com `Aluno`, `Professor`, `Turma`, `EpocaPedagogica`

### Iteração 9 — Plano de Wireframe
- Criado `docs/planoWireframe.md` com wireframes completos por tela e fluxos por persona

### Iteração 10 — Migration V7: Tabelas Faltantes
- `V7__create_tabelas_faltantes.sql` com 15+ tabelas, views, triggers e events MySQL

### Iteração 11 — Módulo Financeiro (Backend)
- Entities: `Contrato`, `PlanoMensalidade`, `Mensalidade`, `Pagamento`
- `ContratoService`, `MensalidadeService`, `PagamentoService`
- `FinanceiroController` com endpoints `/api/v1/finance`

### Iteração 12 — Módulo Comunidade + Comunicação (Backend)
- Entities: `CanalComunicacao`, `MensagemCanal`, `FestivalComunitario`, `Mutirao`, `InscricaoEvento`
- `ComunidadeController` com endpoints `/api/v1/community`
- WebSocket configurado para chat em tempo real

### Iteração 13 — Matriz de Rastreabilidade
- `docs/matrizRastreabilidade.md` com 30+ tabelas rastreadas e roadmap de 6 sprints

### Iteração 14 — Módulo Notificações (Backend)
- `PreferenciaNotificacao.java`, `LogEnvioNotificacao.java`
- `NotificacaoService` com supressoão por janela de silêncio e `@Scheduled`
- `NotificacaoController` com endpoints `/api/v1/notifications`

### Iteração 15 — Módulo LGPD e Compliance (Backend)
- `ConsentimentoLgpd.java`, `SolicitacaoTitular.java`
- `LgpdService` com prazo automático de 15 dias e fluxo de estados
- `LgpdController` com endpoints `/api/v1/lgpd`

### Iteração 16 — Atualização de atualizacoes.md (iterações 1–15)

### Iteração 17 — Frontend Angular: Setup + Auth + Layout
- Setup Angular 17 com lazy loading
- `AuthModule`: login, guards `AuthGuard`/`RoleGuard`, interceptor JWT
- `LayoutModule`: sidebar responsiva, header com notificações badge, overlay mobile
- `SharedModule`: design system Waldorf (tokens Tailwind, classes utilititárias)

### Iteração 18 — Frontend Angular: Dashboard (3 perfis)
- `DashboardModule` com 3 componentes: Secretaria, Professor, Pais
- Cards de métricas, atividades recentes, atalhos rápidos
- Roteamento condicional por perfil

### Iteração 19 — Frontend Angular: Módulo Pedagogía
- `PedagogiaModule`: Turmas, Épocas, Observações Pedagógicas
- `PedagogiaService`, `EpocaService`, `ObservacaoService`
- Pipes: `AspectoPipe`, `DuracaoEpocaPipe`

### Iteração 20 — Frontend Angular: Módulo Financeiro
- `FinanceiroModule`: Dashboard, Contratos, Parcelas
- Preview de parcelas em tempo real via `computed()`
- Modal de baixa de pagamento
- Pipes: `StatusContratoPipe`, `StatusParcelaPipe`

### Iteração 21 — Frontend Angular: Comunidade + LGPD
- `ComunidadeModule`: Mural, Comunicados, Portal dos Pais
- `LgpdModule`: Consentimentos, Solicitações, Relatório LGPD
- `AvisoService`, `ComunicadoService`, `LgpdService`
- Pipes: `TipoAvisoPipe`, `StatusConsentimentoPipe`

### Iteração 22 — Frontend Angular: Integração final
- `AppModule` com registro de locale `pt-BR`, interceptors registrados
- `AppRoutingModule`: roteamento lazy de todos os módulos com `AuthGuard` + `RoleGuard` por perfil
- `ErrorInterceptor`: trata 401 (logout) e 403 (redireciona dashboard)
- Models TypeScript centralizados em `src/app/@models/`:
  - `auth.models.ts`: `TokenPayload`, `LoginRequest`, `LoginResponse`, `UsuarioLogado`
  - `pessoa.models.ts`: `Pessoa`, `Aluno`, `Responsavel`, `Professor`
  - `pedagogia.models.ts`: `Turma`, `EpocaPedagogica`, `ObservacaoPedagogica`
  - `financeiro.models.ts`: `Contrato`, `Parcela` com tipos de status
- `tsconfig.json` com path aliases: `@models`, `@environments`, `@core`, `@shared`

---

## 📊 STATUS ATUAL DO PROJETO (Iteração 22)

### Completude por camada

| Módulo | Banco (MySQL) | Backend (Java) | API REST | Frontend (Angular) | Mobile (Flutter) |
|--------|:---:|:---:|:---:|:---:|:---:|
| Pessoas | ✅ 100% | 30% | 25% | ✅ 90% | 0% |
| Estrutura Escolar | ✅ 100% | 5% | 10% | ✅ 85% | 0% |
| Pedagogia Waldorf | ✅ 100% | 25% | 30% | ✅ 90% | 0% |
| Segurança / Auth | ✅ 100% | 65% | 40% | ✅ 95% | 0% |
| Financeiro | ✅ 100% | 50% | 45% | ✅ 90% | 0% |
| Comunidade | ✅ 100% | 40% | 35% | ✅ 85% | 0% |
| Notificações | ✅ 100% | 60% | 50% | 20% | 0% |
| LGPD | ✅ 100% | 55% | 50% | ✅ 90% | 0% |
| **MÉDIA** | **✅ 100%** | **~41%** | **~36%** | **✅ ~78%** | **0%** |

### Migrations aplicadas

| Versão | Arquivo | Status |
|--------|---------|--------|
| V1 | `V1__create_pessoas.sql` | ✅ Aplicada |
| V2 | `V2__create_estrutura_escolar.sql` | ✅ Aplicada |
| V3 | `V3__create_pedagogia_waldorf.sql` | ✅ Aplicada |
| V4 | `V4__create_seguranca_usuarios.sql` | ✅ Aplicada |
| V5 | `V5__insert_data_inicial.sql` | ✅ Aplicada |
| V6 | `V6__add_audit_columns.sql` | ✅ Aplicada |
| V7 | `V7__create_tabelas_faltantes.sql` | 📋 Criada — executar `./mvnw flyway:migrate` |

---

## 🔜 PRÓXIMAS ITERAÇÕES PLANEJADAS

### Iteração 23 — Frontend: Módulo Notificações + Ajustes
- `NotificacoesModule`: painel de notificações, marcar como lida, preferências
- Polimento geral de UX: estados vazios, toasts de sucesso/erro, loading global

### Iteração 24 — Backend: Endpoints faltantes
- Completar endpoints de Pessoas (`/alunos`, `/responsaveis`, `/professores`)
- Completar endpoints de Pedagogia (`/turmas`, `/epocas`, `/observacoes`)
- Swagger/OpenAPI atualizado

### Iteração 25 — Mobile Flutter: Setup + Auth + Dashboard Pais
- Autenticação + push notifications (FCM)
- Dashboard de pais com filhos
- Financeiro: ver e pagar mensalidades
- Offline-first com SQLite

### Iteração 26 — Testes + CI/CD
- Testes unitários (JUnit 5 + Mockito)
- Testes de integração (Testcontainers)
- Pipeline GitHub Actions (build + test + docker push)
- Deploy em staging (Docker + Kubernetes)

---

## 🗂️ ESTRUTURA DO REPOSITÓRIO

```
waldorf-school-system/
├── backend/                          # Spring Boot 3.x (Java 21)
│   └── src/main/
│       ├── java/com/waldorf/
│       │   ├── domain/               # Entities, Value Objects, Domain Services
│       │   ├── application/          # Use Cases, DTOs, Mappers
│       │   ├── infrastructure/       # Repositories, Configs, Integrations
│       │   └── presentation/         # Controllers, Exception Handlers
│       └── resources/
│           └── db/migration/         # Flyway V1-V7
├── frontend-web/                     # Angular 17+
│   └── src/app/
│       ├── @models/              # Interfaces/tipos TypeScript centralizados
│       ├── core/                     # Auth, Guards, Interceptors
│       ├── shared/                   # Design System Waldorf
│       ├── layout/                   # Shell, Sidebar, Header
│       └── modules/                  # Features lazy-loaded
│           ├── auth/
│           ├── dashboard/
│           ├── pessoas/
│           ├── pedagogia/
│           ├── financeiro/
│           ├── comunidade/
│           ├── lgpd/
│           └── notificacoes/
├── frontend-mobile/                  # Flutter 3.x
│   └── lib/
├── infra/                            # Docker, Kubernetes, Nginx
├── docs/
│   ├── atualizacoes.md               # Este arquivo
│   ├── planoArquitetura.md
│   ├── planoBancoDadosRelacionais.md
│   ├── planoAPIs.md
│   ├── planoFrontend.md
│   ├── planoWireframe.md
│   └── matrizRastreabilidade.md
├── docker-compose.yml
├── .env.example
├── .gitignore
└── README.md
```

---

*Última atualização: Iteração 22 — 11/03/2026*
