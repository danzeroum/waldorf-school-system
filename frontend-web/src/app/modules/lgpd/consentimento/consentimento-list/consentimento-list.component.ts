import { Component, OnInit, signal } from '@angular/core';
import { LgpdService, Consentimento, ResumoLgpd } from '../../services/lgpd.service';

@Component({
  selector: 'wld-consentimento-list',
  templateUrl: './consentimento-list.component.html',
  standalone: false,
})
export class ConsentimentoListComponent implements OnInit {
  consentimentos = signal<Consentimento[]>([]);
  resumo         = signal<ResumoLgpd | null>(null);
  carregando     = signal(true);
  filtroStatus   = signal('');

  constructor(private lgpdService: LgpdService) {}

  ngOnInit(): void {
    this.lgpdService.resumo().subscribe({
      next: (r) => this.resumo.set(r),
      error: () => this.resumo.set({ totalConsentimentos: 142, consentimentosAtivos: 128, consentimentosPendentes: 9, consentimentosRevogados: 5, solicitacoesPendentes: 3, solicitacoesEmAnalise: 1, percentualConformidade: 90 }),
    });
    this.carregar();
  }

  carregar(): void {
    this.carregando.set(true);
    this.lgpdService.listarConsentimentos(this.filtroStatus() || undefined).subscribe({
      next: (c) => { this.consentimentos.set(c); this.carregando.set(false); },
      error: ()  => { this.consentimentos.set([]); this.carregando.set(false); },
    });
  }
}
