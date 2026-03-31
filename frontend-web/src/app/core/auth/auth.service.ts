import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { jwtDecode } from 'jwt-decode';

// ─── DTOs alinhados com LoginRequestDTO / LoginResponseDTO do backend ───────
export interface LoginRequest {
  username: string;    // backend espera 'username' (nao 'email')
  password: string;
  deviceId?:   string;
  deviceType?: string; // WEB, MOBILE
}

export interface UsuarioResumo {
  id:           number;
  username:     string;
  email:        string;
  nomeCompleto: string;
  primaryRole:  string;
  roles:        string[]; // backend retorna 'roles' (nao 'perfis')
}

export interface LoginResponse {
  success:          boolean;
  accessToken:      string;
  refreshToken:     string;
  expiresIn:        number;
  refreshExpiresIn: number;
  user:             UsuarioResumo; // backend retorna 'user' (nao 'usuario')
}

export interface RefreshTokenRequest {
  refreshToken: string;
}

export interface MeResponse {
  id:           number;
  username:     string;
  email:        string;
  nomeCompleto: string;
  primaryRole:  string;
  roles:        string[];
  permissions:  string[];
}

@Injectable({ providedIn: 'root' })
export class AuthService {

  private readonly base = `${environment.apiUrl}/auth`;
  private _usuario$ = new BehaviorSubject<UsuarioResumo | null>(this.getUsuario());

  /** Observable para componentes reagirem a login/logout */
  readonly usuario$ = this._usuario$.asObservable();

  constructor(private http: HttpClient) {}

  // ── Login ──────────────────────────────────────────────────────────────────
  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.base}/login`, credentials).pipe(
      tap(res => {
        localStorage.setItem('access_token',  res.accessToken);
        localStorage.setItem('refresh_token', res.refreshToken);
        localStorage.setItem('usuario',       JSON.stringify(res.user));
        this._usuario$.next(res.user);
      })
    );
  }

  // ── Refresh Token ──────────────────────────────────────────────────────────
  refreshToken(): Observable<LoginResponse> {
    const token = localStorage.getItem('refresh_token') ?? '';
    const body: RefreshTokenRequest = { refreshToken: token };
    return this.http.post<LoginResponse>(`${this.base}/refresh`, body).pipe(
      tap(res => {
        localStorage.setItem('access_token',  res.accessToken);
        localStorage.setItem('refresh_token', res.refreshToken);
      })
    );
  }

  // ── GET /me ────────────────────────────────────────────────────────────────
  getMe(): Observable<MeResponse> {
    return this.http.get<MeResponse>(`${this.base}/me`);
  }

  // ── Logout ─────────────────────────────────────────────────────────────────
  logout(): void {
    // Notifica o backend (best-effort)
    this.http.post(`${this.base}/logout`, {}).subscribe({ error: () => {} });
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
    // Aceita tanto 'ROLE_ADMIN' quanto 'ADMIN'
    return usuario.roles.some(r => r === role || r === `ROLE_${role}`);
  }

  isAdmin():       boolean { return this.hasRole('ADMIN'); }
  isCoordenador(): boolean { return this.hasRole('COORDENADOR'); }
  isProfessor():   boolean { return this.hasRole('PROFESSOR'); }
  isResponsavel(): boolean { return this.hasRole('RESPONSAVEL'); }
}
