import { Component, OnInit, computed, signal } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { FinanceiroService, Parcela } from '../../services/financeiro.service';

@Component({
  selector: 'wld-parcela-list',
  templateUrl: './parcela-list.component.html',
  standalone: false,
})
export class ParcelaListComponent implements OnInit {
  parcelas      = signal<Parcela[]>([]);
  carregando    = signal(true);
  filtroStatus  = signal('');
  baixaAberta   = signal<number | null>(null);
  salvandoBaixa = signal(false);
  termoBusca    = signal('');
  selecionadas  = signal<Set<number>>(new Set());
  pagina        = signal(1);
  formBaixa!: FormGroup;

  readonly formasPagamento = ['PIX', 'Boleto', 'Cartão de Crédito', 'Cartão de Débito', 'Transferência', 'Dinheiro'];

  parcelasFiltradas = computed(() => {
    const termo  = this.termoBusca().toLowerCase().trim();
    const status = this.filtroStatus();
    return this.parcelas().filter(p => {
      const matchTermo  = !termo  || (p.alunoNome ?? '').toLowerCase().includes(termo);
      const matchStatus = !status || p.status === status;
      return matchTermo && matchStatus;
    });
  });

  contagemPorStatus = computed((): { total: number; [key: string]: number } => {
    const all = this.parcelas();
    const conta = (s: string) => all.filter(p => p.status === s).length;
    return { total: all.length, PENDENTE: conta('PENDENTE'), VENCIDA: conta('VENCIDA'), PAGA: conta('PAGA') };
  });

  resumoLocal = computed(() => {
    const all  = this.parcelas();
    const soma = (s: string) => all.filter(p => p.status === s).reduce((acc, p) => acc + p.valor, 0);
    return {
      totalReceita:  all.reduce((a, p) => a + p.valor, 0),
      totalRecebido: soma('PAGA'),
      totalPendente: soma('PENDENTE'),
      totalVencido:  soma('VENCIDA'),
    };
  });

  constructor(
    private financeiroService: FinanceiroService,
    private route: ActivatedRoute,
    private fb: FormBuilder,
  ) {}

  ngOnInit(): void {
    const statusQuery = this.route.snapshot.queryParamMap.get('status');
    if (statusQuery) this.filtroStatus.set(statusQuery);

    this.formBaixa = this.fb.group({
      valorPago:      ['', [Validators.required, Validators.min(0.01)]],
      dataPagamento:  [new Date().toISOString().split('T')[0], Validators.required],
      formaPagamento: ['PIX', Validators.required],
      observacao:     [''],
    });

    this.carregar();
  }

  carregar(): void {
    this.carregando.set(true);
    this.financeiroService.listarParcelas({ status: this.filtroStatus() || undefined }).subscribe({
      next: (p) => { this.parcelas.set(p); this.carregando.set(false); },
      error: ()  => { this.parcelas.set(this.mockParcelas()); this.carregando.set(false); },
    });
  }

  toggleSelecao(id: number): void {
    this.selecionadas.update(set => {
      const clone = new Set(set);
      if (clone.has(id)) clone.delete(id); else clone.add(id);
      return clone;
    });
  }

  toggleTodos(): void {
    const visiveis = this.parcelasFiltradas();
    this.selecionadas.update(set =>
      set.size === visiveis.length ? new Set() : new Set(visiveis.map(p => p.id))
    );
  }

  limparSelecao(): void { this.selecionadas.set(new Set()); }

  enviarCobranca(): void {
    // TODO: integrar endpoint POST /api/v1/financeiro/parcelas/cobranca
    const ids = Array.from(this.selecionadas());
    console.log('Enviar cobrança:', ids);
  }

  baixarLote(): void {
    // TODO: integrar endpoint POST /api/v1/financeiro/parcelas/baixa-lote
    const ids = Array.from(this.selecionadas());
    console.log('Baixar lote:', ids);
  }

  abrirBaixa(parcelaId: number, valor: number): void {
    this.formBaixa.patchValue({ valorPago: valor });
    this.baixaAberta.set(parcelaId);
  }

  fecharBaixa(): void { this.baixaAberta.set(null); }

  confirmarBaixa(): void {
    if (this.formBaixa.invalid || !this.baixaAberta()) return;
    this.salvandoBaixa.set(true);
    this.financeiroService.registrarPagamento({
      parcelaId: this.baixaAberta()!,
      ...this.formBaixa.value,
    }).subscribe({
      next: (parcelaAtualizada) => {
        this.parcelas.update(list =>
          list.map(p => p.id === parcelaAtualizada.id ? parcelaAtualizada : p)
        );
        this.salvandoBaixa.set(false);
        this.fecharBaixa();
      },
      error: () => { this.salvandoBaixa.set(false); },
    });
  }

  formatarMoeda(v: number): string {
    return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(v);
  }

  private mockParcelas(): Parcela[] {
    return [
      { id: 1, contratoId: 1, alunoNome: 'Ana Clara Silva',       numero: 3, descricao: 'Mensalidade 06/2026', valor: 2350, dataVencimento: '2026-06-10', status: 'PAGA',     dataPagamento: '2026-06-09' },
      { id: 2, contratoId: 1, alunoNome: 'Ana Clara Silva',       numero: 4, descricao: 'Mensalidade 07/2026', valor: 2350, dataVencimento: '2026-07-10', status: 'PENDENTE' },
      { id: 3, contratoId: 2, alunoNome: 'Pedro Santos Oliveira', numero: 2, descricao: 'Mensalidade 04/2026', valor: 1850, dataVencimento: '2026-04-10', status: 'VENCIDA'  },
      { id: 4, contratoId: 2, alunoNome: 'Pedro Santos Oliveira', numero: 3, descricao: 'Mensalidade 05/2026', valor: 1850, dataVencimento: '2026-05-10', status: 'VENCIDA'  },
      { id: 5, contratoId: 3, alunoNome: 'Maria Souza',           numero: 3, descricao: 'Mensalidade 06/2026', valor: 2450, dataVencimento: '2026-06-15', status: 'PENDENTE' },
      { id: 6, contratoId: 3, alunoNome: 'Maria Souza',           numero: 4, descricao: 'Mensalidade 07/2026', valor: 2450, dataVencimento: '2026-07-15', status: 'PENDENTE' },
      { id: 7, contratoId: 4, alunoNome: 'Lucas Ferreira Costa',  numero: 2, descricao: 'Mensalidade 06/2026', valor: 1980, dataVencimento: '2026-06-12', status: 'PAGA',     dataPagamento: '2026-06-11' },
      { id: 8, contratoId: 5, alunoNome: 'Sofia Lima Mendes',     numero: 1, descricao: 'Mensalidade 03/2026', valor: 2200, dataVencimento: '2026-03-01', status: 'VENCIDA'  },
    ] as Parcela[];
  }
}
