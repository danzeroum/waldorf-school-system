// === MODELOS DE PEDAGOGIA WALDORF ===

export enum AspectoDesenvolvimento {
  FISICO    = 'FISICO',
  ANIMICO   = 'ANIMICO',
  COGNITIVO = 'COGNITIVO',
  SOCIAL    = 'SOCIAL',
  ARTISTICO = 'ARTISTICO',
  MANUAL    = 'MANUAL',
  NATUREZA  = 'NATUREZA',
  LINGUAGEM = 'LINGUAGEM',
  OUTRO     = 'OUTRO',
}

export enum Temperamento {
  COLERICO     = 'COLERICO',
  SANGUINEO    = 'SANGUINEO',
  FLEUMATICO   = 'FLEUMATICO',
  MELANCOLICO  = 'MELANCOLICO',
}

export interface ObservacaoDesenvolvimento {
  id: number;
  alunoId: number;
  alunoNome: string;
  professorId: number;
  professorNome: string;
  turmaId: number;
  turmaNome: string;
  epocaId?: number;
  epocaTitulo?: string;
  aspecto: AspectoDesenvolvimento;
  titulo: string;
  descricao: string;
  observacaoTemperamento?: string;
  sugestoesApoio?: string;
  evidencias?: string[];
  privado: boolean;
  compartilharComPais: boolean;
  developmentTags?: string[];
  createdAt: string;
  updatedAt: string;
}

export interface CreateObservacaoRequest {
  alunoId: number;
  turmaId: number;
  epocaId?: number;
  aspecto: AspectoDesenvolvimento;
  titulo: string;
  descricao: string;
  observacaoTemperamento?: string;
  sugestoesApoio?: string;
  privado: boolean;
  compartilharComPais: boolean;
  developmentTags?: string[];
}

export interface EpocaPedagogica {
  id: number;
  turmaId: number;
  titulo: string;
  tematica: string;
  dataInicio: string;
  dataFim: string;
  status: 'PLANEJADA' | 'EM_ANDAMENTO' | 'CONCLUIDA';
}

export interface RelatorioNarrativo {
  id: number;
  alunoId: number;
  alunoNome: string;
  professorId: number;
  turmaId: number;
  ciclo: string;
  periodo: string;
  titulo: string;
  textosDesenvolvimento: Record<string, string>;
  dataElaboracao: string;
  dataEntregaPais?: string;
  status: 'RASCUNHO' | 'REVISAO' | 'APROVADO' | 'ENTREGUE';
  arquivoPdfAssinado?: string;
}
