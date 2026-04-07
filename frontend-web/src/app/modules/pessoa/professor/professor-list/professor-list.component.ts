import { Component, OnInit, signal } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ProfessorService, Professor, ProfessorRequest } from '../../services/professor.service';

@Component({
  selector: 'wld-professor-list',
  templateUrl: './professor-list.component.html',
  standalone: false,
})
export class ProfessorListComponent implements OnInit {
  professores = signal<Professor[]>([]);
  carregando = signal(false);
  formularioVisivel = false;
  editandoId: number | null = null;
  form!: FormGroup;

  constructor(
    private service: ProfessorService,
    private fb: FormBuilder,
    private snackBar: MatSnackBar,
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      nome: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      especialidade: [''],
    });
    this.carregar();
  }

  carregar(): void {
    this.carregando.set(true);
    this.service.listar({ page: 0, size: 50 }).subscribe({
      next: (page) => {
        this.professores.set(page.content);
        this.carregando.set(false);
      },
      error: () => {
        this.snackBar.open('Erro ao carregar professores.', 'Fechar', { duration: 4000 });
        this.carregando.set(false);
      },
    });
  }

  novo(): void {
    this.editandoId = null;
    this.form.reset({ nome: '', email: '', especialidade: '' });
    this.formularioVisivel = true;
  }

  editar(p: Professor): void {
    this.editandoId = p.id;
    this.form.patchValue({ nome: p.nome, email: p.email, especialidade: p.especialidade });
    this.formularioVisivel = true;
  }

  fecharFormulario(): void {
    this.editandoId = null;
    this.formularioVisivel = false;
    this.form.reset();
  }

  salvar(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    const dto: ProfessorRequest = this.form.value;
    const req = this.editandoId
      ? this.service.atualizar(this.editandoId, dto)
      : this.service.criar(dto);
    req.subscribe({
      next: () => {
        this.snackBar.open(
          this.editandoId ? 'Professor atualizado!' : 'Professor cadastrado!',
          'Fechar', { duration: 3000 }
        );
        this.fecharFormulario();
        this.carregar();
      },
      error: (err) => {
        const msg = err?.error?.message || 'Erro ao salvar.';
        this.snackBar.open(msg, 'Fechar', { duration: 5000 });
      },
    });
  }

  inativar(p: Professor): void {
    if (!confirm('Inativar professor "' + p.nome + '"?')) return;
    this.service.inativar(p.id).subscribe({
      next: () => {
        this.snackBar.open('Professor inativado.', 'Fechar', { duration: 3000 });
        this.carregar();
      },
      error: () => this.snackBar.open('Erro ao inativar.', 'Fechar', { duration: 4000 }),
    });
  }
}
