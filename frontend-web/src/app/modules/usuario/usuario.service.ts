import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

export interface UsuarioItem {
  id: number;
  nome: string;
  email: string;
  ativo: boolean;
  perfis: string[];
  createdAt: string;
}

export interface UsuarioRequest {
  nome: string;
  email: string;
  senha?: string;
  perfis: string[];
  ativo?: boolean;
}

@Injectable({ providedIn: 'root' })
export class UsuarioService {
  private readonly base = environment.apiUrl + '/usuarios';
  constructor(private http: HttpClient) {}
  listar(): Observable<UsuarioItem[]> { return this.http.get<UsuarioItem[]>(this.base); }
  buscarPorId(id: number): Observable<UsuarioItem> { return this.http.get<UsuarioItem>(this.base + '/' + id); }
  criar(dto: UsuarioRequest): Observable<UsuarioItem> { return this.http.post<UsuarioItem>(this.base, dto); }
  atualizar(id: number, dto: UsuarioRequest): Observable<UsuarioItem> { return this.http.put<UsuarioItem>(this.base + '/' + id, dto); }
  toggleAtivo(id: number): Observable<UsuarioItem> { return this.http.patch<UsuarioItem>(this.base + '/' + id + '/toggle-ativo', {}); }
  resetarSenha(id: number, novaSenha: string): Observable<{mensagem: string}> { return this.http.post<{mensagem: string}>(this.base + '/' + id + '/resetar-senha', {novaSenha: novaSenha}); }
  deletar(id: number): Observable<void> { return this.http.delete<void>(this.base + '/' + id); }
  listarPerfis(): Observable<string[]> { return this.http.get<string[]>(this.base + '/perfis'); }
}
