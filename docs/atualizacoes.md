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
- Criado `docs/planoWireframe.md` com:
  - Tela 0: Login
  - Tela 1: Dashboard Secretária
  - Tela 2: Dashboard Professor
  - Tela 3: Dashboard Pais (Flutter)
  - Tela 4: Nova Observação (wizard 3 passos)
  - Tela 5: Gestão de Alunos
  - Tela 6: Cadastro de Aluno (wizard 4 passos)
  - Tela 7: Financeiro (Mobile)
  - Tela 8: Relatório Narrativo
  - Fluxos completos por persona (A, B, C, D)

### Iteração 10 — Migration V7: Tabelas Faltantes
- Criado `V7__create_tabelas_faltantes.sql` com:
  - `funcionarios`
  - `relatorios_narrativos`, `trabalhos_manuais`, `portfolio_artistico`
  - `planos_mensalidade`, `contratos`, `mensalidades`, `pagamentos`
  - `canais_comunicacao`, `mensagens_canal`, `festivais_comunitarios`, `mutiroes`, `inscricoes_eventos`
  - `preferencias_notificacao`, `logs_envio_notificacoes`
  - `consentimentos_lgpd`, `solicitacoes_titulares`
  - Views: `vw_dashboard_secretaria`, `vw_resumo_pedagogico_turma`, `vw_financeiro_mensal`
  - Triggers e Events MySQL

### Iteração 11 — Módulo Financeiro (Backend)
- Entities: `Contrato`, `PlanoMensalidade`, `Mensalidade`, `Pagamento`
- ENUMs: `SituacaoContrato`, `FormaPagamento`, `StatusMensalidade`, `StatusPagamento`
- `ContratoService`, `MensalidadeService`, `PagamentoService`
- `FinanceiroController` com endpoints `/api/v1/finance/contracts`, `/invoices`, `/webhooks/payment`
- DTOs: `ContratoRequestDTO`, `ContratoResponseDTO`, `MensalidadeResponseDTO`

### Iteração 12 — Módulo Comunidade + Comunicação (Backend)
- Entities: `CanalComunicacao`, `MensagemCanal`, `FestivalComunitario`, `Mutirao`, `InscricaoEvento`
- ENUMs: `TipoCanal`, `TipoMensagem`, `TipoFestival`, `StatusEvento`
- `ComunidadeService` com suporte a moderação e fixação de mensagens
- `ComunidadeController` com endpoints `/api/v1/community/channels`, `/events`, `/messages`
- WebSocket preparado para chat em tempo real (configuração `@EnableWebSocketMessageBroker`)

### Iteração 13 — Matriz de Rastreabilidade
- Criado `docs/matrizRastreabilidade.md` com:
  - Rastreamento de 30+ tabelas → entity → repo → service → controller → endpoint → Angular → Flutter
  - Status ✅/📋/❌ por camada
  - Resumo quantitativo por módulo (% de completude)
  - Gaps críticos identificados
  - Inconsistências de nomenclatura entre camadas
  - Roadmap de implementação por sprint (6 sprints × 2 semanas)

### Iteração 14 — Módulo Notificações (Backend)
- Entities:
  - `PreferenciaNotificacao.java`: canais (EMAIL/PUSH/SMS/IN_APP), agregação (IMEDIATO/RESUMO_DIARIO/RESUMO_SEMANAL), janela de silêncio noturno com suporte a cruzamento de meia-noite
  - `LogEnvioNotificacao.java`: 7 tipos de conteúdo, 6 status de envio, rastreio de tentativas e erro
- `NotificacaoService`:
  - Supressão automática por preferência e janela de silêncio
  - Agendamento de notificações para fora do silêncio
  - `@Scheduled` a cada 2 minutos para processar notificações pendentes
- `NotificacaoController` com endpoints `/api/v1/notifications`:
  - `GET /user/{id}` — histórico paginado
  - `GET /user/{id}/unread-count` — contagem de não lidas
  - `PATCH /{id}/read` — marcar como lida
  - `GET/PUT /preferences` — upsert de preferências

