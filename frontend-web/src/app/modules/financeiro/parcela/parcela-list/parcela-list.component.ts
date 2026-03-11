import { Component, OnInit, signal } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { FinanceiroService, Parcela } from '../../services/financeiro.service';

@Component({
  selector: 'wld-parcela-list',
  templateUrl: './parcela-list.component.html',
  standalone: false,
})
export class ParcelaListComponent implements OnInit {
  parcelas       = signal<Parcela[]>([]);
  carregando     = signal(true);
  filtroStatus   = signal('');
  baixaAberta    = signal<number | null>(null); // id da parcela com modal aberto
  salvandoBaixa  = signal(false);
  formBaixa!: FormGroup;

  readonly formasPagamento = ['PIX', 'Boleto', 'Cartão de Crédito', 'Cartão de Débito', 'Transferência', 'Dinheiro'];

  constructor(
    private financeiroService: FinanceiroService,
    private route: ActivatedRoute,
    private fb: FormBuilder,
  ) {}

  ngOnInit(): void {
    const statusQuery = this.route.snapshot.queryParamMap.get('status');
    if (statusQuery) this.filtroStatus.set(statusQuery);

    this.formBaixa = this.fb.group({
      valorPago:       ['', [Validators.required, Validators.min(0.01)]],
      dataPagamento:   [new Date().toISOString().split('T')[0], Validators.required],
      formaPagamento:  ['PIX', Validators.required],
      observacao:      [''],
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
      { id: 1, contratoId: 1, alunoNome: 'Ana Clara Silva',       numero: 1, descricao: 'Mensalidade 02/2026', valor: 2350, dataVencimento: '2026-02-10', status: 'PAGA',     dataPagamento: '2026-02-08' },
      { id: 2, contratoId: 1, alunoNome: 'Ana Clara Silva',       numero: 2, descricao: 'Mensalidade 03/2026', valor: 1850, dataVencimento: '2026-03-10', status: 'PAGA',     dataPagamento: '2026-03-09' },
      { id: 3, contratoId: 2, alunoNome: 'Pedro Santos Oliveira', numero: 1, descricao: 'Mensalidade 02/2026', valor: 2350, dataVencimento: '2026-02-10', status: 'VENCIDA',  },
      { id: 4, contratoId: 2, alunoNome: 'Pedro Santos Oliveira', numero: 2, descricao: 'Mensalidade 03/2026', valor: 1850, dataVencimento: '2026-03-10', status: 'VENCIDA',  },
      { id: 5, contratoId: 3, alunoNome: 'Maria Souza',           numero: 1, descricao: 'Mensalidade 02/2026', valor: 2450, dataVencimento: '2026-02-15', status: 'PAGA',     dataPagamento: '2026-02-14' },
      { id: 6, contratoId: 3, alunoNome: 'Maria Souza',           numero: 2, descricao: 'Mensalidade 03/2026', valor: 1950, dataVencimento: '2026-03-15', status: 'PENDENTE', },
    ] as Parcela[];
  }
}
