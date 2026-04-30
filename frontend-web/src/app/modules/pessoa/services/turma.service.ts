import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from '@environments/environment';

export interface TurmaDTO {
  id: number;
  nome: string;
  anoLetivo: number;
  anoEscolar: number;
  capacidadeMaxima: number;
  professorRegenteId: number | null;
  professorRegenteNome: string | null;
  totalAlunos: number;
  ativa: boolean;
}

@Injectable({ providedIn: 'root' })
export class TurmaService {
  private readonly base = environment.apiUrl + '/turmas';

  constructor(private http: HttpClient) {}

  /** Retorna lista simples de turmas (backend não usa paginação neste endpoint) */
  listar(anoLetivo?: number): Observable<TurmaDTO[]> {
    let params = new HttpParams();
    if (anoLetivo) {
      params = params.set('anoLetivo', String(anoLetivo));
    }
    return this.http.get<TurmaDTO[]>(this.base, { params }).pipe(
      catchError(() => of([]))
    );
  }
}
