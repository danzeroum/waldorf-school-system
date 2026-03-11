import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '@environments/environment';

export interface Turma {
  id: number;
  nome: string;
  serie: string;
  anoLetivo: number;
  professorRegente?: { id: number; nomeCompleto: string };
  totalAlunos: number;
  situacao: 'ATIVA' | 'ENCERRADA' | 'PLANEJADA';
}

export interface AlunoTurma {
  id: number;
  nomeCompleto: string;
  nomeSocial?: string;
  situacao: string;
  dataNascimento?: string;
}

@Injectable({ providedIn: 'root' })
export class PedagogiaService {
  private readonly apiTurmas   = `${environment.apiUrl}/classes`;
  private readonly apiMatriculas = `${environment.apiUrl}/enrollments`;

  constructor(private http: HttpClient) {}

  listarTurmas(anoLetivo?: number): Observable<Turma[]> {
    let params = new HttpParams();
    if (anoLetivo) params = params.set('anoLetivo', anoLetivo);
    return this.http.get<Turma[]>(this.apiTurmas, { params });
  }

  buscarTurma(id: number): Observable<Turma> {
    return this.http.get<Turma>(`${this.apiTurmas}/${id}`);
  }

  listarAlunosDaTurma(turmaId: number): Observable<AlunoTurma[]> {
    return this.http.get<AlunoTurma[]>(`${this.apiTurmas}/${turmaId}/students`);
  }

  criarTurma(turma: Partial<Turma>): Observable<Turma> {
    return this.http.post<Turma>(this.apiTurmas, turma);
  }

  atualizarTurma(id: number, turma: Partial<Turma>): Observable<Turma> {
    return this.http.put<Turma>(`${this.apiTurmas}/${id}`, turma);
  }
}
