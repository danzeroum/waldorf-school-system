import { Component, OnInit, signal } from '@angular/core';
import { FormControl } from '@angular/forms';
import { Router } from '@angular/router';
import { Subject, debounceTime, distinctUntilChanged, takeUntil } from 'rxjs';
import { FinanceiroService, Contrato } from '../../services/financeiro.service';

@Component({
  selector: 'wld-contrato-list',
  templateUrl: './contrato-list.component.html',
  standalone: false,
})
export class ContratoListComponent implements OnInit {
  private destroy$ = new Subject<void>();
  contratos    = signal<Contrato[]>([]);
  carregando   = signal(true);
  filtroStatus = signal('');
  buscaCtrl    = new FormControl('');

  constructor(
    private financeiroService: FinanceiroService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.buscaCtrl.valueChanges.pipe(
      debounceTime(400), distinctUntilChanged(), takeUntil(this.destroy$),
    ).subscribe(() => this.carregar());
    this.carregar();
  }

  ngOnDestroy(): void { this.destroy$.next(); this.destroy$.complete(); }

  carregar(): void {
    this.carregando.set(true);
    this.financeiroService.listarContratos({
      status: this.filtroStatus() || undefined,
      nome:   this.buscaCtrl.value || undefined,
    }).subscribe({
      next: (c) => { this.contratos.set(c); this.carregando.set(false); },
      error: ()  => { this.contratos.set(this.mockContratos()); this.carregando.set(false); },
    });
  }

  novoContrato(): void { this.router.navigate(['/financeiro/contratos/novo']); }
  verContrato(id: number): void { this.router.navigate(['/financeiro/contratos', id]); }

  formatarMoeda(v: number): string {
    return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(v);
  }

  private mockContratos(): Contrato[] {
    return [
      { id: 1, alunoId: 1, alunoNome: 'Ana Clara Silva',       turmaId: 1, turmaNome: 'Classe 3', anoLetivo: 2026, valorMensalidade: 1850, valorMatricula: 500, totalParcelas: 12, diaVencimento: 10, status: 'ATIVO',        dataInicio: '2026-02-01', createdAt: '' },
      { id: 2, alunoId: 2, alunoNome: 'Pedro Santos Oliveira', turmaId: 1, turmaNome: 'Classe 3', anoLetivo: 2026, valorMensalidade: 1850, valorMatricula: 500, totalParcelas: 12, diaVencimento: 10, status: 'INADIMPLENTE', dataInicio: '2026-02-01', createdAt: '' },
      { id: 3, alunoId: 3, alunoNome: 'Maria Souza',           turmaId: 2, turmaNome: 'Classe 5', anoLetivo: 2026, valorMensalidade: 1950, valorMatricula: 500, totalParcelas: 12, diaVencimento: 15, status: 'ATIVO',        dataInicio: '2026-02-01', createdAt: '' },
    ];
  }
}
