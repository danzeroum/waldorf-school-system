# 🏗️ Arquitetura do Sistema Waldorf

## Modelo C4

### Nível 1: Contexto

```
ATORES EXTERNOS:
- Pais/Responsáveis
- Professores
- Secretária/Administradores
- Direção

SISTEMA CENTRAL:
- Sistema Escolar Waldorf

INTEGRAÇÕES:
- Gateway de Pagamento (Pagar.me/Stripe)
- Email (SendGrid)
- SMS/Push (Firebase)
```

### Nível 2: Containers

```
1. Portal Web (Angular 17+)
   - Interface administrativa
   - Gestão pedagógica
   - Relatórios

2. Aplicativo Mobile (Flutter)
   - Acesso para pais
   - Offline-first
   - Notificações push

3. API Backend (Spring Boot)
   - REST API
   - Business logic
   - Integrações

4. Banco de Dados (MySQL)
   - Dados transacionais
   - Auditoria

5. Cache (Redis)
   - Sessões
   - Rate limiting

6. Fila (RabbitMQ)
   - Processamento assíncrono
   - Notificações

7. Storage (MinIO/S3)
   - Fotos
   - Documentos
   - Portfólios
```

### Nível 3: Componentes (Backend)

```
MÓDULOS PRINCIPAIS:
- Auth Service (JWT, RBAC)
- Pessoa Service (CRUD)
- Aluno Service
- Professor Service
- Pedagogia Service (observações, épocas)
- Gestão Service (turmas, matrículas)
- Financeiro Service
- Notificação Service
- LGPD Service
- Comunidade Service
```

## Stack Tecnológico

### Backend
- **Framework**: Spring Boot 3.2+
- **Java**: 17 LTS
- **Segurança**: Spring Security + JWT
- **Persistência**: Spring Data JPA + Hibernate
- **Migrations**: Flyway
- **Documentação**: SpringDoc OpenAPI
- **Testes**: JUnit 5, Mockito, TestContainers

### Frontend Web
- **Framework**: Angular 17+
- **UI**: Angular Material + Custom Components
- **State**: NgRx (opcional)
- **Forms**: Reactive Forms
- **HTTP**: HttpClient com interceptors
- **Testes**: Jasmine, Karma, Cypress

### Mobile
- **Framework**: Flutter 3.x
- **State**: BLoC Pattern
- **Local DB**: SQLite (sqflite)
- **HTTP**: Dio
- **Notifications**: Firebase Cloud Messaging

Ver arquivo completo: [planoArquitetura.md](../planoArquitetura.md)