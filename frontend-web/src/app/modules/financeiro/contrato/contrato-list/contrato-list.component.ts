import { Component, OnInit, signal, computed } from '@angular/core';
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

  readonly chips = [
    { status: '',             label: 'Todos'        },
    { status: 'ATIVO',        label: 'Ativo'        },
    { status: 'INADIMPLENTE', label: 'Inadimplente' },
    { status: 'SUSPENSO',     label: 'Suspenso'     },
    { status: 'ENCERRADO',    label: 'Encerrado'    },
  ];

  // Filtragem de status no cliente para manter contagens precisas; a busca por
  // nome continua server-side via buscaCtrl.
  contratosFiltrados = computed(() => {
    const status = this.filtroStatus();
    return status ? this.contratos().filter(c => c.status === status) : this.contratos();
  });

  contagemPorStatus = computed((): { total: number; [key: string]: number } => {
    const contagem: { total: number; [key: string]: number } = { total: this.contratos().length };
    for (const c of this.contratos()) {
      contagem[c.status] = (contagem[c.status] ?? 0) + 1;
    }
    return contagem;
  });

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
      nome: this.buscaCtrl.value || undefined,
    }).subscribe({
      next: (c) => { this.contratos.set(c); this.carregando.set(false); },
      error: ()  => { this.contratos.set(this.mockContratos()); this.carregando.set(false); },
    });
  }

  /** Percentual de parcelas pagas (0–100); 0 quando não há dados. */
  progressoParcelas(c: Contrato): number {
    if (!c.totalParcelas || c.parcelasPagas == null) return 0;
    return Math.round((c.parcelasPagas / c.totalParcelas) * 100);
  }

  novoContrato(): void { this.router.navigate(['/financeiro/contratos/novo']); }
  verContrato(id: number): void { this.router.navigate(['/financeiro/contratos', id]); }

  formatarMoeda(v: number): string {
    return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(v);
  }

  private mockContratos(): Contrato[] {
    return [
      { id: 1, alunoId: 1, alunoNome: 'Ana Clara Silva',       turmaId: 1, turmaNome: 'Classe 3', anoLetivo: 2026, valorMensalidade: 1850, valorMatricula: 500, totalParcelas: 12, parcelasPagas: 4, diaVencimento: 10, status: 'ATIVO',        dataInicio: '2026-02-01', createdAt: '' },
      { id: 2, alunoId: 2, alunoNome: 'Pedro Santos Oliveira', turmaId: 1, turmaNome: 'Classe 3', anoLetivo: 2026, valorMensalidade: 1850, valorMatricula: 500, totalParcelas: 12, parcelasPagas: 2, diaVencimento: 10, status: 'INADIMPLENTE', dataInicio: '2026-02-01', createdAt: '' },
      { id: 3, alunoId: 3, alunoNome: 'Maria Souza',           turmaId: 2, turmaNome: 'Classe 5', anoLetivo: 2026, valorMensalidade: 1950, valorMatricula: 500, totalParcelas: 12, parcelasPagas: 5, diaVencimento: 15, status: 'ATIVO',        dataInicio: '2026-02-01', createdAt: '' },
    ];
  }
}
