import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '@environments/environment';

// Alinhado com EpocaResponseDTO do backend (com.waldorf)
export interface Epoca {
  id: number;
  turmaId: number;
  turmaNome?: string;
  titulo: string;
  materia: string;
  aspecto: 'FISICO' | 'ANIMICO' | 'ESPIRITUAL';
  dataInicio: string;
  dataFim: string;
  descricao?: string;
  objetivos?: string;
  status: 'PLANEJADA' | 'EM_ANDAMENTO' | 'CONCLUIDA'; // era: situacao
  totalAulas?: number;
  createdAt?: string;
}

export interface CreateEpocaRequest {
  turmaId: number;
  titulo: string;
  materia: string;
  aspecto?: string;
  dataInicio: string;
  dataFim?: string;
  descricao?: string;
  objetivos?: string;
}

@Injectable({ providedIn: 'root' })
export class EpocaService {
  private readonly api = `${environment.apiUrl}/epocas`; // era: /teaching-periods

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
    return this.http.post<Epoca>(`${this.api}/${id}/encerrar`, {}); // era: /conclude
  }
}
