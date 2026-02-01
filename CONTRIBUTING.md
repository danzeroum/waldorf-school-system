# Contribuindo para o Sistema Waldorf

## Como Contribuir

### 1. Fork e Clone

```bash
# Fork no GitHub
# Clone seu fork
git clone https://github.com/SEU-USUARIO/waldorf-school-system.git
cd waldorf-school-system

# Adicione o remote upstream
git remote add upstream https://github.com/danzeroum/waldorf-school-system.git
```

### 2. Crie uma Branch

```bash
# Sempre partir de develop
git checkout develop
git pull upstream develop

# Criar feature branch
git checkout -b feature/nome-da-feature
```

### 3. Desenvolva

- Siga os padrões de código
- Escreva testes
- Mantenha commits atômicos
- Use Conventional Commits

### 4. Teste

```bash
# Backend
cd backend
./mvnw test

# Frontend
cd frontend-web
npm run test
npm run lint
```

### 5. Pull Request

1. Push sua branch
2. Abra PR para `develop`
3. Descreva suas mudanças
4. Aguarde review

## Padrões de Código

### Java
- Google Java Style Guide
- Cobertura de testes > 70%
- SonarQube sem issues críticos

### TypeScript
- Angular Style Guide
- ESLint sem erros
- Componentes testados

### Commits

```
<tipo>(<escopo>): <descrição>

<corpo opcional>

<rodapé opcional>
```

Tipos: `feat`, `fix`, `docs`, `style`, `refactor`, `test`, `chore`

## Código de Conduta

- Seja respeitoso
- Aceite críticas construtivas
- Foque no melhor para o projeto
- Ajude outros desenvolvedores

## Questões?

Abra uma issue ou pergunte no Discord da comunidade.