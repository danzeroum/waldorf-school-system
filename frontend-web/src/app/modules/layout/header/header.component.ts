import { Component, Input, Output, EventEmitter, signal, OnInit, OnDestroy } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';
import { filter, Subscription } from 'rxjs';

interface Breadcrumb {
  label: string;
  route?: string;
}

@Component({
  selector: 'wld-header',
  templateUrl: './header.component.html',
  standalone: false,
})
export class HeaderComponent implements OnInit, OnDestroy {
  @Input() sidebarAberta = true;
  @Output() toggleSidebar = new EventEmitter<void>();

  menuUsuarioAberto = signal(false);
  breadcrumbs = signal<Breadcrumb[]>([]);
  buscaAberta = signal(false);
  termoBusca = signal('');

  private routerSub?: Subscription;

  // Mapa de rotas → labels para breadcrumbs
  private readonly rotaLabels: Record<string, string> = {
    'dashboard': 'Dashboard',
    'pessoas': 'Pessoas',
    'alunos': 'Alunos',
    'novo': 'Novo',
    'editar': 'Editar',
    'professores': 'Professores',
    'pedagogia': 'Pedagogia',
    'turmas': 'Turmas',
    'epocas': 'Épocas',
    'nova': 'Nova',
    'observacoes': 'Observações',
    'financeiro': 'Financeiro',
    'contratos': 'Contratos',
    'parcelas': 'Parcelas',
    'comunidade': 'Comunidade',
    'comunicados': 'Comunicados',
    'portal-pais': 'Portal dos Pais',
    'lgpd': 'LGPD',
    'solicitacoes': 'Solicitações',
    'relatorio': 'Relatório',
    'notificacoes': 'Notificações',
    'preferencias': 'Preferências',
  };

  constructor(
    readonly authService: AuthService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.atualizarBreadcrumbs(this.router.url);
    this.routerSub = this.router.events.pipe(
      filter(e => e instanceof NavigationEnd)
    ).subscribe((e: any) => {
      this.atualizarBreadcrumbs(e.urlAfterRedirects || e.url);
    });
  }

  ngOnDestroy(): void {
    this.routerSub?.unsubscribe();
  }

  toggle(): void {
    this.toggleSidebar.emit();
  }

  toggleMenuUsuario(): void {
    this.menuUsuarioAberto.update(v => !v);
  }

  fecharMenu(): void {
    this.menuUsuarioAberto.set(false);
  }

  logout(): void {
    this.menuUsuarioAberto.set(false);
    this.authService.logout();
  }

  onBusca(termo: string): void {
    this.termoBusca.set(termo);
    // TODO: integrar com serviço de busca global
  }

  private atualizarBreadcrumbs(url: string): void {
    const segmentos = url.split('/').filter(s => s && !s.match(/^\d+$/));
    const crumbs: Breadcrumb[] = [];
    let rotaAcumulada = '';

    for (const seg of segmentos) {
      rotaAcumulada += '/' + seg;
      const label = this.rotaLabels[seg] || seg.charAt(0).toUpperCase() + seg.slice(1);
      crumbs.push({ label, route: rotaAcumulada });
    }

    this.breadcrumbs.set(crumbs);
  }
}
