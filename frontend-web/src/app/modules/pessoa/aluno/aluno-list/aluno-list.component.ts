import { Component, OnInit, OnDestroy, signal, computed } from '@angular/core';
import { Router } from '@angular/router';
import { FormControl } from '@angular/forms';
import { Subject, debounceTime, distinctUntilChanged, takeUntil } from 'rxjs';
import { AlunoService, AlunoFiltros } from '../../services/aluno.service';
import { Aluno, SituacaoAluno, PageResponse } from '@models/pessoa.models';

@Component({
  selector: 'wld-aluno-list',
  templateUrl: './aluno-list.component.html',
  standalone: false,
})
export class AlunoListComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();

  // Estado
  alunos = signal<Aluno[]>([]);
  totalElements = signal(0);
  totalPages = signal(0);
  carregando = signal(true);
  paginaAtual = signal(0);
  tamanhoPagina = 25;

  // Filtros
  buscaCtrl = new FormControl('');
  filtroSituacao = signal<string>('');
  filtroTurma = signal<number | null>(null);

  readonly situacoes = Object.values(SituacaoAluno);
  readonly SituacaoAluno = SituacaoAluno;

  // Menu de ações por linha
  menuAbertoId = signal<number | null>(null);

  constructor(
    private alunoService: AlunoService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    // Debounce na busca por nome
    this.buscaCtrl.valueChanges.pipe(
      debounceTime(400),
      distinctUntilChanged(),
      takeUntil(this.destroy$),
    ).subscribe(() => {
      this.paginaAtual.set(0);
      this.carregar();
    });

    this.carregar();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  carregar(): void {
    this.carregando.set(true);
    const filtros: AlunoFiltros = {
      nome:      this.buscaCtrl.value || undefined,
      situacao:  this.filtroSituacao() || undefined,
      turmaId:   this.filtroTurma() ?? undefined,
    };

    this.alunoService.listar(filtros, {
      page: this.paginaAtual(),
      size: this.tamanhoPagina,
    }).subscribe({
      next: (resp: PageResponse<Aluno>) => {
        this.alunos.set(resp.content);
        this.totalElements.set(resp.totalElements);
        this.totalPages.set(resp.totalPages);
        this.carregando.set(false);
      },
      error: () => {
        // Modo demo: dados mockados enquanto API não está pronta
        this.alunos.set(this.dadosMock());
        this.totalElements.set(3);
        this.totalPages.set(1);
        this.carregando.set(false);
      },
    });
  }

  irParaPagina(pagina: number): void {
    if (pagina < 0 || pagina >= this.totalPages()) return;
    this.paginaAtual.set(pagina);
    this.carregar();
  }

  mudarSituacao(situacao: string): void {
    this.filtroSituacao.set(situacao);
    this.paginaAtual.set(0);
    this.carregar();
  }

  novoAluno(): void {
    this.router.navigate(['/pessoas/alunos/novo']);
  }

  verDetalhe(id: number): void {
    this.router.navigate(['/pessoas/alunos', id]);
  }

  editarAluno(id: number): void {
    this.router.navigate(['/pessoas/alunos', id, 'editar']);
  }

  toggleMenu(id: number, event: Event): void {
    event.stopPropagation();
    this.menuAbertoId.update(v => v === id ? null : id);
  }

  fecharMenus(): void {
    this.menuAbertoId.set(null);
  }

  get paginasVisiveis(): number[] {
    const total = this.totalPages();
    const atual = this.paginaAtual();
    const pages: number[] = [];
    const start = Math.max(0, atual - 2);
    const end   = Math.min(total - 1, atual + 2);
    for (let i = start; i <= end; i++) pages.push(i);
    return pages;
  }

  private dadosMock(): Aluno[] {
    return [
      {
        id: 1, nomeCompleto: 'Ana Clara Silva', tipo: 'ALUNO' as any,
        situacao: SituacaoAluno.ATIVO,
        turmaAtual: { id: 1, nome: 'Classe 3', anoLetivo: 2026, serie: '3' },
        createdAt: '', updatedAt: '',
      },
      {
        id: 2, nomeCompleto: 'Pedro Santos Oliveira', tipo: 'ALUNO' as any,
        situacao: SituacaoAluno.ATIVO,
        turmaAtual: { id: 1, nome: 'Classe 3', anoLetivo: 2026, serie: '3' },
        createdAt: '', updatedAt: '',
      },
      {
        id: 3, nomeCompleto: 'Maria Souza', tipo: 'ALUNO' as any,
        situacao: SituacaoAluno.PENDENTE,
        turmaAtual: { id: 2, nome: 'Classe 5', anoLetivo: 2026, serie: '5' },
        createdAt: '', updatedAt: '',
      },
    ] as Aluno[];
  }
}
