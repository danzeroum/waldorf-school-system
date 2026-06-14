import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { jwtDecode } from 'jwt-decode';

// ─── DTOs ────────────────────────────────────────────────────────────────────
export interface LoginRequest {
  email: string;
  password: string;
  deviceId?:   string;
  deviceType?: string;
}

export interface UsuarioResumo {
  id:           number;
  username:     string;
  email:        string;
  nome:         string;
  nomeCompleto: string;
  primaryRole:  string;
  roles:        string[];
  perfis:       string[];
}

export interface LoginResponse {
  accessToken:  string;
  refreshToken: string;
  usuario:      { id: number; nome: string; email: string; perfis: string[] };
}

export interface RefreshTokenRequest {
  refreshToken: string;
}

export interface MeResponse {
  id:           number;
  email:        string;
  nomeCompleto: string;
  roles:        string[];
  permissions:  string[];
}

@Injectable({ providedIn: 'root' })
export class AuthService {

  private readonly base = `${environment.apiUrl}/auth`;
  private _usuario$ = new BehaviorSubject<UsuarioResumo | null>(this.loadUsuario());

  readonly usuario$ = this._usuario$.asObservable();

  constructor(private http: HttpClient) {}

  // ── Login ──────────────────────────────────────────────────────────────────
  // Os tokens trafegam em cookies HttpOnly (definidos pelo backend); o JS nunca os
  // persiste. Guardamos apenas o perfil e o instante de expiração (não sensível) para
  // os guards de rota. `withCredentials` é obrigatório para receber/enviar os cookies.
  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.base}/login`, credentials, { withCredentials: true }).pipe(
      tap(res => {
        const normalized = this.normalizeUsuario(res.usuario);
        this.persistSession(res.accessToken, normalized);
        this._usuario$.next(normalized);
      })
    );
  }

  // ── Refresh Token ──────────────────────────────────────────────────────────
  // O refresh token vai no cookie HttpOnly; corpo vazio.
  refreshToken(): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.base}/refresh`, {}, { withCredentials: true }).pipe(
      tap(res => {
        const normalized = this.normalizeUsuario(res.usuario);
        this.persistSession(res.accessToken, normalized);
        this._usuario$.next(normalized);
      })
    );
  }

  // ── GET /me ────────────────────────────────────────────────────────────────
  getMe(): Observable<MeResponse> {
    return this.http.get<MeResponse>(`${this.base}/me`, { withCredentials: true });
  }

  // ── Logout ─────────────────────────────────────────────────────────────────
  logout(): void {
    this.http.post(`${this.base}/logout`, {}, { withCredentials: true }).subscribe({ error: () => {} });
    localStorage.removeItem('token_exp');
    localStorage.removeItem('usuario');
    this._usuario$.next(null);
  }

  // ── Helpers ────────────────────────────────────────────────────────────────
  isAuthenticated(): boolean {
    const exp = localStorage.getItem('token_exp');
    if (!exp) return false;
    return Number(exp) > Date.now();
  }

  /** Persiste o perfil e a expiração; o token em si fica no cookie HttpOnly. */
  private persistSession(accessToken: string, usuario: UsuarioResumo): void {
    try {
      const decoded: any = jwtDecode(accessToken);
      localStorage.setItem('token_exp', String((decoded?.exp ?? 0) * 1000));
    } catch {
      localStorage.removeItem('token_exp');
    }
    localStorage.setItem('usuario', JSON.stringify(usuario));
  }

  /** @deprecated Tokens agora ficam em cookies HttpOnly e não são acessíveis via JS. */
  getAccessToken(): string | null {
    return null;
  }

  /** @deprecated O refresh token fica em cookie HttpOnly. */
  getRefreshTokenValue(): string | null {
    return null;
  }

  getUsuario(): UsuarioResumo | null {
    const u = localStorage.getItem('usuario');
    return u ? JSON.parse(u) : null;
  }

  hasRole(role: string): boolean {
    const usuario = this.getUsuario();
    if (!usuario) return false;
    const roles = usuario.roles || usuario.perfis || [];
    return roles.some((r: string) => r === role || r === `ROLE_${role}`);
  }

  isAdmin():       boolean { return this.hasRole('ADMIN'); }
  isCoordenador(): boolean { return this.hasRole('COORDENADOR'); }
  isProfessor():   boolean { return this.hasRole('PROFESSOR'); }
  isResponsavel(): boolean { return this.hasRole('RESPONSAVEL'); }

  // ── Privado ────────────────────────────────────────────────────────────────

  private normalizeUsuario(u: any): UsuarioResumo {
    const perfis = u.perfis || u.roles || [];
    return {
      id:           u.id,
      username:     u.username || u.email || '',
      email:        u.email || '',
      nome:         u.nome || u.nomeCompleto || '',
      nomeCompleto: u.nomeCompleto || u.nome || '',
      primaryRole:  u.primaryRole || perfis[0] || '',
      roles:        perfis,
      perfis:       perfis,
    };
  }

  usuario(): any { return this._usuario$.value; }
  perfisUsuario(): string[] { const u = this.getUsuario(); return u ? (u.roles || u.perfis || []) : []; }
  temAlgumPerfil(perfis: string[]): boolean { if (!perfis.length) return true; return perfis.some(p => this.perfisUsuario().includes(p)); }
  private loadUsuario(): UsuarioResumo | null {
    try {
      const u = this.getUsuario();
      if (!u) return null;
      if (!u.roles && u.perfis) (u as any).roles = u.perfis;
      if (!u.perfis && u.roles) (u as any).perfis = u.roles;
      if (!u.nomeCompleto && u.nome) (u as any).nomeCompleto = u.nome;
      if (!u.nome && u.nomeCompleto) (u as any).nome = u.nomeCompleto;
      if (!u.username) (u as any).username = u.email || '';
      if (!u.primaryRole) {
        const roles = u.roles || u.perfis || [];
        (u as any).primaryRole = roles[0] || '';
      }
      return u;
    } catch {
      localStorage.removeItem('usuario');
      return null;
    }
  }
}
