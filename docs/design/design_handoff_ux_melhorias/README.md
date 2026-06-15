# Handoff: Melhorias de UX — Quintal Aroeira (Escola Aroeira)

## Overview
Redesenho de UX de três áreas do sistema de gestão escolar Waldorf, **mantendo 100% da paleta, tipografia e design system atuais**. O objetivo é melhorar hierarquia visual, escaneabilidade de listas e consistência entre módulos — sem reescrever a fundação visual.

Áreas cobertas:
1. **Shell & navegação** (sidebar + header) — ajustes pontuais
2. **Dashboard da Secretaria** — `secretaria-dashboard`
3. **Financeiro · Parcelas** — `parcela-list`
4. **Pedagogia · Observações** — `observacao-pedagogica`

## About the Design Files
Os arquivos `.dc.html` deste pacote são **referências de design feitas em HTML** — protótipos que mostram a aparência e o comportamento pretendidos, **não** código de produção para copiar e colar.

O alvo é um codebase **já existente**: **Angular 17 (standalone-less, NgModules) + TailwindCSS + Angular Material**, repositório `danzeroum/waldorf-school-system`, pasta `frontend-web/`. A tarefa é **recriar estes designs dentro do ambiente existente**, reutilizando os componentes, classes utilitárias e padrões já estabelecidos no projeto (descritos abaixo). Não introduzir novas bibliotecas de UI nem novos tokens de cor.

## Fidelity
**Alta fidelidade (hifi).** Cores, tipografia, espaçamento e estados finais. Recriar pixel-a-pixel usando as classes do design system do projeto (`styles.scss`), **não** os estilos inline do HTML (que existem só porque o protótipo é um arquivo isolado). Onde o protótipo usa `style="..."`, traduzir para as classes Tailwind/`@layer components` equivalentes listadas em **Design Tokens**.

---

## Princípios transversais (aplicar em todas as telas)
- **Padronizar componentes:** usar sempre as classes de `styles.scss` (`.card`, `.btn-primary`, `.badge-*`, `.table-*`). **Aposentar** o `dashboard.component.html` genérico que usa `mat-card`/cinza.
- **Formulários:** padronizar no padrão **slide-panel** já existente em `turma-list.component.html` (backdrop + `fixed inset-y-0 right-0 w-96`), em vez de formulários inline que empurram a página.
- **Cor semântica:** verde (`waldorf-green`) = saudável/ok, dourado (`waldorf-amber`) = atenção, vermelho/terracota (`red-*`) = risco.
- **Estados existentes:** preservar skeletons (`.skeleton`), empty-states e animações (`.animate-fade-in-up`).

---

## Screens / Views

### 0. Shell & navegação
- **Arquivos:** `modules/layout/sidebar/sidebar.component.{html,ts}`, `modules/layout/header/header.component.html`
- **Manter:** estrutura colapsável 260/68px (`sidebar-container`), `secoesFiltradas()` por perfil, auto-expand do grupo ativo.
- **Ajustar:**
  - Item ativo: fundo `bg-waldorf-green-100`, texto `text-waldorf-green-700`, `font-bold`.
  - Header: busca global com atalho `⌘K` → abrir command palette. **[NOVO]**
  - Badge do sino vindo de `notificacoesNaoLidas()` em vez de valor fixo `3`. **[NOVO]**

### 1. Dashboard da Secretaria
- **Arquivo:** `modules/dashboard/containers/secretaria-dashboard/secretaria-dashboard.component.{html,ts}`
- **Purpose:** Visão de abertura da secretaria — o que precisa de ação hoje + saúde geral.
- **Layout (vertical, `space-y-6`):**
  1. **Saudação** — linha flex `justify-between`. Esquerda: `<h1>` Lora 26px + data por extenso (`date:"EEEE, d 'de' MMMM 'de' yyyy":'':'pt-BR'`). Direita: `.btn-primary` "Novo aluno" + `.btn-secondary` "Nova matrícula".
  2. **Faixa "Precisa da sua atenção"** — card largura total, fundo gradiente terracota→dourado claro, ícone `priority_high` em quadrado `#C0564B`, três contadores inline, botão "Resolver agora". **Renderizar só quando há pendências.**
  3. **3 KPIs principais** — grid `lg:grid-cols-3`, cada um `.card` com `border-t-4`:
     - Alunos ativos → `border-waldorf-green-500`, número 38px, tendência "+6 este mês".
     - A receber → `border-waldorf-amber-500`, valor `formatarMoeda`, barra de progresso `[style.width.%]`, "68% recebido".
     - Inadimplência → `border-red-500`, "3,5%", "5 alunos · R$ 4.100".
  4. **3 métricas secundárias** — grid `sm:grid-cols-3`, cards compactos com ícone + número + label (Matrículas pendentes, Contratos vencendo, LGPD pendentes).
  5. **Atividade + gráfico** — grid `lg:grid-cols-[1.2fr_1fr]`: timeline (ponto colorido por origem) + gráfico de barras de recebimento (6 meses).
