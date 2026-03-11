// === MODELOS DE PESSOAS ===

export interface Pessoa {
  id: number;
  nomeCompleto: string;
  nomeSocial?: string;
  cpf?: string;
  dataNascimento?: string;
  email?: string;
  telefone?: string;
  celular?: string;
  fotoPerfil?: string;
  tipo: TipoPessoa;
  createdAt: string;
  updatedAt: string;
}

export enum TipoPessoa {
  ALUNO = 'ALUNO',
  PROFESSOR = 'PROFESSOR',
  RESPONSAVEL = 'RESPONSAVEL',
  FUNCIONARIO = 'FUNCIONARIO',
}

export interface Aluno extends Pessoa {
  matricula?: string;
  situacao: SituacaoAluno;
  turmaAtual?: TurmaResumo;
  responsaveis?: ResponsavelResumo[];
  dadosMedicos?: DadosMedicos;
}

export enum SituacaoAluno {
  ATIVO = 'ATIVO',
  INATIVO = 'INATIVO',
  TRANSFERIDO = 'TRANSFERIDO',
  DESLIGADO = 'DESLIGADO',
  PENDENTE = 'PENDENTE',
}

export interface TurmaResumo {
  id: number;
  nome: string;
  anoLetivo: number;
  serie: string;
}

export interface ResponsavelResumo {
  id: number;
  nomeCompleto: string;
  parentesco: string;
  telefone?: string;
  celular?: string;
  email?: string;
  autorizadoBuscar: boolean;
  contatoEmergencia: boolean;
}

export interface DadosMedicos {
  tipoSanguineo?: string;
  planoSaude?: string;
  alergias?: string;
  medicamentosControlados?: string;
  necessidadesEspeciais?: string;
  observacoesMedicas?: string;
}

// === PAGINAÇÃO ===
export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}

export interface PageRequest {
  page: number;
  size: number;
  sort?: string;
  direction?: 'ASC' | 'DESC';
}
