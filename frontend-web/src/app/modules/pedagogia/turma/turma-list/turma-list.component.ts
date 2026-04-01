import { Component, OnInit, signal } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { PedagogiaService, Turma, TurmaRequest } from '../../services/pedagogia.service';

@Component({
  selector: 'wld-turma-list',
  templateUrl: './turma-list.component.html',
  standalone: false,
})
export class TurmaListComponent implements OnInit {
  turmas = signal<Turma[]>([]);
  carregando = signal(true);
  anoLetivo = signal(new Date().getFullYear());
  formularioVisivel = false;
  editandoId: number | null = null;
  salvando = false;
  formulario!: FormGroup;

  constructor(
    private fb: FormBuilder,
    private pedagogiaService: PedagogiaService,
    private router: Router,
    private snackBar: MatSnackBar,
  ) {
    this.formulario = this.fb.group({
      nome:               ['', [Validators.required, Validators.maxLength(100)]],
      anoLetivo:          [new Date().getFullYear(), [Validators.required, Validators.min(2020), Validators.max(2040)]],
      anoEscolar:         [null],
      capacidadeMaxima:   [null, [Validators.min(1), Validators.max(50)]],
      professorRegenteId: [null],
    });
  }

  ngOnInit(): void {
    this.carregar();
  }

  carregar(): void {
    this.carregando.set(true);
    this.pedagogiaService.listarTurmas(this.anoLetivo()).subscribe({
      next: (turmas) => { this.turmas.set(turmas); this.carregando.set(false); },
      error: () => { this.turmas.set([]); this.carregando.set(false); },
    });
  }

  verTurma(id: number): void {
    if (this.formularioVisivel) return;
    this.router.navigate(['/pedagogia/turmas', id]);
  }

  abrirFormulario(turma?: Turma): void {
    if (turma) {
      this.editandoId = turma.id;
      this.formulario.patchValue({
        nome:               turma.nome,
        anoLetivo:          turma.anoLetivo,
        anoEscolar:         turma.anoEscolar ?? null,
        capacidadeMaxima:   turma.capacidadeMaxima ?? null,
        professorRegenteId: turma.professorRegenteId ?? null,
      });
    } else {
      this.editandoId = null;
      this.formulario.reset({ anoLetivo: this.anoLetivo() });
    }
    this.formularioVisivel = true;
  }

  fecharFormulario(): void {
    this.formularioVisivel = false;
    this.editandoId = null;
    this.formulario.reset({ anoLetivo: this.anoLetivo() });
  }

  salvar(): void {
    if (this.formulario.invalid) return;
    this.salvando = true;
    const dto: TurmaRequest = this.formulario.value;

    const obs$ = this.editandoId
      ? this.pedagogiaService.atualizarTurma(this.editandoId, dto)
      : this.pedagogiaService.criarTurma(dto);

    obs$.subscribe({
      next: () => {
        this.snackBar.open(
          this.editandoId ? 'Turma atualizada!' : 'Turma criada!',
          'Fechar',
          { duration: 3000 },
        );
        this.salvando = false;
        this.fecharFormulario();
        this.carregar();
      },
      error: () => {
        this.snackBar.open('Erro ao salvar turma.', 'Fechar', { duration: 5000 });
        this.salvando = false;
      },
    });
  }

  alternarAtiva(turma: Turma): void {
    this.pedagogiaService.toggleAtiva(turma.id, !turma.ativa).subscribe({
      next: () => this.carregar(),
      error: () => this.snackBar.open('Erro ao alterar status.', 'Fechar', { duration: 5000 }),
    });
  }

  get f() { return this.formulario.controls; }
}