### Iteração 15 — Módulo LGPD e Compliance (Backend)
- Entities:
  - `ConsentimentoLgpd.java`: registro com IP, versão dos termos, revogação com data
  - `SolicitacaoTitular.java`: 6 tipos (ACESSO, CORREÇÃO, EXCLUSÃO, PORTABILIDADE, REVOGAÇÃO, INFORMAÇÃO), prazo automático de 15 dias, fluxo ABERTA→EM_ANÁLISE→EM_ATENDIMENTO→CONCLUÍDA/REJEITADA
- `LgpdService`:
  - Cálculo automático de prazo (15 dias corridos)
  - Fluxo de avanço de status com validações
  - `@Scheduled` diário às 08h para alertar solicitações com prazo expirado
- `LgpdController` com endpoints `/api/v1/lgpd`:
  - `GET/POST /consents` — listar/criar consentimentos
  - `PATCH /consents/{id}/revoke` — revogar consentimento
  - `GET /requests` — listar solicitações em aberto
  - `POST /requests` — criar solicitação (prazo 15 dias automático)
  - `PATCH /requests/{id}/advance` — avançar status
  - `PATCH /requests/{id}/conclude` — concluir com resposta
  - `PATCH /requests/{id}/reject` — rejeitar com justificativa

---

## 📊 STATUS ATUAL DO PROJETO (Iteração 15)

### Completude por camada

| Módulo | Banco (MySQL) | Backend (Java) | API REST | Frontend (Angular) | Mobile (Flutter) |
|--------|:---:|:---:|:---:|:---:|:---:|
| Pessoas | ✅ 100% | 30% | 25% | 5% | 0% |
| Estrutura Escolar | ✅ 100% | 5% | 10% | 0% | 0% |
| Pedagogia Waldorf | ✅ 100% | 25% | 30% | 5% | 0% |
| Segurança / Auth | ✅ 100% | 65% | 40% | 0% | 0% |
| Financeiro | ✅ 100% | 50% | 45% | 0% | 0% |
| Comunidade | ✅ 100% | 40% | 35% | 0% | 0% |
| Notificações | ✅ 100% | 60% | 50% | 0% | 0% |
| LGPD | ✅ 100% | 55% | 50% | 0% | 0% |
| **MÉDIA** | **✅ 100%** | **~41%** | **~36%** | **~1%** | **0%** |

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

### Iteração 16 ✅ (esta) — Atualização do atualizacoes.md
### Iteração 17 — Frontend Angular: Setup + Auth
- Setup Angular 17 com lazy loading e standalone components
- `AuthModule`: login, guards, interceptors JWT
- Layout principal (sidebar responsiva + header com notificações)
- `DashboardModule`: 3 variações por perfil (Secretária, Professor, Pais)

### Iteração 18 — Frontend Angular: Módulo Pessoas
- `PessoaModule`: lista, CRUD, busca
- `AlunoModule`: wizard 4 passos (cadastro completo)
- Integração com ViaCEP para busca de endereço

### Iteração 19 — Frontend Angular: Módulo Pedagogia
- `ObservacaoModule`: wizard 3 passos + lista + detalhe
- `EpocaModule`: planejamento e visualização
- `RelatorioModule`: editor narrativo + fluxo de aprovação

### Iteração 20 — Mobile Flutter: Módulo Pais
- Autenticação + push notifications (FCM)
- Dashboard de pais com filhos
- Observações (timeline + detalhe)
- Financeiro (ver e pagar mensalidades)
- Offline-first com SQLite

### Iteração 21 — Testes + Deploy
- Testes unitários (JUnit 5 + Mockito)
- Testes de integração (Testcontainers)
- Pipeline CI/CD (GitHub Actions)
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
│       ├── core/                     # Auth, Guards, Interceptors
│       ├── shared/                   # Design System Waldorf
│       └── modules/                  # Features lazy-loaded
├── frontend-mobile/                  # Flutter 3.x
│   └── lib/
│       ├── core/
│       ├── presentation/
│       └── data/
├── infra/                            # Docker, Kubernetes, Nginx
├── docs/                             # Planos e documentação
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

*Última atualização: Iteração 16 — 11/03/2026*
