import { Component, OnInit, signal, computed } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { FinanceiroService } from '../../services/financeiro.service';

@Component({
  selector: 'wld-contrato-form',
  templateUrl: './contrato-form.component.html',
  standalone: false,
})
export class ContratoFormComponent implements OnInit {
  form!: FormGroup;
  salvando   = signal(false);
  erro       = signal<string | null>(null);
  modoEdicao = signal(false);

  // Preview de parcelas calculado em tempo real
  previewParcelas = computed(() => {
    const v = this.form?.value;
    if (!v || !v.valorMensalidade || !v.totalParcelas) return [];
    const parcelas = [];
    const inicio = v.dataInicio ? new Date(v.dataInicio) : new Date();
    for (let i = 1; i <= Math.min(v.totalParcelas, 12); i++) {
      const d = new Date(inicio);
      d.setMonth(d.getMonth() + i - 1);
      d.setDate(v.diaVencimento || 10);
      parcelas.push({
        num: i,
        vencimento: d.toLocaleDateString('pt-BR'),
        valor: i === 1
          ? (+v.valorMensalidade + +v.valorMatricula)
          : +v.valorMensalidade,
      });
    }
    return parcelas;
  });

  constructor(
    private fb: FormBuilder,
    private financeiroService: FinanceiroService,
    protected router: Router,
    private route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    this.modoEdicao.set(!!id && id !== 'novo');

    this.form = this.fb.group({
      alunoId:          ['', Validators.required],
      turmaId:          ['', Validators.required],
      anoLetivo:        [new Date().getFullYear(), Validators.required],
      valorMensalidade: ['', [Validators.required, Validators.min(1)]],
      valorMatricula:   [0],
      totalParcelas:    [12, [Validators.required, Validators.min(1), Validators.max(12)]],
      diaVencimento:    [10, [Validators.required, Validators.min(1), Validators.max(28)]],
      dataInicio:       ['', Validators.required],
      observacoes:      [''],
    });
  }

  salvar(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.salvando.set(true);
    this.financeiroService.criarContrato(this.form.value).subscribe({
      next: (c) => {
        this.salvando.set(false);
        this.router.navigate(['/financeiro/contratos', c.id]);
      },
      error: (err) => {
        this.salvando.set(false);
        this.erro.set(err.error?.message ?? 'Erro ao criar contrato.');
      },
    });
  }

  formatarMoeda(v: number): string {
    return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(v);
  }

  get totalContrato(): number {
    const v = this.form?.value;
    if (!v) return 0;
    return (+v.valorMensalidade * +v.totalParcelas) + (+v.valorMatricula || 0);
  }
}
