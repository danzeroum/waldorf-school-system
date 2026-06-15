import { Component, OnInit, OnDestroy, computed, signal } from '@angular/core';
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
  observacoes   = signal<ObservacaoPedagogica[]>([]);
  carregando    = signal(true);
  exibirForm    = signal(false);
  salvando      = signal(false);
  editandoId    = signal<number | null>(null);
  alunoId       = signal(0);
  filtroAspecto = signal<string>('');
  statusLocal   = signal<StatusLocal>('RASCUNHO');

  turmaSel = signal<number | null>(null);
  alunoSel = signal<number | null>(null);
  epocaSel = signal<number | null>(null);

  form!: FormGroup;
  private sub?: Subscription;

  readonly aspectos = [
    { value: 'FISICO',     label: 'Físico',     chipCss: 'bg-green-100 text-green-700 border-green-200'     },
    { value: 'ANIMICO',    label: 'Anímico',    chipCss: 'bg-blue-100 text-blue-700 border-blue-200'       },
    { value: 'ESPIRITUAL', label: 'Espiritual', chipCss: 'bg-indigo-100 text-indigo-700 border-indigo-200' },
  ];

  readonly statusLabels: Record<StatusLocal, { label: string; css: string }> = {
    RASCUNHO: { label: 'Rascunho', css: 'badge badge-neutral'  },
    REVISADO: { label: 'Revisado', css: 'badge badge-warning'  },
    APROVADO: { label: 'Aprovado', css: 'badge badge-success'  },
  };

  // Mock context data — TODO: carregar via API
  readonly turmasMock = [
    { id: 1, nome: 'Classe 1' },
    { id: 2, nome: 'Classe 3' },
    { id: 3, nome: 'Classe 5' },
  ];

  readonly alunosMock = [
    { id: 1, nome: 'Ana Clara Silva'       },
    { id: 2, nome: 'Pedro Santos Oliveira' },
    { id: 3, nome: 'Maria Souza'           },
  ];

  readonly epocasMock = [
    { id: 1, titulo: '1ª Época 2026' },
    { id: 2, titulo: '2ª Época 2026' },
  ];

  observacoesFiltradas = computed(() => {
    const aspecto  = this.filtroAspecto();
    const alunoSel = this.alunoSel();
    const epocaSel = this.epocaSel();
    return this.observacoes().filter(o => {
      const matchAspecto = !aspecto  || o.aspecto  === aspecto;
      const matchAluno   = !alunoSel || o.alunoId  === alunoSel;
      const matchEpoca   = !epocaSel || o.epocaId  === epocaSel;
      return matchAspecto && matchAluno && matchEpoca;
    });
  });

  contagemPorAspecto = computed((): Record<string, number> => {
    const contagem: Record<string, number> = {};
    for (const o of this.observacoesFiltradas()) {
      contagem[o.aspecto] = (contagem[o.aspecto] ?? 0) + 1;
    }
    return contagem;
  });

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private obsService: ObservacaoService,
    private epocaService: EpocaService,
    private snackBar: MatSnackBar,
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('alunoId'));
    if (id) this.alunoId.set(id);

    this.form = this.fb.group({
      data:     [new Date().toISOString().split('T')[0], Validators.required],
      aspecto:  ['ANIMICO', Validators.required],
      conteudo: ['', [Validators.required, Validators.minLength(10)]],
      privada:  [false],
      epocaId:  [null],
    });

    this.carregar();
  }

  ngOnDestroy(): void {
    this.sub?.unsubscribe();
  }

  carregar(): void {
    this.carregando.set(true);
    const id = this.alunoId();
    const obs$ = id
      ? this.obsService.listarPorAluno(id)
      : null;

    if (!obs$) {
      this.observacoes.set(this.mockObservacoes());
      this.carregando.set(false);
      return;
    }

    this.sub = obs$.subscribe({
      next: (obs) => { this.observacoes.set(obs); this.carregando.set(false); },
      error: ()   => { this.observacoes.set(this.mockObservacoes()); this.carregando.set(false); },
    });
  }

  salvar(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.salvando.set(true);

    const dto: CreateObservacaoRequest = {
      alunoId:  this.alunoId() || (this.alunoSel() ?? 0),
      data:     this.form.value.data,
      aspecto:  this.form.value.aspecto,
      conteudo: this.form.value.conteudo,
      privada:  this.form.value.privada ?? false,
    };

    const req = this.editandoId()
      ? this.obsService.atualizar(this.editandoId()!, dto)
      : this.obsService.criar(dto);

    req.subscribe({
      next: (resultado) => {
        if (this.editandoId()) {
          this.observacoes.update(list => list.map(o => o.id === resultado.id ? resultado : o));
          this.snackBar.open('Observação atualizada!', 'Fechar', { duration: 3000 });
        } else {
          this.observacoes.update(list => [resultado, ...list]);
          this.snackBar.open('Observação registrada!', 'Fechar', { duration: 3000 });
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
  }

  excluir(obs: ObservacaoPedagogica): void {
    if (!confirm(`Excluir observação de ${obs.data}?`)) return;
    this.obsService.excluir(obs.id).subscribe({
      next: () => {
        this.observacoes.update(list => list.filter(o => o.id !== obs.id));
        this.snackBar.open('Observação excluída.', 'Fechar', { duration: 3000 });
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

  iniciais(nome: string | null | undefined): string {
    if (!nome) return '?';
    const partes = nome.trim().split(/\s+/);
    if (partes.length === 1) return partes[0][0].toUpperCase();
    return (partes[0][0] + partes[partes.length - 1][0]).toUpperCase();
  }

  private mockObservacoes(): ObservacaoPedagogica[] {
    return [
      { id: 1, alunoId: 1, alunoNome: 'Ana Clara Silva',       professorId: 1, professorNome: 'Profa. Renata Coelho', data: '2026-06-10', aspecto: 'FISICO',     privada: false, conteudo: 'Ana demonstrou grande evolução na coordenação motora nas atividades de euritimia. Sua postura corporal melhorou significativamente desde o início da época.',         epocaId: 1, epocaTitulo: '1ª Época 2026' },
      { id: 2, alunoId: 1, alunoNome: 'Ana Clara Silva',       professorId: 1, professorNome: 'Profa. Renata Coelho', data: '2026-05-20', aspecto: 'ANIMICO',    privada: true,  conteudo: 'A aluna demonstra sensibilidade artística marcante, especialmente nas atividades de pintura aquarela. Relaciona-se bem com os colegas, demonstrando empatia.', epocaId: 1, epocaTitulo: '1ª Época 2026' },
      { id: 3, alunoId: 2, alunoNome: 'Pedro Santos Oliveira', professorId: 1, professorNome: 'Profa. Renata Coelho', data: '2026-06-12', aspecto: 'ESPIRITUAL', privada: false, conteudo: 'Pedro demonstra profunda capacidade reflexiva e de concentração durante as histórias e contos. Faz perguntas pertinentes que revelam amadurecimento interior.' },
      { id: 4, alunoId: 3, alunoNome: 'Maria Souza',           professorId: 2, professorNome: 'Prof. Marcos Alves',  data: '2026-06-08', aspecto: 'FISICO',     privada: false, conteudo: 'Maria participou ativamente nas atividades físicas do jardim. Apresenta boa coordenação motora grossa e início do desenvolvimento da motricidade fina.',        epocaId: 1, epocaTitulo: '1ª Época 2026' },
      { id: 5, alunoId: 2, alunoNome: 'Pedro Santos Oliveira', professorId: 2, professorNome: 'Prof. Marcos Alves',  data: '2026-05-15', aspecto: 'ANIMICO',    privada: false, conteudo: 'Pedro mostrou grande engajamento nas atividades artísticas coletivas. Liderança natural na organização dos grupos, com respeito aos colegas.' },
    ];
  }
}
