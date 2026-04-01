import { Injectable } from '@angular/core';
import {
  HttpRequest, HttpHandler, HttpEvent, HttpInterceptor, HttpErrorResponse,
} from '@angular/common/http';
import { Observable, throwError, EMPTY } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Router } from '@angular/router';

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {

  constructor(private router: Router) {}

  intercept(req: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    return next.handle(req).pipe(
      catchError((err: HttpErrorResponse) => {
        // 401 é responsabilidade exclusiva do JwtInterceptor:
        // ele tenta refresh e só faz logout se o refresh também falhar.
        // Tratar 401 aqui causaria logout forçado antes do refresh ser tentado.
        if (err.status === 403) {
          this.router.navigate(['/dashboard']);
          return EMPTY;
        }
        return throwError(() => err);
      }),
    );
  }
}
