import { Injectable } from '@angular/core';
import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor } from '@angular/common/http';
import { Observable, finalize } from 'rxjs';
import { LoadingService } from '@shared/services/loading.service';

@Injectable()
export class LoadingInterceptor implements HttpInterceptor {
  private readonly SKIP_URLS = ['/notifications/user/me/unread-count'];

  constructor(private loadingService: LoadingService) {}

  intercept(req: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    const skip = this.SKIP_URLS.some(u => req.url.includes(u));
    if (skip) return next.handle(req);
    this.loadingService.iniciar();
    return next.handle(req).pipe(finalize(() => this.loadingService.encerrar()));
  }
}
