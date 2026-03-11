/** Modelos TypeScript compartilhados — Módulo Financeiro */

export type StatusContrato = 'ATIVO' | 'ENCERRADO' | 'SUSPENSO' | 'INADIMPLENTE';
export type StatusParcela  = 'PENDENTE' | 'PAGA' | 'VENCIDA' | 'CANCELADA' | 'PARCIAL';
export type FormaPagamento = 'PIX' | 'BOLETO' | 'CARTAO_CREDITO' | 'CARTAO_DEBITO' | 'TRANSFERENCIA' | 'DINHEIRO';

export interface Contrato {
  id:                number;
  alunoId:           number;
  alunoNome?:        string;
  turmaId:           number;
  turmaNome?:        string;
  anoLetivo:         number;
  valorMensalidade:  number;
  valorMatricula:    number;
  totalParcelas:     number;
  diaVencimento:     number;
  status:            StatusContrato;
  dataInicio:        string;
  dataFim?:          string;
  observacoes?:      string;
  parcelas?:         Parcela[];
  createdAt:         string;
}

export interface Parcela {
  id:               number;
  contratoId:       number;
  alunoNome?:       string;
  numero:           number;
  descricao:        string;
  valor:            number;
  valorPago?:       number;
  dataVencimento:   string;
  dataPagamento?:   string;
  status:           StatusParcela;
  formaPagamento?:  FormaPagamento;
  observacao?:      string;
}
