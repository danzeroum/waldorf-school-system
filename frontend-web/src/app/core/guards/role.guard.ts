import { Injectable } from '@angular/core';
import {
  CanActivate,
  ActivatedRouteSnapshot,
  Router,
} from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({ providedIn: 'root' })
export class RoleGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): boolean {
    const rolesRequeridos: string[] = route.data['roles'] ?? [];

    if (rolesRequeridos.length === 0) return true;

    if (this.authService.temAlgumPerfil(rolesRequeridos)) {
      return true;
    }

    // Sem permissão → redirecionar para dashboard
    this.router.navigate(['/dashboard']);
    return false;
  }
}
