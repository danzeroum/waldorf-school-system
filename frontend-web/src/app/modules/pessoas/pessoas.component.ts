import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { PageEvent } from '@angular/material/paginator';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AlunoService, Aluno, AlunoRequest, Genero } from './services/aluno.service';

@Component({
  selector: 'app-pessoas',
  templateUrl: './pessoas.component.html',
})
export class PessoasComponent implements OnInit {

  displayedColumns = ['matricula', 'nome', 'turma', 'email', 'status', 'acoes'];
  loading = false;
  alunos: Aluno[] = [];
  totalElements = 0;
  pageSize = 10;
  pageIndex = 0;

  filtroNome  = '';
  filtroAtivo: boolean | null = null;

  form!: FormGroup;
  editandoId: number | null = null;
  formularioVisivel = false;

  constructor(
    private alunoService: AlunoService,
    private fb:           FormBuilder,
    private snackBar:     MatSnackBar,
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      nome:            ['', Validators.required],
      dataNascimento:  ['', Validators.required],
      genero:          ['NAO_INFORMADO' as Genero],
      email:           ['', [Validators.required, Validators.email]],
      telefone:        [''],
      turmaId:         [null],
      anoIngresso:     [new Date().getFullYear()],
      temperamento:    [''],
      enderecoRua:     [''],
      enderecoNumero:  [''],
      enderecoBairro:  [''],
      enderecoCidade:  [''],
      enderecoEstado:  [''],
      enderecoCep:     [''],
      observacoes:     [''],
    });
    this.carregarAlunos();
  }

  carregarAlunos(): void {
    this.loading = true;
    this.alunoService.listar({
      nome:  this.filtroNome  || undefined,
      ativo: this.filtroAtivo ?? undefined,
      page:  this.pageIndex,
      size:  this.pageSize,
    }).subscribe({
      next: (page) => {
        this.alunos        = page.content;
        this.totalElements = page.totalElements;
        this.loading       = false;
      },
      error: () => {
        this.snackBar.open('Erro ao carregar alunos.', 'Fechar', { duration: 4000 });
        this.loading = false;
      },
    });
  }

  filtrar(): void {
    this.pageIndex = 0;
    this.carregarAlunos();
  }

  limparFiltro(): void {
    this.filtroNome  = '';
    this.filtroAtivo = null;
    this.pageIndex   = 0;
    this.carregarAlunos();
  }

  mudarPagina(event: PageEvent): void {
    this.pageIndex = event.pageIndex;
    this.pageSize  = event.pageSize;
    this.carregarAlunos();
  }

  novo(): void {
    this.editandoId       = null;
    this.formularioVisivel = true;
    this.form.reset({
      nome: '', dataNascimento: '', genero: 'NAO_INFORMADO' as Genero,
      email: '', telefone: '', turmaId: null,
      anoIngresso: new Date().getFullYear(),
      temperamento: '', enderecoRua: '', enderecoNumero: '',
      enderecoBairro: '', enderecoCidade: '', enderecoEstado: '',
      enderecoCep: '', observacoes: '',
    });
  }

  editar(aluno: Aluno): void {
    this.editandoId        = aluno.id;
    this.formularioVisivel = true;
    this.form.patchValue({
      nome:           aluno.nome,
      dataNascimento: aluno.dataNascimento,
      genero:         aluno.genero,
      email:          aluno.email,
      anoIngresso:    aluno.anoIngresso,
      temperamento:   aluno.temperamento,
    });
  }

  fecharFormulario(): void {
    this.editandoId        = null;
    this.formularioVisivel = false;
    this.form.reset();
  }

  salvar(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }

    const dto: AlunoRequest = this.form.value;
    const req = this.editandoId
      ? this.alunoService.atualizar(this.editandoId, dto)
      : this.alunoService.criar(dto);

    req.subscribe({
      next: () => {
        this.snackBar.open(
          this.editandoId ? 'Aluno atualizado!' : 'Aluno cadastrado!',
          'Fechar', { duration: 3000 }
        );
        this.fecharFormulario();
        this.carregarAlunos();
      },
      error: (err) => {
        const msg = err?.error?.message || 'Erro ao salvar. Verifique os dados.';
        this.snackBar.open(msg, 'Fechar', { duration: 5000 });
      },
    });
  }

  inativar(aluno: Aluno): void {
    if (!confirm(`Inativar aluno "${aluno.nome}"? Esta ação pode ser revertida pelo administrador.`)) return;
    this.alunoService.inativar(aluno.id).subscribe({
      next: () => {
        this.snackBar.open('Aluno inativado.', 'Fechar', { duration: 3000 });
        this.carregarAlunos();
      },
      error: () => this.snackBar.open('Erro ao inativar.', 'Fechar', { duration: 4000 }),
    });
  }
}
