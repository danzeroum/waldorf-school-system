import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '@environments/environment';

/** Alinhado com backend TurmaResponseDTO */
export interface Turma {
  id: number;
  nome: string;
  anoLetivo: number;
  anoEscolar?: number;
  capacidadeMaxima?: number;
  professorRegenteId?: number;
  professorRegenteNome?: string;
  totalAlunos: number;
  ativa: boolean;
  createdAt: string;
  updatedAt?: string;
}

/** Alinhado com backend TurmaRequestDTO */
export interface TurmaRequest {
  nome: string;
  anoLetivo: number;
  anoEscolar?: number;
  professorRegenteId?: number;
  capacidadeMaxima?: number;
  ativa?: boolean;
}

/** Alinhado com AlunoResponseDTO retornado por /turmas/:id/alunos */
export interface AlunoTurma {
  id: number;
  matricula?: string;
  nome: string;
  dataNascimento?: string;
  ativo: boolean;
}

@Injectable({ providedIn: 'root' })
export class PedagogiaService {
  private readonly base = `${environment.apiUrl}/turmas`; // era /classes ❌

  constructor(private http: HttpClient) {}

  listarTurmas(anoLetivo?: number): Observable<Turma[]> {
    let params = new HttpParams();
    if (anoLetivo) params = params.set('anoLetivo', anoLetivo);
    return this.http.get<Turma[]>(this.base, { params });
  }

  buscarTurma(id: number): Observable<Turma> {
    return this.http.get<Turma>(`${this.base}/${id}`);
  }

  listarAlunosDaTurma(turmaId: number): Observable<AlunoTurma[]> {
    return this.http.get<AlunoTurma[]>(`${this.base}/${turmaId}/alunos`); // era /students ❌
  }

  criarTurma(dto: TurmaRequest): Observable<Turma> {
    return this.http.post<Turma>(this.base, dto);
  }

  atualizarTurma(id: number, dto: TurmaRequest): Observable<Turma> {
    return this.http.put<Turma>(`${this.base}/${id}`, dto);
  }

  toggleAtiva(id: number, ativa: boolean): Observable<Turma> {
    return this.http.put<Turma>(`${this.base}/${id}`, { ativa });
  }
}
