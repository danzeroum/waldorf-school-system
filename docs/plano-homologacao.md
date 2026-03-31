# 📋 Plano de Homologação — Waldorf School System v0.1.0-rc1

**Versão:** 0.1.0-rc1  
**Data de início:** 2026-04-01  
**Escopo:** Frontend Web + Backend API (Mobile Flutter **fora** deste ciclo)  
**Ambiente:** `docker-compose.homologacao.yml`  

---

## 1. Configuração do Ambiente

```bash
# 1. Clonar repositório
git clone https://github.com/danzeroum/waldorf-school-system.git
cd waldorf-school-system

# 2. Subir ambiente de homologação
docker-compose -f docker-compose.homologacao.yml up -d

# 3. Aguardar ~30s e verificar saúde dos serviços
docker-compose -f docker-compose.homologacao.yml ps

# 4. Verificar logs do backend
docker logs waldorf-homolog-backend --tail 50
```

**URLs de acesso:**
| Serviço | URL |
|---------|-----|
| Sistema Web | http://localhost:8090 |
| API Backend | http://localhost:8081 |
| Swagger UI | http://localhost:8081/swagger-ui.html |
| RabbitMQ UI | http://localhost:15673 |
| MinIO Console | http://localhost:9003 |

---

## 2. Critérios de Aceitação Geral

- [ ] Todos os serviços Docker sobem sem erro
- [ ] Migrations Flyway aplicadas com sucesso
- [ ] Login funciona para os 5 perfis de acesso
- [ ] Swagger UI acessível e operacional
- [ ] CI/CD (GitHub Actions) verde: Backend ✅ · Frontend ✅ · Docker ✅

---

## 3. Casos de Teste por Módulo

### 3.1 Autenticação (Auth)

| ID | Cenário | Passos | Resultado Esperado | Status |
|----|---------|--------|--------------------|--------|
| AUTH-01 | Login válido | POST `/api/v1/auth/login` com admin@waldorf.edu.br / admin123 | HTTP 200, `accessToken` e `refreshToken` retornados | ⬜ |
| AUTH-02 | Login inválido | POST com senha errada | HTTP 401, mensagem de erro | ⬜ |
| AUTH-03 | Refresh token | POST `/api/v1/auth/refresh` com token válido | HTTP 200, novos tokens | ⬜ |
| AUTH-04 | Refresh token expirado | POST com token inválido | HTTP 401 | ⬜ |
| AUTH-05 | Acesso sem token | GET `/api/v1/alunos` sem Authorization header | HTTP 401 | ⬜ |
| AUTH-06 | RBAC — PROFESSOR acessa financeiro | GET `/api/v1/finance/contracts` com token PROFESSOR | HTTP 403 | ⬜ |

### 3.2 Pessoas — Alunos

| ID | Cenário | Passos | Resultado Esperado | Status |
|----|---------|--------|--------------------|--------|
| ALU-01 | Criar aluno | POST `/api/v1/alunos` com dados válidos | HTTP 201, `id` e `matricula` gerados automaticamente | ⬜ |
| ALU-02 | Buscar aluno | GET `/api/v1/alunos/{id}` | HTTP 200, dados corretos | ⬜ |
| ALU-03 | Listar com filtro | GET `/api/v1/alunos?nome=Pedro&ativo=true` | HTTP 200, lista paginada | ⬜ |
| ALU-04 | Atualizar aluno | PUT `/api/v1/alunos/{id}` | HTTP 200, dados atualizados | ⬜ |
| ALU-05 | Inativar (soft delete) | DELETE `/api/v1/alunos/{id}` | HTTP 204, `ativo=false` no banco | ⬜ |
| ALU-06 | Aluno inexistente | GET `/api/v1/alunos/9999` | HTTP 404 | ⬜ |

### 3.3 Pedagogia

| ID | Cenário | Passos | Resultado Esperado | Status |
|----|---------|--------|--------------------|--------|
| PED-01 | Criar turma | POST `/api/v1/turmas` | HTTP 201 | ⬜ |
| PED-02 | Listar alunos da turma | GET `/api/v1/turmas/{id}/alunos` | HTTP 200, lista | ⬜ |
| PED-03 | Criar época pedagógica | POST `/api/v1/epocas` | HTTP 201 | ⬜ |
| PED-04 | Encerrar época | POST `/api/v1/epocas/{id}/encerrar` | HTTP 200, status atualizado | ⬜ |
| PED-05 | Registrar observação | POST `/api/v1/observacoes` | HTTP 201 | ⬜ |
| PED-06 | Listar observações do aluno | GET `/api/v1/observacoes/aluno/{id}` | HTTP 200, lista | ⬜ |

### 3.4 Financeiro

| ID | Cenário | Passos | Resultado Esperado | Status |
|----|---------|--------|--------------------|--------|
| FIN-01 | Criar contrato | POST `/api/v1/finance/contracts` | HTTP 201, número CTR-YYYY-NNNNN-MMDD | ⬜ |
| FIN-02 | Listar mensalidades | GET `/api/v1/finance/invoices` | HTTP 200, parcelas geradas | ⬜ |
| FIN-03 | Desconto irmão (10%) | Criar contrato com `descontoIrmao=true` | Valor final = valorBase × 0,90 | ⬜ |
| FIN-04 | Parcela vencida | Buscar mensalidade com vencimento passado | Status = `ATRASADA` | ⬜ |
| FIN-05 | Webhook pagamento | POST `/api/v1/finance/webhooks/payment` com payload válido | HTTP 200, status atualizado | ⬜ |

