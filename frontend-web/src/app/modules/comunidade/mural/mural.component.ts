import { Component, OnInit, signal } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AvisoService, Aviso, TipoAviso } from '../services/aviso.service';
import { AuthService } from '@core/auth/auth.service';

@Component({
  selector: 'wld-mural',
  templateUrl: './mural.component.html',
  standalone: false,
})
export class MuralComponent implements OnInit {
  avisos       = signal<Aviso[]>([]);
  carregando   = signal(true);
  exibirForm   = signal(false);
  salvando     = signal(false);
  filtroTipo   = signal<string>('');
  form!: FormGroup;

  readonly tipos: TipoAviso[] = ['GERAL', 'TURMA', 'URGENTE', 'EVENTO', 'CARDAPIO', 'FESTIVAL', 'MUTIRAO'];

  constructor(
    private avisoService: AvisoService,
    private fb: FormBuilder,
    public authService: AuthService,
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      titulo:       ['', [Validators.required, Validators.minLength(3)]],
      conteudo:     ['', [Validators.required, Validators.minLength(10)]],
      tipo:         ['GERAL', Validators.required],
      turmaId:      [null],
      fixado:       [false],
      dataExpiracao:[null],
    });
    this.carregar();
  }

  carregar(): void {
    this.carregando.set(true);
    this.avisoService.listar().subscribe({
      next: (a) => { this.avisos.set(a); this.carregando.set(false); },
      error: ()  => { this.avisos.set(this.mockAvisos()); this.carregando.set(false); },
    });
  }

  salvar(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.salvando.set(true);
    this.avisoService.criar(this.form.value).subscribe({
      next: (a) => {
        this.avisos.update(list => [a, ...list]);
        this.salvando.set(false);
        this.exibirForm.set(false);
        this.form.reset({ tipo: 'GERAL', fixado: false });
      },
      error: () => { this.salvando.set(false); },
    });
  }

  get avisosFiltrados(): Aviso[] {
    const fixados  = this.avisos().filter(a => a.fixado);
    const normais  = this.avisos().filter(a => !a.fixado);
    const todos    = [...fixados, ...normais];
    const f = this.filtroTipo();
    return f ? todos.filter(a => a.tipo === f) : todos;
  }

  // FIX: AuthService não tem temPerfil() → usa hasRole()
  get podePublicar(): boolean {
    return this.authService.hasRole('SECRETARIA')
        || this.authService.hasRole('DIRETOR')
        || this.authService.hasRole('PROFESSOR');
  }

  private mockAvisos(): Aviso[] {
    return [
      { id: 1, titulo: 'Reunião de Pais — Classe 3', conteudo: 'Lembramos que a reunião de pais da Classe 3 será na sexta-feira, dia 13/03, às 19h no salão principal. Presença importante!', tipo: 'TURMA', autorNome: 'Prof. Maria', fixado: true, dataPublicacao: '2026-03-09', turmaNome: 'Classe 3' },
      { id: 2, titulo: 'Feriado — 28 de março', conteudo: 'Informamos que não haverá aulas no dia 28/03 (feriado municipal). As atividades retornam normalmente na segunda-feira, 30/03.', tipo: 'GERAL', autorNome: 'Secretaria', fixado: false, dataPublicacao: '2026-03-10' },
      { id: 3, titulo: 'Festival de Artes — Abril', conteudo: 'O Festival Anual de Artes acontecerá nos dias 18 e 19 de abril. Aguardem programação completa em breve.', tipo: 'FESTIVAL', autorNome: 'Direção', fixado: false, dataPublicacao: '2026-03-11' },
    ];
  }
}
