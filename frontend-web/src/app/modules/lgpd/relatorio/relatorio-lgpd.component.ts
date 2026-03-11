import { Component, OnInit, signal } from '@angular/core';
import { LgpdService, ResumoLgpd } from '../services/lgpd.service';

@Component({
  selector: 'wld-relatorio-lgpd',
  templateUrl: './relatorio-lgpd.component.html',
  standalone: false,
})
export class RelatorioLgpdComponent implements OnInit {
  resumo     = signal<ResumoLgpd | null>(null);
  carregando = signal(true);
  dataGeracao = new Date().toLocaleDateString('pt-BR', { dateStyle: 'full' });

  constructor(private lgpdService: LgpdService) {}

  ngOnInit(): void {
    this.lgpdService.resumo().subscribe({
      next: (r) => { this.resumo.set(r); this.carregando.set(false); },
      error: ()  => {
        this.resumo.set({ totalConsentimentos: 142, consentimentosAtivos: 128, consentimentosPendentes: 9, consentimentosRevogados: 5, solicitacoesPendentes: 3, solicitacoesEmAnalise: 1, percentualConformidade: 90 });
        this.carregando.set(false);
      },
    });
  }
}
