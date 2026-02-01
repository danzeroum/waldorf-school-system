# 🛠️ Guia de Desenvolvimento

## Setup do Ambiente

### 1. Pré-requisitos

```bash
# Verificar versões
java -version        # Java 17+
node -v              # Node 20+
flutter --version    # Flutter 3.x
docker -v            # Docker 24+
docker-compose -v    # Compose 2.20+
```

### 2. Clone e Setup

```bash
# Clonar repositório
git clone https://github.com/danzeroum/waldorf-school-system.git
cd waldorf-school-system

# Subir serviços de infraestrutura
docker-compose up -d mysql redis rabbitmq minio

# Aguardar serviços ficarem healthy
docker-compose ps
```

### 3. Backend

```bash
cd backend

# Instalar dependências
./mvnw clean install

# Rodar migrations
./mvnw flyway:migrate

# Rodar aplicação
./mvnw spring-boot:run

# Acessar Swagger
open http://localhost:8080/swagger-ui.html
```

### 4. Frontend Web

```bash
cd frontend-web

# Instalar dependências
npm install

# Rodar em desenvolvimento
npm start

# Acessar aplicação
open http://localhost:4200
```

### 5. Mobile

```bash
cd frontend-mobile

# Instalar dependências
flutter pub get

# Rodar em emulador
flutter run
```

## Padrões de Código

### Backend (Java)

```java
// 1. Nomenclatura de pacotes
br.edu.waldorf.modules.<modulo>.<camada>

// 2. Nomenclatura de classes
- Entities: Aluno, Professor, ObservacaoDesenvolvimento
- DTOs: AlunoDTO, CreateAlunoRequest, AlunoResponse
- Services: AlunoService, PedagogiaAppService
- Controllers: AlunoController
- Repositories: AlunoRepository

// 3. Anotações obrigatórias
@RequiredArgsConstructor  // Lombok
@Slf4j                    // Logging
@Validated                // Validação
@Transactional           // Transações (service layer)
```

### Frontend (TypeScript)

```typescript
// 1. Nomenclatura de arquivos
aluno.component.ts
aluno.service.ts
aluno.model.ts
aluno.module.ts

// 2. Nomenclatura de classes
export class AlunoComponent { }
export class AlunoService { }
export interface Aluno { }

// 3. Padrões obrigatórios
- Usar standalone components (Angular 17+)
- Reactive Forms para formulários
- OnPush change detection
- Async pipe para observables
```

## Git Workflow

### Branches

```
main         # Produção
develop      # Desenvolvimento
feature/*    # Features
bugfix/*     # Correções
hotfix/*     # Hotfixes
```

### Commits (Conventional Commits)

```bash
feat: adiciona endpoint de observações
fix: corrige validação de CPF
refactor: refatora serviço de alunos
docs: atualiza documentação da API
test: adiciona testes unitários
chore: atualiza dependências
```

## Testes

### Backend

```bash
# Todos os testes
./mvnw test

# Coverage report
./mvnw jacoco:report
open target/site/jacoco/index.html

# Testes de integração
./mvnw verify -P integration-tests
```

### Frontend

```bash
# Unit tests
npm run test

# E2E tests
npm run e2e

# Coverage
npm run test:coverage
```

## Debug

### Backend (IntelliJ IDEA)

1. Run > Edit Configurations
2. Add New > Spring Boot
3. Main class: `WaldorfApplication`
4. Set breakpoints
5. Debug

### Frontend (VS Code)

1. Install extension: Debugger for Chrome
2. F5 para iniciar debug
3. Breakpoints no TypeScript

## Deployment

### Local (Docker)

```bash
# Build de tudo
docker-compose build

# Subir stack completo
docker-compose up -d
```

### Staging/Production

Ver [deployment.md](./deployment.md) para detalhes de Kubernetes.