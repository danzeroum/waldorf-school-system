import { Component, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';
import { FinanceiroService, ResumoFinanceiro, Parcela } from '../services/financeiro.service';

@Component({
  selector: 'wld-financeiro-dashboard',
  templateUrl: './financeiro-dashboard.component.html',
  standalone: false,
})
export class FinanceiroDashboardComponent implements OnInit {
  resumo         = signal<ResumoFinanceiro | null>(null);
  vencendoHoje   = signal<Parcela[]>([]);
  vencidas       = signal<Parcela[]>([]);
  carregando     = signal(true);
  anoLetivo      = signal(new Date().getFullYear());

  constructor(
    private financeiroService: FinanceiroService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.carregar();
  }

  carregar(): void {
    this.carregando.set(true);

    this.financeiroService.resumo(this.anoLetivo()).subscribe({
      next: (r) => { this.resumo.set(r); this.carregando.set(false); },
      error: () => {
        // Mock para desenvolvimento
        this.resumo.set({
          totalReceita: 284000,
          totalRecebido: 196500,
          totalPendente: 67200,
          totalVencido: 20300,
          totalContratos: 142,
          inadimplentes: 11,
          taxaInadimplencia: 7.7,
        });
        this.carregando.set(false);
      },
    });

    const hoje = new Date().toISOString().split('T')[0];
    this.financeiroService.listarParcelas({ vencimentoAte: hoje, status: 'VENCIDA' }).subscribe({
      next: (p) => this.vencidas.set(p.slice(0, 10)),
      error: () => this.vencidas.set([]),
    });
  }

  formatarMoeda(valor: number): string {
    return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(valor);
  }

  get percentualRecebido(): number {
    const r = this.resumo();
    if (!r || r.totalReceita === 0) return 0;
    return Math.round((r.totalRecebido / r.totalReceita) * 100);
  }

  irParaParcelas(status?: string): void {
    this.router.navigate(['/financeiro/parcelas'], { queryParams: status ? { status } : {} });
  }
}
