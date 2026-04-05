import { Component, Input, computed } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { TipoPerfil } from '../../../shared/models/auth.models';

interface NavItem {
  label: string;
  icon: string;
  route: string;
  roles: string[];
  children?: NavItem[];
}

@Component({
  selector: 'wld-sidebar',
  templateUrl: './sidebar.component.html',
  standalone: false,
})
export class SidebarComponent {
  @Input() collapsed = false;

  readonly navItems: NavItem[] = [
    {
      label: 'Início',
      icon: 'home',
      route: '/dashboard',
      roles: [],
    },
    {
      label: 'Alunos',
      icon: 'school',
      route: '/pessoas/alunos',
      roles: [TipoPerfil.ADMIN, TipoPerfil.SECRETARIA],
    },
    {
      label: 'Professores',
      icon: 'person',
      route: '/pessoas/professores',
      roles: [TipoPerfil.ADMIN, TipoPerfil.SECRETARIA],
    },
    {
      label: 'Turmas',
      icon: 'groups',
      route: '/gestao/turmas',
      roles: [TipoPerfil.ADMIN, TipoPerfil.SECRETARIA, TipoPerfil.DIRECAO],
    },
    {
      label: 'Matrículas',
      icon: 'assignment',
      route: '/gestao/matriculas',
      roles: [TipoPerfil.ADMIN, TipoPerfil.SECRETARIA],
    },
    {
      label: 'Observações',
      icon: 'visibility',
      route: '/pedagogia/observacoes',
      roles: [TipoPerfil.ADMIN, TipoPerfil.PROFESSOR, TipoPerfil.DIRECAO],
    },
    {
      label: 'Épocas',
      icon: 'auto_stories',
      route: '/pedagogia/epocas',
      roles: [TipoPerfil.ADMIN, TipoPerfil.PROFESSOR, TipoPerfil.DIRECAO],
    },
    {
      label: 'Relatórios',
      icon: 'description',
      route: '/pedagogia/relatorios',
      roles: [TipoPerfil.ADMIN, TipoPerfil.PROFESSOR, TipoPerfil.DIRECAO],
    },
    {
      label: 'Financeiro',
      icon: 'payments',
      route: '/financeiro',
      roles: [TipoPerfil.ADMIN, TipoPerfil.SECRETARIA],
    },
    {
      label: 'Comunidade',
      icon: 'forum',
      route: '/comunidade',
      roles: [],
    },
    {
      label: 'LGPD',
      icon: 'policy',
      route: '/lgpd',
      roles: [TipoPerfil.ADMIN, TipoPerfil.SECRETARIA],
    },
  ];

  constructor(
    readonly authService: AuthService,
    readonly router: Router,
  ) {}

  itensFiltrados = computed(() => {
    return this.navItems.filter(item =>
      item.roles.length === 0 || this.authService.temAlgumPerfil(item.roles)
    );
  });

  isActive(route: string): boolean {
    return this.router.url.startsWith(route);
  }
}
