/** Modelos TypeScript compartilhados — Módulo Pedagogia */

export type AspectoWaldorf = 'FISICO' | 'ANIMICO' | 'ESPIRITUAL';

export interface Turma {
  id:          number;
  nome:        string;         // ex: 'Classe 3'
  anoLetivo:   number;
  professorId: number;
  professorNome?: string;
  totalAlunos: number;
  ativa:       boolean;
}

export interface EpocaPedagogica {
  id:           number;
  turmaId:      number;
  turmaNome?:   string;
  titulo:       string;
  materia:      string;
  aspecto:      AspectoWaldorf;
  dataInicio:   string;
  dataFim:      string;
  descricao?:   string;
  objetivos?:   string;
  status:       'PLANEJADA' | 'EM_ANDAMENTO' | 'CONCLUIDA';
}

export interface ObservacaoPedagogica {
  id:          number;
  alunoId:     number;
  alunoNome?:  string;
  professorId: number;
  aspecto:     AspectoWaldorf;
  conteudo:    string;
  privada:     boolean;
  data:        string;
  createdAt:   string;
}
