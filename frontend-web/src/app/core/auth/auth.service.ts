import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { jwtDecode } from 'jwt-decode';

export interface LoginRequest  { email: string; password: string; }
export interface LoginResponse { accessToken: string; refreshToken: string; usuario: UsuarioInfo; }
export interface UsuarioInfo   { id: number; nome: string; email: string; perfis: string[]; }

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly base = `${environment.apiUrl}/auth`;

  constructor(private http: HttpClient) {}

  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.base}/login`, credentials).pipe(
      tap(res => {
        localStorage.setItem('access_token',  res.accessToken);
        localStorage.setItem('refresh_token', res.refreshToken);
        localStorage.setItem('usuario', JSON.stringify(res.usuario));
      })
    );
  }

  logout(): void {
    localStorage.removeItem('access_token');
    localStorage.removeItem('refresh_token');
    localStorage.removeItem('usuario');
  }

  isAuthenticated(): boolean {
    const token = localStorage.getItem('access_token');
    if (!token) return false;
    try {
      const decoded: any = jwtDecode(token);
      return decoded.exp * 1000 > Date.now();
    } catch { return false; }
  }

  getUsuario(): UsuarioInfo | null {
    const u = localStorage.getItem('usuario');
    return u ? JSON.parse(u) : null;
  }

  hasRole(role: string): boolean {
    return this.getUsuario()?.perfis.includes(role) ?? false;
  }
}
