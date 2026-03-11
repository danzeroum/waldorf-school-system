import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '@environments/environment';

export interface Comunicado {
  id: number;
  assunto: string;
  corpo: string;
  destinatarios: 'TODOS' | 'TURMA' | 'RESPONSAVEIS' | 'PROFESSORES';
  turmaId?: number;
  turmaNome?: string;
  autorNome: string;
  dataEnvio: string;
  totalDestinatarios?: number;
  totalLidos?: number;
}

export interface CreateComunicadoRequest {
  assunto: string;
  corpo: string;
  destinatarios: string;
  turmaId?: number;
}

@Injectable({ providedIn: 'root' })
export class ComunicadoService {
  private readonly api = `${environment.apiUrl}/communications`;

  constructor(private http: HttpClient) {}

  listar(): Observable<Comunicado[]> {
    return this.http.get<Comunicado[]>(this.api);
  }

  criar(req: CreateComunicadoRequest): Observable<Comunicado> {
    return this.http.post<Comunicado>(this.api, req);
  }
}
