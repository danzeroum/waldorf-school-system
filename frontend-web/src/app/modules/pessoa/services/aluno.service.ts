import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '@environments/environment';

export interface AlunoFiltros {
  nome?: string;
  turmaId?: number;
  situacao?: string;
  anoLetivo?: number;
}

@Injectable({ providedIn: 'root' })
export class AlunoService {
  private readonly base = environment.apiUrl + '/alunos';

  constructor(private http: HttpClient) {}

  private formatarDataISO(dateStr: string): string {
    if (!dateStr) return '';
    const parts = dateStr.split('/');
    if (parts.length === 3) {
      return parts[2] + '-' + parts[1] + '-' + parts[0];
    }
    return dateStr;
  }

  private mapearParaBackend(dados: any): any {
    return {
      nome: dados.nomeCompleto || dados.nome || '',
      dataNascimento: this.formatarDataISO(dados.dataNascimento),
      genero: dados.genero || 'NAO_INFORMADO',
      anoIngresso: dados.anoIngresso || dados.anoLetivo || new Date().getFullYear(),
      email: dados.email || null,
      telefone: dados.telefone || null,
      temperamento: dados.temperamento || null,
      turmaId: dados.turmaId || null,
      enderecoRua: dados.logradouro || dados.enderecoRua || null,
      enderecoNumero: dados.numero || dados.enderecoNumero || null,
      enderecoBairro: dados.bairro || dados.enderecoBairro || null,
      enderecoCidade: dados.cidade || dados.enderecoCidade || null,
      enderecoEstado: dados.estado || dados.enderecoEstado || null,
      enderecoCep: dados.cep || dados.enderecoCep || null,
      observacoes: dados.observacoesMedicas || dados.observacoes || null,
    };
  }

  listar(filtros?: AlunoFiltros, page?: any): Observable<any> {
    let p = new HttpParams();
    if (filtros?.nome)      p = p.set('nome', filtros.nome);
    if (filtros?.turmaId)   p = p.set('turmaId', String(filtros.turmaId));
    if (filtros?.situacao)  p = p.set('situacao', filtros.situacao);
    if (filtros?.anoLetivo) p = p.set('anoLetivo', String(filtros.anoLetivo));
    if (page?.page)         p = p.set('page', String(page.page));
    if (page?.size)         p = p.set('size', String(page.size));
    if (page?.sort)         p = p.set('sort', page.sort);
    return this.http.get(this.base, { params: p });
  }

  buscarPorId(id: number): Observable<any> {
    return this.http.get(this.base + '/' + id);
  }

  criar(dados: any): Observable<any> {
    return this.http.post(this.base, this.mapearParaBackend(dados));
  }

  atualizar(id: number, dados: any): Observable<any> {
    return this.http.put(this.base + '/' + id, this.mapearParaBackend(dados));
  }

  inativar(id: number): Observable<void> {
    return this.http.delete<void>(this.base + '/' + id);
  }

  matricular(dados: any): Observable<any> {
    if (!dados.alunoId) throw new Error('alunoId obrigatorio');
    return this.http.put(this.base + '/' + dados.alunoId, {
      turmaId: dados.turmaId || null,
      anoIngresso: dados.anoLetivo || new Date().getFullYear(),
    });
  }

  adicionarResponsavel(alunoId: number, responsavel: any): Observable<any> {
    console.warn('Endpoint de responsaveis nao disponivel');
    return new Observable();
  }
}
