import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, Router } from '@angular/router';
import { AuthService } from './auth.service';

@Injectable({ providedIn: 'root' })
export class RoleGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): boolean {
    const required: string[] = route.data['roles'] || [];
    if (required.length === 0) return true;
    const hasRole = required.some(r => this.authService.hasRole(r));
    if (!hasRole) {
      this.router.navigate(['/dashboard']);
    }
    return hasRole;
  }
}
