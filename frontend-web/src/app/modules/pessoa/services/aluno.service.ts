import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '@environments/environment';
import { Aluno, PageResponse, PageRequest } from '@models/pessoa.models';

export interface AlunoFiltros {
  nome?: string;
  turmaId?: number;
  situacao?: string;
  anoLetivo?: number;
}

export interface CreateAlunoRequest {
  // Passo 1: Dados pessoais
  nomeCompleto: string;
  nomeSocial?: string;
  dataNascimento: string;
  cpf?: string;
  nomePai?: string;
  nomeMae: string;
  naturalidade?: string;
  nacionalidade?: string;
  // Passo 2: Dados médicos
  tipoSanguineo?: string;
  planoSaude?: string;
  alergias?: string;
  medicamentosControlados?: string;
  necessidadesEspeciais?: string;
  observacoesMedicas?: string;
  // Passo 4: Endereço
  cep?: string;
  logradouro?: string;
  numero?: string;
  complemento?: string;
  bairro?: string;
  cidade?: string;
  estado?: string;
}

export interface CreateMatriculaRequest {
  alunoId: number;
  turmaId: number;
  anoLetivo: number;
  formaIngresso: string;
  aceiteTermosLgpd: boolean;
}

@Injectable({ providedIn: 'root' })
export class AlunoService {
  private readonly api = `${environment.apiUrl}/students`;

  constructor(private http: HttpClient) {}

  listar(filtros: AlunoFiltros, page: PageRequest): Observable<PageResponse<Aluno>> {
    let params = new HttpParams()
      .set('page', page.page)
      .set('size', page.size)
      .set('sort', page.sort ?? 'nomeCompleto')
      .set('direction', page.direction ?? 'ASC');

    if (filtros.nome)      params = params.set('nome', filtros.nome);
    if (filtros.turmaId)   params = params.set('turmaId', filtros.turmaId);
    if (filtros.situacao)  params = params.set('situacao', filtros.situacao);
    if (filtros.anoLetivo) params = params.set('anoLetivo', filtros.anoLetivo);

    return this.http.get<PageResponse<Aluno>>(this.api, { params });
  }

  buscarPorId(id: number): Observable<Aluno> {
    return this.http.get<Aluno>(`${this.api}/${id}`);
  }

  criar(request: CreateAlunoRequest): Observable<Aluno> {
    return this.http.post<Aluno>(`${environment.apiUrl}/pessoas`, request);
  }

  atualizar(id: number, request: Partial<CreateAlunoRequest>): Observable<Aluno> {
    return this.http.put<Aluno>(`${this.api}/${id}`, request);
  }

  matricular(request: CreateMatriculaRequest): Observable<any> {
    return this.http.post(`${environment.apiUrl}/enrollments`, request);
  }

  adicionarResponsavel(alunoId: number, responsavel: any): Observable<any> {
    return this.http.post(`${this.api}/${alunoId}/guardians`, responsavel);
  }

  transferir(id: number, turmaDestinoId: number, motivo: string): Observable<any> {
    return this.http.post(`${this.api}/${id}/transfer`, { turmaDestinoId, motivo });
  }

  desligar(id: number, motivo: string): Observable<any> {
    return this.http.post(`${this.api}/${id}/deactivate`, { motivo });
  }
}
