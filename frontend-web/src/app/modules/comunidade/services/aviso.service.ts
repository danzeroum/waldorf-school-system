import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '@environments/environment';

// FIX: adicionado 'FESTIVAL' e 'MUTIRAO' para alinhar com enum Java TipoAviso
export type TipoAviso = 'GERAL' | 'TURMA' | 'URGENTE' | 'EVENTO' | 'CARDAPIO' | 'FESTIVAL' | 'MUTIRAO';

export interface Aviso {
  id: number;
  titulo: string;
  conteudo: string;
  tipo: TipoAviso;
  turmaId?: number;
  turmaNome?: string;
  autorNome: string;
  fixado: boolean;
  dataPublicacao: string;
  dataExpiracao?: string;
  lido?: boolean;
}

export interface CreateAvisoRequest {
  titulo: string;
  conteudo: string;
  tipo: TipoAviso;
  turmaId?: number;
  fixado: boolean;
  dataExpiracao?: string;
}

@Injectable({ providedIn: 'root' })
export class AvisoService {
  private readonly api = `${environment.apiUrl}/announcements`;

  constructor(private http: HttpClient) {}

  listar(turmaId?: number): Observable<Aviso[]> {
    let params = new HttpParams();
    if (turmaId) params = params.set('turmaId', turmaId);
    return this.http.get<Aviso[]>(this.api, { params });
  }

  criar(req: CreateAvisoRequest): Observable<Aviso> {
    return this.http.post<Aviso>(this.api, req);
  }

  marcarLido(id: number): Observable<void> {
    return this.http.post<void>(`${this.api}/${id}/read`, {});
  }

  excluir(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/${id}`);
  }
}
