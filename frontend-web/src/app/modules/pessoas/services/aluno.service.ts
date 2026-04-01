import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

export type Genero = 'MASCULINO' | 'FEMININO' | 'OUTRO' | 'NAO_INFORMADO';

export interface Aluno {
  id:              number;
  matricula:       string;
  nome:            string;
  dataNascimento:  string;
  genero:          Genero;
  email:           string;
  anoIngresso:     number;
  turmaNome:       string;
  temperamento:    string;
  ativo:           boolean;
  createdAt:       string;
  updatedAt:       string;
}

export interface AlunoRequest {
  nome:            string;
  dataNascimento:  string;
  genero:          Genero;
  email:           string;
  telefone:        string;
  turmaId:         number | null;
  anoIngresso:     number;
  temperamento:    string;
  enderecoRua:     string;
  enderecoNumero:  string;
  enderecoBairro:  string;
  enderecoCidade:  string;
  enderecoEstado:  string;
  enderecoCep:     string;
  observacoes:     string;
}

export interface PageAluno {
  content:       Aluno[];
  totalElements: number;
  totalPages:    number;
  number:        number;
  size:          number;
}

@Injectable({ providedIn: 'root' })
export class AlunoService {
  private readonly base = `${environment.apiUrl}/alunos`;

  constructor(private http: HttpClient) {}

  listar(params?: {
    nome?:    string;
    turmaId?: number;
    ativo?:   boolean;
    page?:    number;
    size?:    number;
  }): Observable<PageAluno> {
    let p = new HttpParams();
    if (params?.nome)            p = p.set('nome',    params.nome);
    if (params?.turmaId != null) p = p.set('turmaId', params.turmaId);
    if (params?.ativo   != null) p = p.set('ativo',   String(params.ativo));
    if (params?.page    != null) p = p.set('page',    params.page);
    if (params?.size    != null) p = p.set('size',    params.size);
    return this.http.get<PageAluno>(this.base, { params: p });
  }

  buscarPorId(id: number): Observable<Aluno> {
    return this.http.get<Aluno>(`${this.base}/${id}`);
  }

  criar(dto: AlunoRequest): Observable<Aluno> {
    return this.http.post<Aluno>(this.base, dto);
  }

  atualizar(id: number, dto: AlunoRequest): Observable<Aluno> {
    return this.http.put<Aluno>(`${this.base}/${id}`, dto);
  }

  inativar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }
}
