/** Modelos TypeScript compartilhados — Módulo Pessoas */

export type Genero   = 'MASCULINO' | 'FEMININO' | 'OUTRO' | 'NAO_INFORMADO';
export type Temperamento = 'COLERICO' | 'SANGUINEO' | 'FLEUGMATICO' | 'MELANCOLICO';

export interface Endereco {
  logradouro: string;
  numero:     string;
  complemento?: string;
  bairro:     string;
  cidade:     string;
  estado:     string;
  cep:        string;
}

export interface Pessoa {
  id:         number;
  nome:       string;
  cpf?:       string;
  dataNascimento: string;   // ISO date
  genero:     Genero;
  email?:     string;
  telefone?:  string;
  endereco?:  Endereco;
  ativo:      boolean;
  createdAt:  string;
  updatedAt:  string;
}

export interface Responsavel extends Pessoa {
  parentesco: string;
  profissao?: string;
  empresa?:   string;
  autorizado: boolean;
}

export interface Aluno extends Pessoa {
  matricula:      string;
  turmaId?:       number;
  turmaNome?:     string;
  anoIngresso:    number;
  temperamento?:  Temperamento;
  responsaveis:   Responsavel[];
  ativo:          boolean;
}

export interface Professor extends Pessoa {
  registro?:      string;
  especialidades: string[];
  turmasIds:      number[];
}
