import { Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap, catchError, throwError, BehaviorSubject } from 'rxjs';
import { environment } from '@environments/environment';
import {
  LoginRequest,
  LoginResponse,
  UsuarioLogado,
  RefreshTokenRequest,
  TipoPerfil,
} from '@models/auth.models';
import { JwtService } from './jwt.service';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly apiUrl = `${environment.apiUrl}/auth`;

  // Estado reativo com Signal (Angular 17)
  private _usuario = signal<UsuarioLogado | null>(null);
  private _carregando = signal<boolean>(false);
  private _refreshing$ = new BehaviorSubject<boolean>(false);

  // Computed signals
  readonly usuario = this._usuario.asReadonly();
  readonly carregando = this._carregando.asReadonly();
  readonly estaAutenticado = computed(() => this._usuario() !== null);
  readonly perfisUsuario = computed(() => this._usuario()?.perfis?.map(p => p.nome) ?? []);

  constructor(
    private http: HttpClient,
    private router: Router,
    private jwtService: JwtService,
  ) {
    this.inicializarSessao();
  }

  // === AUTENTICAÇÃO ===

  login(request: LoginRequest): Observable<LoginResponse> {
    this._carregando.set(true);
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, request).pipe(
      tap(response => {
        this.salvarTokens(response);
        this._usuario.set(response.usuario);
        this._carregando.set(false);
      }),
      catchError(err => {
        this._carregando.set(false);
        return throwError(() => err);
      })
    );
  }

  logout(): void {
    const refreshToken = localStorage.getItem(environment.refreshTokenKey);
    if (refreshToken) {
      this.http.post(`${this.apiUrl}/logout`, { refreshToken }).subscribe();
    }
    this.limparSessao();
    this.router.navigate(['/auth/login']);
  }

  refreshToken(): Observable<LoginResponse> {
    const refreshToken = localStorage.getItem(environment.refreshTokenKey);
    if (!refreshToken) {
      return throwError(() => new Error('Sem refresh token'));
    }
    const request: RefreshTokenRequest = { refreshToken };
    return this.http.post<LoginResponse>(`${this.apiUrl}/refresh`, request).pipe(
      tap(response => {
        this.salvarTokens(response);
        this._usuario.set(response.usuario);
      }),
      catchError(err => {
        this.limparSessao();
        this.router.navigate(['/auth/login']);
        return throwError(() => err);
      })
    );
  }

  // === AUTORIZAÇÃO ===

  temPerfil(perfil: TipoPerfil | string): boolean {
    return this.perfisUsuario().includes(perfil);
  }

  temAlgumPerfil(perfis: (TipoPerfil | string)[]): boolean {
    return perfis.some(p => this.temPerfil(p));
  }

  temPermissao(permissao: string): boolean {
    return this._usuario()?.perfis?.some(
      p => p.permissoes?.includes(permissao)
    ) ?? false;
  }

  // Retorna a rota padrão do usuário conforme seu perfil principal
  rotaInicial(): string {
    if (this.temPerfil(TipoPerfil.ADMIN) || this.temPerfil(TipoPerfil.SECRETARIA)) {
      return '/dashboard/secretaria';
    }
    if (this.temPerfil(TipoPerfil.PROFESSOR)) {
      return '/dashboard/professor';
    }
    if (this.temPerfil(TipoPerfil.DIRECAO)) {
      return '/dashboard/direcao';
    }
    if (this.temPerfil(TipoPerfil.PAIS)) {
      return '/dashboard/pais';
    }
    return '/dashboard';
  }

  // === GETTERS ===

  getAccessToken(): string | null {
    return localStorage.getItem(environment.tokenKey);
  }

  isRefreshing$(): Observable<boolean> {
    return this._refreshing$.asObservable();
  }

  setRefreshing(value: boolean): void {
    this._refreshing$.next(value);
  }

  // === PRIVADOS ===

  private inicializarSessao(): void {
    const token = this.getAccessToken();
    if (!token) return;

    const payload = this.jwtService.decode(token);
    if (!payload || this.jwtService.isExpired(token)) {
      this.limparSessao();
      return;
    }

    // Recarregar dados do usuário a partir do token armazenado
    const usuarioArmazenado = localStorage.getItem('waldorf_usuario');
    if (usuarioArmazenado) {
      try {
        this._usuario.set(JSON.parse(usuarioArmazenado));
      } catch {
        this.limparSessao();
      }
    }
  }

  private salvarTokens(response: LoginResponse): void {
    localStorage.setItem(environment.tokenKey, response.accessToken);
    localStorage.setItem(environment.refreshTokenKey, response.refreshToken);
    localStorage.setItem('waldorf_usuario', JSON.stringify(response.usuario));
  }

  private limparSessao(): void {
    localStorage.removeItem(environment.tokenKey);
    localStorage.removeItem(environment.refreshTokenKey);
    localStorage.removeItem(environment.tokenExpiryKey);
    localStorage.removeItem('waldorf_usuario');
    this._usuario.set(null);
  }
}
