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
  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.base}/login`, credentials).pipe(
      tap(res => {
        const normalized = this.normalizeUsuario(res.usuario);
        localStorage.setItem('access_token',  res.accessToken);
        localStorage.setItem('refresh_token', res.refreshToken);
        localStorage.setItem('usuario',       JSON.stringify(normalized));
        this._usuario$.next(normalized);
      })
    );
  }

  // ── Refresh Token ──────────────────────────────────────────────────────────
  refreshToken(): Observable<LoginResponse> {
    const token = localStorage.getItem('refresh_token') ?? '';
    const body: RefreshTokenRequest = { refreshToken: token };
    return this.http.post<LoginResponse>(`${this.base}/refresh`, body).pipe(
      tap(res => {
        const normalized = this.normalizeUsuario(res.usuario);
        localStorage.setItem('access_token',  res.accessToken);
        localStorage.setItem('refresh_token', res.refreshToken);
        localStorage.setItem('usuario',       JSON.stringify(normalized));
        this._usuario$.next(normalized);
      })
    );
  }

  // ── GET /me ────────────────────────────────────────────────────────────────
  getMe(): Observable<MeResponse> {
    return this.http.get<MeResponse>(`${this.base}/me`);
  }

  // ── Logout ─────────────────────────────────────────────────────────────────
  logout(): void {
    const token = localStorage.getItem('access_token');
    if (token) {
      this.http.post(`${this.base}/logout`, {}).subscribe({ error: () => {} });
    }
    localStorage.removeItem('access_token');
    localStorage.removeItem('refresh_token');
    localStorage.removeItem('usuario');
    this._usuario$.next(null);
  }

  // ── Helpers ────────────────────────────────────────────────────────────────
  isAuthenticated(): boolean {
    const token = localStorage.getItem('access_token');
    if (!token) return false;
    try {
      const decoded: any = jwtDecode(token);
      return decoded.exp * 1000 > Date.now();
    } catch {
      return false;
    }
  }

  getAccessToken(): string | null {
    return localStorage.getItem('access_token');
  }

  getRefreshTokenValue(): string | null {
    return localStorage.getItem('refresh_token');
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
