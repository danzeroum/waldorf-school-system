import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpErrorResponse,
} from '@angular/common/http';
import { Observable, throwError, BehaviorSubject, switchMap, filter, take, catchError } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { environment } from '@environments/environment';

@Injectable()
export class TokenInterceptor implements HttpInterceptor {
  private refreshTokenSubject = new BehaviorSubject<string | null>(null);

  constructor(private authService: AuthService) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    // Não adicionar token em rotas públicas
    if (this.isPublicRoute(request.url)) {
      return next.handle(request);
    }

    const token = this.authService.getAccessToken();
    if (token) {
      request = this.addToken(request, token);
    }

    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401) {
          return this.handle401(request, next);
        }
        return throwError(() => error);
      })
    );
  }

  private handle401(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    if (this.authService.isRefreshing$()) {
      this.authService.setRefreshing(true);
      this.refreshTokenSubject.next(null);

      return this.authService.refreshToken().pipe(
        switchMap(response => {
          this.authService.setRefreshing(false);
          this.refreshTokenSubject.next(response.accessToken);
          return next.handle(this.addToken(request, response.accessToken));
        }),
        catchError(err => {
          this.authService.setRefreshing(false);
          this.authService.logout();
          return throwError(() => err);
        })
      );
    }

    // Aguardar o refresh em andamento
    return this.refreshTokenSubject.pipe(
      filter(token => token !== null),
      take(1),
      switchMap(token => next.handle(this.addToken(request, token!)))
    );
  }

  private addToken(request: HttpRequest<unknown>, token: string): HttpRequest<unknown> {
    return request.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json',
        'Accept-Language': 'pt-BR',
      },
    });
  }

  private isPublicRoute(url: string): boolean {
    const publicRoutes = [
      '/auth/login',
      '/auth/refresh',
      '/auth/activate',
      '/auth/forgot-password',
    ];
    return publicRoutes.some(route => url.includes(route));
  }
}
