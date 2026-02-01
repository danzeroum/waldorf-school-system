# 📋 Plano de Banco de Dados - Sistema Waldorf

## Visão Geral

O banco de dados segue uma **modelagem híbrida** que integra:

1. **Camada Administrativa**: Estrutura formal (pessoas, matrículas, contratos)
2. **Camada Pedagógica Waldorf**: Princípios específicos (observações, desenvolvimento, épocas)
3. **Camada LGPD**: Compliance e governança de dados
4. **Camada de Serviços**: Notificações, comunicação

## Módulos Principais

### 1. Pessoas e Relacionamentos
- `pessoas` (supertipo)
- `alunos`, `professores`, `responsaveis`, `funcionarios` (subtipos)
- `responsaveis_alunos` (relacionamento N:N)

### 2. Estrutura Escolar
- `cursos`
- `turmas`
- `matriculas`

### 3. Pedagogia Waldorf
- `desenvolvimento_waldorf`
- `observacoes_desenvolvimento`
- `epocas_pedagogicas`
- `relatorios_narrativos`
- `ritmo_diario_semanal`

### 4. Financeiro
- `contratos`
- `planos_mensalidade`
- `mensalidades`
- `pagamentos`

### 5. Segurança
- `usuarios`
- `permissoes`
- `logs_sistema`

### 6. LGPD
- `consentimentos_lgpd`
- `registro_tratamento_dados`
- `solicitacoes_titular`

## Princípios de Design

1. **Normalização**: 3NF em geral, desnormalização estratégica para performance
2. **Índices**: Criados para queries frequentes
3. **Triggers**: Automação de regras de negócio
4. **Views**: Simplificação de consultas complexas
5. **Audit Trail**: Timestamps e logs para todas as mudanças críticas

Ver arquivo completo: [planoBancoDadosRelacionais.md](../planoBancoDadosRelacionais.md)