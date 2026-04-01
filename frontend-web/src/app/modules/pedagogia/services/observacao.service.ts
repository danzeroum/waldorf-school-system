import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '@environments/environment';

// Alinhado com ObservacaoResponseDTO do backend (com.waldorf)
export interface ObservacaoPedagogica {
  id: number;
  alunoId: number;
  alunoNome?: string;
  professorId: number;
  professorNome?: string;
  epocaId?: number;
  epocaTitulo?: string;
  data: string;
  aspecto: 'FISICO' | 'ANIMICO' | 'ESPIRITUAL';
  conteudo: string;
  privada: boolean;
  createdAt?: string;
}

export interface CreateObservacaoRequest {
  alunoId: number;
  epocaId?: number;
  data: string;
  aspecto: string;
  conteudo: string;
  privada: boolean;
}

@Injectable({ providedIn: 'root' })
export class ObservacaoService {
  private readonly api = `${environment.apiUrl}/observacoes`; // era: /observations

  constructor(private http: HttpClient) {}

  listarPorAluno(alunoId: number): Observable<ObservacaoPedagogica[]> {
    const params = new HttpParams().set('alunoId', alunoId); // era: studentId
    return this.http.get<ObservacaoPedagogica[]>(this.api, { params });
  }

  criar(req: CreateObservacaoRequest): Observable<ObservacaoPedagogica> {
    return this.http.post<ObservacaoPedagogica>(this.api, req);
  }

  atualizar(id: number, req: Partial<CreateObservacaoRequest>): Observable<ObservacaoPedagogica> {
    return this.http.put<ObservacaoPedagogica>(`${this.api}/${id}`, req);
  }

  excluir(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/${id}`);
  }
}
