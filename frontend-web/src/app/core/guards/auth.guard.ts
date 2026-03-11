import { Injectable } from '@angular/core';
import {
  CanActivate,
  CanActivateChild,
  ActivatedRouteSnapshot,
  RouterStateSnapshot,
  Router,
} from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate, CanActivateChild {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    return this.verificarAutenticacao(state.url);
  }

  canActivateChild(childRoute: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    return this.verificarAutenticacao(state.url);
  }

  private verificarAutenticacao(urlAttempted: string): boolean {
    if (this.authService.estaAutenticado()) {
      return true;
    }
    // Salvar URL tentada para redirecionar após login
    sessionStorage.setItem('waldorf_return_url', urlAttempted);
    this.router.navigate(['/auth/login']);
    return false;
  }
}
