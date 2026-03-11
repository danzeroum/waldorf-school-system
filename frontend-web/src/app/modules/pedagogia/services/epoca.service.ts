import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '@environments/environment';

export interface Epoca {
  id: number;
  titulo: string;
  materia: string;
  aspecto: 'FISICO' | 'ANIMICO' | 'ESPIRITUAL';
  turmaId: number;
  turmaNome?: string;
  dataInicio: string;
  dataFim: string;
  descricao?: string;
  objetivos?: string;
  situacao: 'PLANEJADA' | 'EM_ANDAMENTO' | 'CONCLUIDA';
  totalAulas?: number;
}

export interface CreateEpocaRequest {
  titulo: string;
  materia: string;
  aspecto: string;
  turmaId: number;
  dataInicio: string;
  dataFim: string;
  descricao?: string;
  objetivos?: string;
}

@Injectable({ providedIn: 'root' })
export class EpocaService {
  private readonly api = `${environment.apiUrl}/teaching-periods`;

  constructor(private http: HttpClient) {}

  listar(turmaId?: number, anoLetivo?: number): Observable<Epoca[]> {
    let params = new HttpParams();
    if (turmaId)   params = params.set('turmaId', turmaId);
    if (anoLetivo) params = params.set('anoLetivo', anoLetivo);
    return this.http.get<Epoca[]>(this.api, { params });
  }

  buscarPorId(id: number): Observable<Epoca> {
    return this.http.get<Epoca>(`${this.api}/${id}`);
  }

  criar(req: CreateEpocaRequest): Observable<Epoca> {
    return this.http.post<Epoca>(this.api, req);
  }

  atualizar(id: number, req: Partial<CreateEpocaRequest>): Observable<Epoca> {
    return this.http.put<Epoca>(`${this.api}/${id}`, req);
  }

  encerrar(id: number): Observable<Epoca> {
    return this.http.post<Epoca>(`${this.api}/${id}/conclude`, {});
  }
}
