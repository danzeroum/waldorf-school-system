import { Component, Input, signal, computed } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';
import { TipoPerfil } from '../../../shared/models/auth.models';

export interface NavItem {
  label: string;
  icon: string;
  route: string;
  roles: string[];
  children?: NavItem[];
  badge?: number;
}

export interface NavSection {
  label: string;
  items: NavItem[];
}

@Component({
  selector: 'wld-sidebar',
  templateUrl: './sidebar.component.html',
  standalone: false,
})
export class SidebarComponent {
  @Input() collapsed = false;

  // Controla quais grupos estão expandidos
  gruposAbertos = signal<Record<string, boolean>>({});

  readonly sections: NavSection[] = [
    {
      label: 'Principal',
      items: [
        {
          label: 'Dashboard',
          icon: 'dashboard',
          route: '/dashboard',
          roles: [],
        },
      ],
    },
    {
      label: 'Gestão',
      items: [
        {
          label: 'Pessoas',
          icon: 'people',
          route: '/pessoas',
          roles: [TipoPerfil.ADMIN, TipoPerfil.SECRETARIA, TipoPerfil.DIRETOR],
          children: [
            { label: 'Alunos', icon: 'school', route: '/pessoas/alunos', roles: [] },
            { label: 'Professores', icon: 'person', route: '/pessoas/professores', roles: [] },
          ],
        },
        {
          label: 'Pedagogia',
          icon: 'auto_stories',
          route: '/pedagogia',
          roles: [TipoPerfil.ADMIN, TipoPerfil.PROFESSOR, TipoPerfil.DIRETOR],
          children: [
            { label: 'Turmas', icon: 'groups', route: '/pedagogia/turmas', roles: [] },
            { label: 'Épocas', icon: 'event_note', route: '/pedagogia/epocas', roles: [] },
            { label: 'Observações', icon: 'visibility', route: '/pedagogia/observacoes', roles: [] },
          ],
        },
        {
          label: 'Financeiro',
          icon: 'payments',
          route: '/financeiro',
          roles: [TipoPerfil.ADMIN, TipoPerfil.SECRETARIA],
    },
    {      label: 'Usuarios',      icon: 'admin_panel_settings',      route: '/usuarios',      roles: [TipoPerfil.ADMIN],
          children: [
            { label: 'Dashboard', icon: 'bar_chart', route: '/financeiro', roles: [] },
            { label: 'Contratos', icon: 'description', route: '/financeiro/contratos', roles: [] },
            { label: 'Parcelas', icon: 'receipt_long', route: '/financeiro/parcelas', roles: [] },
          ],
        },
      ],
    },
    {
      label: 'Comunicação',
      items: [
        {
          label: 'Comunidade',
          icon: 'forum',
          route: '/comunidade',
          roles: [],
          children: [
            { label: 'Mural', icon: 'dashboard_customize', route: '/comunidade', roles: [] },
            { label: 'Comunicados', icon: 'campaign', route: '/comunidade/comunicados', roles: [] },
            { label: 'Portal dos Pais', icon: 'family_restroom', route: '/comunidade/portal-pais', roles: [] },
          ],
        },
        {
          label: 'Notificações',
          icon: 'notifications',
          route: '/notificacoes',
          roles: [],
          badge: 3,
        },
      ],
    },
    {
      label: 'Compliance',
      items: [
        {
          label: 'LGPD',
          icon: 'policy',
          route: '/lgpd',
          roles: [TipoPerfil.ADMIN, TipoPerfil.SECRETARIA],
          children: [
            { label: 'Consentimentos', icon: 'verified_user', route: '/lgpd', roles: [] },
            { label: 'Solicitações', icon: 'assignment_late', route: '/lgpd/solicitacoes', roles: [] },
            { label: 'Relatório', icon: 'assessment', route: '/lgpd/relatorio', roles: [] },
          ],
        },
      ],
    },
  ];

  constructor(
    readonly authService: AuthService,
    readonly router: Router,
  ) {}

  /**
   * Filtra seções e itens com base nos perfis do usuário
   */
  secoesFiltradas = computed(() => {
    return this.sections
      .map(section => ({
        ...section,
        items: section.items.filter(item =>
          item.roles.length === 0 || this.authService.temAlgumPerfil(item.roles)
        ),
      }))
      .filter(section => section.items.length > 0);
  });

  /**
   * Verifica se a rota está ativa (exata ou prefixada)
   */
  isActive(route: string): boolean {
    return this.router.url === route || this.router.url.startsWith(route + '/');
  }

  /**
   * Verifica se algum filho está ativo (para destacar o grupo pai)
   */
  isGroupActive(item: NavItem): boolean {
    if (!item.children) return this.isActive(item.route);
    return item.children.some(child => this.isActive(child.route));
  }

  /**
   * Verifica se um grupo está expandido
   */
  isGroupOpen(label: string): boolean {
    return this.gruposAbertos()[label] ?? false;
  }

  /**
   * Alterna a expansão de um grupo
   */
  toggleGroup(item: NavItem): void {
    if (!item.children || item.children.length === 0) {
      // Item sem filhos: navegar diretamente
      this.router.navigate([item.route]);
      return;
    }

    this.gruposAbertos.update(groups => ({
      ...groups,
      [item.label]: !groups[item.label],
    }));
  }

  /**
   * Navega para uma rota de um item filho
   */
  navigateTo(route: string): void {
    this.router.navigate([route]);
  }

  /**
   * Auto-expande grupos que contêm a rota ativa (ao carregar)
   */
  ngOnInit(): void {
    this.autoExpandActiveGroup();
  }

  private autoExpandActiveGroup(): void {
    const updates: Record<string, boolean> = {};
    for (const section of this.sections) {
      for (const item of section.items) {
        if (item.children && item.children.some(c => this.isActive(c.route))) {
          updates[item.label] = true;
        }
      }
    }
    if (Object.keys(updates).length > 0) {
      this.gruposAbertos.update(g => ({ ...g, ...updates }));
    }
  }
}
