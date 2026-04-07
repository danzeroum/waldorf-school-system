import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '@environments/environment';

export interface Professor {
  id: number;
  nome: string;
  email: string;
  especialidade: string;
  ativo: boolean;
  createdAt: string;
}

export interface ProfessorRequest {
  nome: string;
  email: string;
  especialidade?: string;
}

export interface PageProfessor {
  content: Professor[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

@Injectable({ providedIn: 'root' })
export class ProfessorService {
  private readonly base = `${environment.apiUrl}/professores`;

  constructor(private http: HttpClient) {}

  listar(params?: { page?: number; size?: number }): Observable<PageProfessor> {
    let p = new HttpParams();
    if (params?.page != null) p = p.set('page', String(params.page));
    if (params?.size != null) p = p.set('size', String(params.size));
    return this.http.get<PageProfessor>(this.base, { params: p });
  }

  buscarPorId(id: number): Observable<Professor> {
    return this.http.get<Professor>(`${this.base}/${id}`);
  }

  criar(dto: ProfessorRequest): Observable<Professor> {
    return this.http.post<Professor>(this.base, dto);
  }

  atualizar(id: number, dto: ProfessorRequest): Observable<Professor> {
    return this.http.put<Professor>(`${this.base}/${id}`, dto);
  }

  inativar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }
}
