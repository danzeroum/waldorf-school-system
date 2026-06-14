import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpErrorResponse
} from '@angular/common/http';
import { Observable, throwError, BehaviorSubject } from 'rxjs';
import { catchError, filter, switchMap, take } from 'rxjs/operators';
import { AuthService } from './auth.service';
import { Router } from '@angular/router';

/**
 * Autenticação baseada em cookie HttpOnly: o token não é lido em JS. Cada requisição é
 * enviada com `withCredentials` para que o navegador anexe o cookie de sessão. Em 401,
 * tenta renovar (refresh via cookie) e repete a requisição original.
 */
@Injectable()
export class JwtInterceptor implements HttpInterceptor {

  private isRefreshing = false;
  private refreshDone$ = new BehaviorSubject<boolean>(false);

  constructor(private auth: AuthService, private router: Router) {}

  intercept(req: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    if (req.url.includes('/auth/login') || req.url.includes('/auth/refresh')) {
      return next.handle(req);
    }

    const authReq = req.clone({ withCredentials: true });

    return next.handle(authReq).pipe(
      catchError((err: HttpErrorResponse) => {
        if (err.status === 401) {
          return this.handle401(authReq, next);
        }
        return throwError(() => err);
      })
    );
  }

  private handle401(req: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    if (!this.isRefreshing) {
      this.isRefreshing = true;
      this.refreshDone$.next(false);

      return this.auth.refreshToken().pipe(
        switchMap(() => {
          this.isRefreshing = false;
          this.refreshDone$.next(true);
          return next.handle(req.clone({ withCredentials: true }));
        }),
        catchError(err => {
          this.isRefreshing = false;
          this.auth.logout();
          this.router.navigate(['/auth/login']);
          return throwError(() => err);
        })
      );
    }

    return this.refreshDone$.pipe(
      filter(done => done),
      take(1),
      switchMap(() => next.handle(req.clone({ withCredentials: true })))
    );
  }
}
