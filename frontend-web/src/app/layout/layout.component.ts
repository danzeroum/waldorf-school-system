import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../core/auth/auth.service';

@Component({
  selector: 'app-layout',
  templateUrl: './layout.component.html',
})
export class LayoutComponent {
  usuario = this.authService.getUsuario();

  navItems = [
    { label: 'Dashboard',    icon: 'dashboard',  route: '/dashboard'  },
    { label: 'Pessoas',      icon: 'people',     route: '/pessoas'    },
    { label: 'Pedagógico',   icon: 'school',     route: '/pedagogia'  },
    { label: 'Financeiro',   icon: 'payments',   route: '/financeiro' },
    { label: 'Comunidade',   icon: 'forum',      route: '/comunidade' },
    { label: 'LGPD',         icon: 'security',   route: '/lgpd'       },
    { label: 'Notificações', icon: 'notifications', route: '/notificacoes' },
  ];

  constructor(private authService: AuthService, private router: Router) {}

  logout() {
    this.authService.logout();
    this.router.navigate(['/auth/login']);
  }
}
