import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from '@environments/environment';
import { TurmaResumo, PageResponse } from '@models/pessoa.models';

@Injectable({ providedIn: 'root' })
export class TurmaService {
  private readonly base = environment.apiUrl + '/turmas';

  constructor(private http: HttpClient) {}

  listar(anoLetivo?: number): Observable<PageResponse<TurmaResumo>> {
    let params = new HttpParams().set('size', '100');
    if (anoLetivo) {
      params = params.set('anoLetivo', String(anoLetivo));
    }
    return this.http.get<PageResponse<TurmaResumo>>(this.base, { params }).pipe(
      catchError(() => of({ content: [], totalElements: 0, totalPages: 0, size: 0, number: 0, first: true, last: true, empty: true }))
    );
  }
}
