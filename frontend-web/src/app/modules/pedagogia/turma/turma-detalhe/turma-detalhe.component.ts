import { Component, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { PedagogiaService, Turma, AlunoTurma } from '../../services/pedagogia.service';
import { EpocaService, Epoca } from '../../services/epoca.service';

type TabAtiva = 'alunos' | 'epocas' | 'horarios';

@Component({
  selector: 'wld-turma-detalhe',
  templateUrl: './turma-detalhe.component.html',
  standalone: false,
})
export class TurmaDetalheComponent implements OnInit {
  turma      = signal<Turma | null>(null);
  alunos     = signal<AlunoTurma[]>([]);
  epocas     = signal<Epoca[]>([]);
  carregando = signal(true);
  tabAtiva   = signal<TabAtiva>('alunos');

  readonly tabs: { id: TabAtiva; label: string; icone: string }[] = [
    { id: 'alunos',   label: 'Alunos',   icone: 'group'    },
    { id: 'epocas',   label: 'Épocas',   icone: 'timeline' },
    { id: 'horarios', label: 'Horários', icone: 'schedule' },
  ];

  constructor(
    private route: ActivatedRoute,
    readonly router: Router,
    private pedagogiaService: PedagogiaService,
    private epocaService: EpocaService,
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (!id) { this.router.navigate(['/pedagogia/turmas']); return; }

    this.pedagogiaService.buscarTurma(id).subscribe({
      next: (t) => { this.turma.set(t); this.carregando.set(false); },
      error: () => { this.carregando.set(false); },
    });

    this.pedagogiaService.listarAlunosDaTurma(id).subscribe({
      next: (a) => this.alunos.set(a),
      error: () => this.alunos.set([]),
    });

    this.epocaService.listar(id).subscribe({
      next: (e) => this.epocas.set(e),
      error: () => this.epocas.set([]),
    });
  }

  novaEpoca(): void {
    this.router.navigate(['/pedagogia/epocas/nova'], {
      queryParams: { turmaId: this.turma()?.id },
    });
  }

  verObservacoes(alunoId: number): void {
    this.router.navigate(['/pedagogia/observacoes', alunoId]);
  }
}
