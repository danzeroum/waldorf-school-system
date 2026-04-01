import { Component, OnInit, OnDestroy, signal } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ObservacaoService, ObservacaoPedagogica, CreateObservacaoRequest } from '../services/observacao.service';
import { EpocaService, Epoca } from '../services/epoca.service';
import { Subscription } from 'rxjs';

type StatusLocal = 'RASCUNHO' | 'REVISADO' | 'APROVADO';

@Component({
  selector: 'wld-observacao-pedagogica',
  templateUrl: './observacao-pedagogica.component.html',
  standalone: false,
})
export class ObservacaoPedagogicaComponent implements OnInit, OnDestroy {
  observacoes       = signal<ObservacaoPedagogica[]>([]);
  carregando        = signal(true);
  exibirForm        = signal(false);
  salvando          = signal(false);
  editandoId        = signal<number | null>(null);
  alunoId           = signal(0);
  filtroAspecto     = signal<string>('');
  epocasDaTurma     = signal<Epoca[]>([]);
  statusLocal       = signal<StatusLocal>('RASCUNHO');
  form!: FormGroup;
  private sub?: Subscription;

  readonly aspectos = [
    { value: 'FISICO',     label: 'F\u00edsico'     },
    { value: 'ANIMICO',    label: 'An\u00edmico'    },
    { value: 'ESPIRITUAL', label: 'Espiritual' },
  ];

  readonly statusLabels: Record<StatusLocal, { label: string; css: string }> = {
    RASCUNHO: { label: 'Rascunho',  css: 'badge badge-neutral' },
    REVISADO: { label: 'Revisado',  css: 'badge badge-warning' },
    APROVADO: { label: 'Aprovado',  css: 'badge badge-success' },
  };

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private obsService: ObservacaoService,
    private epocaService: EpocaService,
    private snackBar: MatSnackBar,
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('alunoId'));
    if (!id) return;
    this.alunoId.set(id);

    this.form = this.fb.group({
      data:     [new Date().toISOString().split('T')[0], Validators.required],
      aspecto:  ['ANIMICO', Validators.required],
      conteudo: ['', [Validators.required, Validators.minLength(10)]],
      privada:  [false],
      epocaId:  [null], // campo reservado: backend nao aceita ainda
    });

    this.carregar();
  }

  ngOnDestroy(): void {
    this.sub?.unsubscribe();
  }

  carregar(): void {
    this.carregando.set(true);
    this.sub = this.obsService.listarPorAluno(this.alunoId()).subscribe({
      next: (obs) => { this.observacoes.set(obs); this.carregando.set(false); },
      error: ()   => { this.observacoes.set([]); this.carregando.set(false); },
    });
  }

  salvar(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.salvando.set(true);

    const dto: CreateObservacaoRequest = {
      alunoId: this.alunoId(),
      data:    this.form.value.data,
      aspecto: this.form.value.aspecto,
      conteudo: this.form.value.conteudo,
      privada:  this.form.value.privada ?? false,
    };

    const req = this.editandoId()
      ? this.obsService.atualizar(this.editandoId()!, dto)
      : this.obsService.criar(dto);

    req.subscribe({
      next: (resultado) => {
        if (this.editandoId()) {
          this.observacoes.update(list =>
            list.map(o => o.id === resultado.id ? resultado : o)
          );
          this.snackBar.open('Observa\u00e7\u00e3o atualizada!', 'Fechar', { duration: 3000 });
        } else {
          this.observacoes.update(list => [resultado, ...list]);
          this.snackBar.open('Observa\u00e7\u00e3o registrada!', 'Fechar', { duration: 3000 });
        }
        this.salvando.set(false);
        this.fecharFormulario();
      },
      error: () => {
        this.salvando.set(false);
        this.snackBar.open('Erro ao salvar. Verifique os dados.', 'Fechar', { duration: 5000 });
      },
    });
  }

  editar(obs: ObservacaoPedagogica): void {
    this.editandoId.set(obs.id);
    this.form.patchValue({
      data:    obs.data,
      aspecto: obs.aspecto,
      conteudo: obs.conteudo,
      privada: obs.privada,
    });
    this.exibirForm.set(true);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  excluir(obs: ObservacaoPedagogica): void {
    if (!confirm(`Excluir observa\u00e7\u00e3o de ${obs.data}?`)) return;
    this.obsService.excluir(obs.id).subscribe({
      next: () => {
        this.observacoes.update(list => list.filter(o => o.id !== obs.id));
        this.snackBar.open('Observa\u00e7\u00e3o exclu\u00edda.', 'Fechar', { duration: 3000 });
      },
      error: () => {
        this.snackBar.open('Erro ao excluir.', 'Fechar', { duration: 5000 });
      },
    });
  }

  fecharFormulario(): void {
    this.exibirForm.set(false);
    this.editandoId.set(null);
    this.statusLocal.set('RASCUNHO');
    this.form.reset({
      data:    new Date().toISOString().split('T')[0],
      aspecto: 'ANIMICO',
      privada: false,
      epocaId: null,
    });
  }

  avancarStatus(): void {
    const ordem: StatusLocal[] = ['RASCUNHO', 'REVISADO', 'APROVADO'];
    const idx = ordem.indexOf(this.statusLocal());
    if (idx < ordem.length - 1) this.statusLocal.set(ordem[idx + 1]);
  }

  voltarStatus(): void {
    const ordem: StatusLocal[] = ['RASCUNHO', 'REVISADO', 'APROVADO'];
    const idx = ordem.indexOf(this.statusLocal());
    if (idx > 0) this.statusLocal.set(ordem[idx - 1]);
  }

  get observacoesFiltradas(): ObservacaoPedagogica[] {
    const f = this.filtroAspecto();
    return f ? this.observacoes().filter(o => o.aspecto === f) : this.observacoes();
  }
}
