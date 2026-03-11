import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '@environments/environment';

export interface Consentimento {
  id: number;
  alunoId: number;
  alunoNome: string;
  responsavelNome: string;
  responsavelEmail: string;
  tipo: string;
  status: 'PENDENTE' | 'ACEITO' | 'RECUSADO' | 'REVOGADO';
  dataAceite?: string;
  dataRevogacao?: string;
  versaoTermos: string;
  ipAceite?: string;
}

export interface SolicitacaoTitular {
  id: number;
  alunoId: number;
  alunoNome: string;
  solicitanteNome: string;
  solicitanteEmail: string;
  tipo: 'ACESSO' | 'CORRECAO' | 'EXCLUSAO' | 'PORTABILIDADE' | 'OPOSICAO';
  status: 'PENDENTE' | 'EM_ANALISE' | 'CONCLUIDA' | 'NEGADA';
  descricao: string;
  dataSolicitacao: string;
  dataConclusao?: string;
  resposta?: string;
}

export interface ResumoLgpd {
  totalConsentimentos: number;
  consentimentosAtivos: number;
  consentimentosPendentes: number;
  consentimentosRevogados: number;
  solicitacoesPendentes: number;
  solicitacoesEmAnalise: number;
  percentualConformidade: number;
}

@Injectable({ providedIn: 'root' })
export class LgpdService {
  private readonly apiConsent  = `${environment.apiUrl}/consents`;
  private readonly apiRequests = `${environment.apiUrl}/data-requests`;

  constructor(private http: HttpClient) {}

  listarConsentimentos(status?: string): Observable<Consentimento[]> {
    let params = new HttpParams();
    if (status) params = params.set('status', status);
    return this.http.get<Consentimento[]>(this.apiConsent, { params });
  }

  listarSolicitacoes(status?: string): Observable<SolicitacaoTitular[]> {
    let params = new HttpParams();
    if (status) params = params.set('status', status);
    return this.http.get<SolicitacaoTitular[]>(this.apiRequests, { params });
  }

  responderSolicitacao(id: number, resposta: string, status: 'CONCLUIDA' | 'NEGADA'): Observable<SolicitacaoTitular> {
    return this.http.post<SolicitacaoTitular>(`${this.apiRequests}/${id}/respond`, { resposta, status });
  }

  resumo(): Observable<ResumoLgpd> {
    return this.http.get<ResumoLgpd>(`${environment.apiUrl}/lgpd/summary`);
  }
}
