/** Modelos TypeScript compartilhados — Módulo Pessoas */

export type Genero = 'MASCULINO' | 'FEMININO' | 'OUTRO' | 'NAO_INFORMADO';
export type Temperamento = 'COLERICO' | 'SANGUINEO' | 'FLEUGMATICO' | 'MELANCOLICO';

export interface Endereco {
  logradouro: string;
  numero: string;
  complemento?: string;
  bairro: string;
  cidade: string;
  estado: string;
  cep: string;
}

export enum TipoPessoa {
  ALUNO = 'ALUNO',
  PROFESSOR = 'PROFESSOR',
  RESPONSAVEL = 'RESPONSAVEL',
  FUNCIONARIO = 'FUNCIONARIO',
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

export interface Pessoa {
  id: number;
  nomeCompleto: string;
  nome?: string;
  nomeSocial?: string;
  cpf?: string;
  dataNascimento?: string;
  email?: string;
  telefone?: string;
  celular?: string;
  fotoPerfil?: string;
  tipo?: TipoPessoa;
  genero?: Genero;
  endereco?: Endereco;
  ativo?: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface Responsavel extends Pessoa {
  parentesco: string;
  profissao?: string;
  empresa?: string;
  autorizado?: boolean;
  autorizadoBuscar?: boolean;
  contatoEmergencia?: boolean;
}

export interface Aluno extends Pessoa {
  matricula?: string;
  situacao: SituacaoAluno;
  turmaAtual?: TurmaResumo;
  turmaId?: number;
  turmaNome?: string;
  anoIngresso?: number;
  temperamento?: Temperamento;
  responsaveis?: ResponsavelResumo[];
  dadosMedicos?: DadosMedicos;
}

export interface Professor extends Pessoa {
  registro?: string;
  especialidades: string[];
  turmasIds: number[];
}

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
