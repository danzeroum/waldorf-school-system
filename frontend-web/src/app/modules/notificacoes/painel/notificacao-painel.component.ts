import { Component, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';
import { NotificacaoService, Notificacao } from '../services/notificacao.service';

@Component({
  selector: 'wld-notificacao-painel',
  templateUrl: './notificacao-painel.component.html',
  standalone: false,
})
export class NotificacaoPainelComponent implements OnInit {
  notificacoes = signal<Notificacao[]>([]);
  carregando   = signal(true);
  salvando     = signal(false);

  constructor(
    public notificacaoService: NotificacaoService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.notificacaoService.listar().subscribe({
      next: (n) => { this.notificacoes.set(n); this.carregando.set(false); },
      error: ()  => { this.notificacoes.set(this.mockNotificacoes()); this.carregando.set(false); },
    });
  }

  marcarLida(notif: Notificacao): void {
    if (notif.lida) return;
    this.notificacaoService.marcarLida(notif.id).subscribe({
      next: () => this.notificacoes.update(list =>
        list.map(n => n.id === notif.id ? { ...n, lida: true } : n)
      ),
    });
    if (notif.link) this.router.navigateByUrl(notif.link);
  }

  marcarTodas(): void {
    this.salvando.set(true);
    this.notificacaoService.marcarTodasLidas().subscribe({
      next: () => {
        this.notificacoes.update(list => list.map(n => ({ ...n, lida: true })));
        this.salvando.set(false);
      },
      error: () => this.salvando.set(false),
    });
  }

  get naoLidas(): number {
    return this.notificacoes().filter(n => !n.lida).length;
  }

  private mockNotificacoes(): Notificacao[] {
    return [
      { id: 1, tipo: 'MENSALIDADE_VENCIDA',  titulo: 'Parcela vencida',          mensagem: 'A mensalidade de Pedro Santos (03/2026) está vencida há 1 dia.',   lida: false, link: '/financeiro/parcelas?status=VENCIDA', createdAt: '2026-03-11T08:00:00' },
      { id: 2, tipo: 'NOVA_OBSERVACAO',       titulo: 'Nova observação registrada', mensagem: 'Prof. Maria registrou uma observação sobre Ana Clara (Classe 3).', lida: false, link: '/pedagogia/observacoes/1',             createdAt: '2026-03-11T09:15:00' },
      { id: 3, tipo: 'COMUNICADO',            titulo: 'Reunião de pais',            mensagem: 'Novo comunicado: Reunião de pais da Classe 3 — sexta, 19h.',        lida: true,  link: '/comunidade/comunicados',             createdAt: '2026-03-10T14:30:00' },
      { id: 4, tipo: 'EVENTO',                titulo: 'Festival de Artes',          mensagem: 'Lembrete: Festival Anual de Artes nos dias 18 e 19 de abril.',     lida: true,  link: '/comunidade',                         createdAt: '2026-03-10T10:00:00' },
      { id: 5, tipo: 'SOLICITACAO_LGPD',      titulo: 'Solicitação LGPD pendente',  mensagem: '3 solicitações de titulares aguardam resposta há mais de 5 dias.', lida: false, link: '/lgpd/solicitacoes',                   createdAt: '2026-03-09T08:00:00' },
    ];
  }
}
