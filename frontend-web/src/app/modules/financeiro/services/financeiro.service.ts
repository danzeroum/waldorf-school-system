import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '@environments/environment';

export interface Contrato {
  id: number;
  alunoId: number;
  alunoNome?: string;
  turmaId: number;
  turmaNome?: string;
  anoLetivo: number;
  valorMensalidade: number;
  valorMatricula: number;
  totalParcelas: number;
  diaVencimento: number;
  status: 'ATIVO' | 'ENCERRADO' | 'SUSPENSO' | 'INADIMPLENTE';
  dataInicio: string;
  dataFim?: string;
  observacoes?: string;
  parcelas?: Parcela[];
  createdAt: string;
}

export interface Parcela {
  id: number;
  contratoId: number;
  alunoNome?: string;
  numero: number;
  descricao: string;
  valor: number;
  valorPago?: number;
  dataVencimento: string;
  dataPagamento?: string;
  status: 'PENDENTE' | 'PAGA' | 'VENCIDA' | 'CANCELADA' | 'PARCIAL';
  formaPagamento?: string;
  observacao?: string;
}

export interface CreateContratoRequest {
  alunoId: number;
  turmaId: number;
  anoLetivo: number;
  valorMensalidade: number;
  valorMatricula: number;
  totalParcelas: number;
  diaVencimento: number;
  dataInicio: string;
  observacoes?: string;
}

export interface BaixaPagamentoRequest {
  parcelaId: number;
  valorPago: number;
  dataPagamento: string;
  formaPagamento: string;
  observacao?: string;
}

export interface ResumoFinanceiro {
  totalReceita: number;
  totalRecebido: number;
  totalPendente: number;
  totalVencido: number;
  totalContratos: number;
  inadimplentes: number;
  taxaInadimplencia: number;
}

@Injectable({ providedIn: 'root' })
export class FinanceiroService {
  private readonly apiContratos = `${environment.apiUrl}/contracts`;
  private readonly apiParcelas  = `${environment.apiUrl}/invoices`;

  constructor(private http: HttpClient) {}

  // === CONTRATOS ===
  listarContratos(filtros?: { status?: string; anoLetivo?: number; nome?: string }): Observable<Contrato[]> {
    let params = new HttpParams();
    if (filtros?.status)    params = params.set('status', filtros.status);
    if (filtros?.anoLetivo) params = params.set('anoLetivo', filtros.anoLetivo);
    if (filtros?.nome)      params = params.set('nome', filtros.nome);
    return this.http.get<Contrato[]>(this.apiContratos, { params });
  }

  buscarContrato(id: number): Observable<Contrato> {
    return this.http.get<Contrato>(`${this.apiContratos}/${id}`);
  }

  criarContrato(req: CreateContratoRequest): Observable<Contrato> {
    return this.http.post<Contrato>(this.apiContratos, req);
  }

  encerrarContrato(id: number, motivo: string): Observable<Contrato> {
    return this.http.post<Contrato>(`${this.apiContratos}/${id}/terminate`, { motivo });
  }

  // === PARCELAS ===
  listarParcelas(filtros?: { status?: string; vencimentoAte?: string; contratoId?: number }): Observable<Parcela[]> {
    let params = new HttpParams();
    if (filtros?.status)       params = params.set('status', filtros.status);
    if (filtros?.vencimentoAte) params = params.set('vencimentoAte', filtros.vencimentoAte);
    if (filtros?.contratoId)   params = params.set('contratoId', filtros.contratoId);
    return this.http.get<Parcela[]>(this.apiParcelas, { params });
  }

  registrarPagamento(req: BaixaPagamentoRequest): Observable<Parcela> {
    return this.http.post<Parcela>(`${this.apiParcelas}/${req.parcelaId}/pay`, req);
  }

  // === DASHBOARD ===
  resumo(anoLetivo?: number): Observable<ResumoFinanceiro> {
    let params = new HttpParams();
    if (anoLetivo) params = params.set('anoLetivo', anoLetivo);
    return this.http.get<ResumoFinanceiro>(`${environment.apiUrl}/financial/summary`, { params });
  }
}
