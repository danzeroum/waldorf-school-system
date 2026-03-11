import { Component, OnInit, signal } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { ObservacaoService, ObservacaoPedagogica } from '../services/observacao.service';

@Component({
  selector: 'wld-observacao-pedagogica',
  templateUrl: './observacao-pedagogica.component.html',
  standalone: false,
})
export class ObservacaoPedagogicaComponent implements OnInit {
  observacoes   = signal<ObservacaoPedagogica[]>([]);
  carregando    = signal(true);
  exibirForm    = signal(false);
  salvando      = signal(false);
  alunoId       = signal(0);
  filtroAspecto = signal<string>('');
  form!: FormGroup;

  readonly aspectos = [
    { value: 'FISICO',     label: 'Físico'     },
    { value: 'ANIMICO',    label: 'Anímico'    },
    { value: 'ESPIRITUAL', label: 'Espiritual' },
  ];

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private obsService: ObservacaoService,
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('alunoId'));
    this.alunoId.set(id);

    this.form = this.fb.group({
      data:     [new Date().toISOString().split('T')[0], Validators.required],
      aspecto:  ['ANIMICO', Validators.required],
      conteudo: ['', [Validators.required, Validators.minLength(10)]],
      privada:  [false],
    });

    this.carregar();
  }

  carregar(): void {
    this.carregando.set(true);
    this.obsService.listarPorAluno(this.alunoId()).subscribe({
      next: (obs) => { this.observacoes.set(obs); this.carregando.set(false); },
      error: ()   => { this.observacoes.set([]); this.carregando.set(false); },
    });
  }

  salvar(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.salvando.set(true);
    this.obsService.criar({ ...this.form.value, alunoId: this.alunoId() }).subscribe({
      next: (obs) => {
        this.observacoes.update(list => [obs, ...list]);
        this.salvando.set(false);
        this.exibirForm.set(false);
        this.form.reset({ data: new Date().toISOString().split('T')[0], aspecto: 'ANIMICO', privada: false });
      },
      error: () => { this.salvando.set(false); },
    });
  }

  get observacoesFiltradas(): ObservacaoPedagogica[] {
    const f = this.filtroAspecto();
    return f ? this.observacoes().filter(o => o.aspecto === f) : this.observacoes();
  }
}