- **Components / cores:** ver Design Tokens. Ícones Material Symbols: `person_add`, `assignment_add`, `priority_high`, `school`, `savings`, `trending_up`, `assignment_late`, `event_busy`, `policy`, `history`, `bar_chart`.

### 2. Financeiro · Parcelas
- **Arquivo:** `modules/financeiro/parcela/parcela-list/parcela-list.component.{html,ts}`
- **Purpose:** Operar cobranças e baixas com rapidez.
- **Layout (`space-y-5`):**
  1. **Header** — `<h1>` "Parcelas" + busca (`.form-input` com ícone `search`) + botão "Exportar" (`.btn-secondary`).
  2. **Barra de totais** — grid `grid-cols-4`: Total (neutro), Recebido (verde), Pendente (dourado), Vencido (terracota). Reutilizar os campos do `ResumoFinanceiro` do dashboard financeiro.
  3. **Chips de status com contagem** — "Todas · 68", "Pendente · 19", "Vencida · 5", "Paga · 44". Chip ativo `.btn-primary`-like.
  4. **Barra de ações em lote** — fundo escuro `#252321`, aparece quando há seleção: "N selecionadas", "Enviar cobrança", "Baixar selecionadas", "Limpar".
  5. **Tabela** — colunas: checkbox · Aluno (avatar de iniciais + descrição) · Vencimento (badge relativo) · Status (`badge`) · Valor (mono, `text-right`) · Ação. Linhas vencidas: `border-l-[3px] border-red-500` + fundo `#FDF6F4`.
  6. **Paginação** — "Mostrando 1–10 de 68" + controles.
  - **Modal de baixa:** já existe (`baixaAberta()`, `formBaixa`, `confirmarBaixa()`) — reutilizar sem mudança.

### 3. Pedagogia · Observações
- **Arquivo:** `modules/pedagogia/observacao/observacao-pedagogica.component.{html,ts}`
- **Purpose:** Registrar e revisar observações pedagógicas Waldorf por aluno/época.
- **Layout (`space-y-5`):**
  1. **Header** — `<h1>` + subtítulo de contexto (turma · época) + `.btn-primary` "Nova observação" (abre slide-panel).
  2. **Seletores de contexto** — 3 dropdowns: Turma, Aluno, Época.
  3. **Filtros por aspecto** — chips coloridos por aspecto (cor própria de cada um) com contagem "· N". Os 7 aspectos Waldorf: Físico, Anímico, Espiritual, Cognitivo, Social, Artístico, Manual.
  4. **Cards de observação** — `.card` com `border-l-4` da cor do aspecto. Header: badge de aspecto (`.aspect-*`) + avatar/nome do aluno + (opcional) badge "Privada" com `lock`. Conteúdo `whitespace-pre-line`. Rodapé: época + professor; ações Editar/Excluir (hover-reveal mantido).
  5. **Formulário** — migrar de inline (`exibirForm()`) para **slide-panel** (padrão de `turma-list`), preservando `form`, `salvar()` e o stepper de status RASCUNHO → REVISADO → APROVADO.

---

## Interactions & Behavior
- **Dashboard:** faixa de atenção → botões navegam para `/financeiro/parcelas?status=VENCIDA`, `/lgpd`, etc. Cards KPI clicáveis (já há padrão `irParaParcelas(status)`).
- **Parcelas:** checkbox alterna item no `Set`; barra de lote condicional; "Baixar" abre modal existente; "Baixar selecionadas" itera o `Set` chamando o fluxo de baixa; busca filtra por nome do aluno; chips trocam `filtroStatus()` e chamam `carregar()`.
- **Observações:** chips trocam `filtroAspecto()`; "Nova observação" abre slide-panel; Editar carrega item no form; Excluir confirma.
- **Animações:** manter `.animate-fade-in-up` (fadeInUp 0.3s ease-out) nos containers; transições de hover 200ms; slide-panel desliza da direita.
- **Loading:** manter skeletons existentes durante `carregando()`.
- **Responsivo:** grids colapsam para 1–2 colunas em `sm`/`md` (manter padrões `hidden sm:table-cell` etc.); sidebar vira overlay em mobile.