### 3.5 Comunidade

| ID | Cenário | Passos | Resultado Esperado | Status |
|----|---------|--------|--------------------|--------|
| COM-01 | Criar comunicado | POST `/api/v1/community/channels` | HTTP 201 | ⬜ |
| COM-02 | Enviar mensagem | POST `/api/v1/community/messages` | HTTP 201 | ⬜ |
| COM-03 | Criar evento | POST `/api/v1/community/events` | HTTP 201 | ⬜ |

### 3.6 LGPD

| ID | Cenário | Passos | Resultado Esperado | Status |
|----|---------|--------|--------------------|--------|
| LGP-01 | Registrar consentimento | POST `/api/v1/lgpd/consents` | HTTP 201 | ⬜ |
| LGP-02 | Revogar consentimento | PATCH `/api/v1/lgpd/consents/{id}/revoke` | HTTP 200 | ⬜ |
| LGP-03 | Abrir solicitação Art. 18 | POST `/api/v1/lgpd/requests` | HTTP 201 | ⬜ |
| LGP-04 | Avançar solicitação | PATCH `/api/v1/lgpd/requests/{id}/advance` | HTTP 200, novo status | ⬜ |
| LGP-05 | Concluir solicitação | PATCH `/api/v1/lgpd/requests/{id}/conclude` | HTTP 200, status = CONCLUIDA | ⬜ |

### 3.7 Notificações

| ID | Cenário | Passos | Resultado Esperado | Status |
|----|---------|--------|--------------------|--------|
| NOT-01 | Listar notificações do usuário | GET `/api/v1/notifications/user/{id}` | HTTP 200, lista | ⬜ |
| NOT-02 | Contador de não lidas | GET `/api/v1/notifications/user/{id}/unread-count` | HTTP 200, número | ⬜ |
| NOT-03 | Marcar como lida | PATCH `/api/v1/notifications/{id}/read` | HTTP 200 | ⬜ |
| NOT-04 | Atualizar preferências | PUT `/api/v1/notifications/preferences` | HTTP 200 | ⬜ |

---

## 4. Testes de Interface (Frontend Web)

| ID | Fluxo | Resultado Esperado | Status |
|----|-------|--------------------|--------|
| UI-01 | Login → Dashboard ADMIN | Redireciona para dashboard correto | ⬜ |
| UI-02 | Login → Dashboard SECRETARIA | Módulos: Pessoas, Financeiro, Comunidade | ⬜ |
| UI-03 | Login → Dashboard PROFESSOR | Módulos: Pedagogia, Turmas, Observações | ⬜ |
| UI-04 | Login → Dashboard PAIS | Módulos: Filhos, Financeiro (leitura), Comunicados | ⬜ |
| UI-05 | Cadastrar aluno pelo formulário | Matrícula gerada e exibida | ⬜ |
| UI-06 | Visualizar turma | Lista de alunos carregada | ⬜ |
| UI-07 | Gerar contrato | PDF/detalhes exibidos | ⬜ |
| UI-08 | Logout | Redireciona para tela de login, tokens removidos | ⬜ |

---

## 5. Testes de Regressão Automatizados

```bash
# Backend — rodar antes de cada ciclo de homologação
cd backend && ./mvnw verify
# Esperado: Tests run: 28+, Failures: 0, Errors: 0

# Frontend — lint + build
cd frontend-web && npm run lint && npm run build -- --configuration production
```

---

## 6. Critérios de Aprovação

| Critério | Meta | Obrigatório |
|---------|------|-------------|
| Casos de teste AUTH aprovados | 6/6 | ✅ Sim |
| Casos de teste ALU aprovados | 6/6 | ✅ Sim |
| Casos de teste PED aprovados | 5/6 | ✅ Sim |
| Casos de teste FIN aprovados | 4/5 | ✅ Sim |
| Casos de teste LGPD aprovados | 4/5 | ✅ Sim |
| Testes automatizados backend | 0 erros | ✅ Sim |
| Build frontend sem erros | 0 erros | ✅ Sim |
| Tempo de resposta API p95 | < 500ms | ⚠️ Desejável |
| Mobile Flutter | — | ❌ Fora do escopo |

---

## 7. Responsáveis e Comunicação

| Papel | Responsável |
|-------|-------------|
| Desenvolvedor | @danzeroum |
| Validação técnica | A definir |
| Homologador funcional | A definir |
| Go/No-Go final | A definir |

---

## 8. Registro de Resultados

Atualize este arquivo com os resultados durante a homologação:

```
Data de início: ___/___/______
Data de conclusão: ___/___/______
Total de casos: ___
Aprovados: ___
Reprovados: ___
Bloqueadores encontrados: ___
Decisão Go/No-Go: [ ] GO  [ ] NO-GO
Responsável pela aprovação: _______________
```
