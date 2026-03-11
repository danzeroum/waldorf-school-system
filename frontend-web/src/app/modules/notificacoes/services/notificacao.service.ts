import { Injectable, signal } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '@environments/environment';

export type TipoNotificacao =
  | 'MENSALIDADE_VENCENDO'
  | 'MENSALIDADE_VENCIDA'
  | 'NOVA_OBSERVACAO'
  | 'COMUNICADO'
  | 'EVENTO'
  | 'SOLICITACAO_LGPD'
  | 'SISTEMA';

export interface Notificacao {
  id: number;
  tipo: TipoNotificacao;
  titulo: string;
  mensagem: string;
  lida: boolean;
  link?: string;
  createdAt: string;
}

export interface PreferenciaNotificacao {
  email: boolean;
  push: boolean;
  sms: boolean;
  inApp: boolean;
  agregacao: 'IMEDIATO' | 'RESUMO_DIARIO' | 'RESUMO_SEMANAL';
  silencioInicio?: string;  // HH:mm
  silencioFim?: string;
}

@Injectable({ providedIn: 'root' })
export class NotificacaoService {
  private readonly api = `${environment.apiUrl}/notifications`;

  /** Contagem reativa de não lidas — usada pelo Header */
  naoLidas = signal(0);

  constructor(private http: HttpClient) {}

  listar(pagina = 1): Observable<Notificacao[]> {
    const params = new HttpParams().set('page', pagina).set('size', 20);
    return this.http.get<Notificacao[]>(`${this.api}/user/me`, { params });
  }

  contarNaoLidas(): Observable<{ count: number }> {
    return this.http.get<{ count: number }>(`${this.api}/user/me/unread-count`).pipe(
      tap(r => this.naoLidas.set(r.count)),
    );
  }

  marcarLida(id: number): Observable<void> {
    return this.http.patch<void>(`${this.api}/${id}/read`, {}).pipe(
      tap(() => this.naoLidas.update(n => Math.max(0, n - 1))),
    );
  }

  marcarTodasLidas(): Observable<void> {
    return this.http.post<void>(`${this.api}/user/me/read-all`, {}).pipe(
      tap(() => this.naoLidas.set(0)),
    );
  }

  buscarPreferencias(): Observable<PreferenciaNotificacao> {
    return this.http.get<PreferenciaNotificacao>(`${this.api}/preferences`);
  }

  salvarPreferencias(prefs: PreferenciaNotificacao): Observable<PreferenciaNotificacao> {
    return this.http.put<PreferenciaNotificacao>(`${this.api}/preferences`, prefs);
  }
}