## State Management (Angular signals)
**EXISTE (reutilizar):**
- Dashboard: `carregando()`, `metricas()` (`totalAlunosAtivos`, `matriculasPendentes`, `valorAReceber`, `contratosVencendo`, `mensalidadesAtrasadas`, `lgpdPendentes`), `atividades()`, `formatarMoeda()`.
- Parcelas: `parcelas()`, `carregando()`, `filtroStatus()`, `carregar()`, `formatarMoeda()`, `baixaAberta()`, `formBaixa`, `salvandoBaixa()`, `abrirBaixa()`, `confirmarBaixa()`, `fecharBaixa()`; pipe `statusParcela`.
- Observações: `observacoesFiltradas`, `carregando()`, `filtroAspecto()`, `aspectos`, `exibirForm()`, `form`, `salvar()`, `editar()`, `excluir()`, `statusLocal()`; pipe `aspecto`.

**NOVO (criar):**
- Dashboard: `temPendencias` (computed), `percentualRecebido` (computed), `recebimentoMensal()` (endpoint), campos `alunosNovosMes` e `atividade.origem` no DTO.
- Parcelas: `termoBusca` (signal), `selecionadas: signal<Set<number>>`, `contagemPorStatus()` (computed), `pagina` (signal) + paginação na API, `baixarLote()`, `enviarCobranca()` (endpoint), pipes `iniciais` e `vencimentoRelativo`.
- Observações: `turmaSel`/`alunoSel`/`epocaSel` (signals), `contagemPorAspecto()` (computed), utilitário de borda por aspecto (`aspect-border-*`).

## Design Tokens (de `tailwind.config.js` + `styles.scss`)
**Cores:**
- `waldorf-green-500 #5B7F5E`, `-600 #4A6B4D`, `-700 #384E3A`, `-300 #8fae91`, `-100` (claro)
- `waldorf-amber` (dourado) base `#C4A265`, `-700 ≈ #8a6d3b`, `-100` (claro)
- terracota `#D4956A`; risco `red-500 ≈ #C0564B` (alinhado à paleta)
- `waldorf-cream-50 #F5F0E8`, `-200 #E0D5C2`, `-300 #D4C5AC`
- `waldorf-gray-900 #252321`, `-800 #3a3835`, `-700 #4a4742`, `-500`, `-400`
- Aspectos (badges `.aspect-*`): físico=green, anímico=blue, espiritual=indigo, cognitivo=orange, social=red, artístico=purple, manual=amber (100 bg / 700 texto)

**Tipografia:** títulos `Lora` (600/700); corpo/UI `Nunito` (400–800); números/valores `JetBrains Mono`.

**Raio:** cards `rounded-2xl` (1rem+), botões/inputs `rounded-xl`, badges `rounded-full`. **Sombra:** `shadow-waldorf-sm/md`.

**Classes utilitárias (`@layer components`):** `.btn-primary` `.btn-secondary` `.btn-ghost` `.btn-icon` `.btn-danger` · `.card` `.card-hover` · `.form-input` `.form-label` `.form-error` · `.badge-success/-warning/-danger/-info/-neutral` · `.table-header` `.table-cell` `.table-row-hover` · `.skeleton` `.divider` · `.aspect-fisico …` · animações `.animate-fade-in-up/-fade-in/-slide-left`.

## Assets
- **Ícones:** Google **Material Symbols Outlined** (já usado no projeto via `<span class="material-icons">`). Nomes citados em cada tela.
- **Fontes:** Lora, Nunito, JetBrains Mono (Google Fonts).
- Sem imagens raster; avatares são iniciais sobre fundo `waldorf-green-100`.

## Files (neste pacote)
- `Telas para Desenvolvimento.dc.html` — telas finais (hifi) em tamanho real + ficha técnica de cada uma. **Referência principal.**
- `Auditoria UX — Quintal Aroeira.dc.html` — comparativo antes → depois e justificativa de cada mudança.

> Os dois HTML abrem direto no navegador. Use-os como referência visual; implemente com as classes do design system do projeto.
