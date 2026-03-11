import { Component, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';
import { PedagogiaService, Turma } from '../../services/pedagogia.service';

@Component({
  selector: 'wld-turma-list',
  templateUrl: './turma-list.component.html',
  standalone: false,
})
export class TurmaListComponent implements OnInit {
  turmas = signal<Turma[]>([]);
  carregando = signal(true);
  anoLetivo = signal(new Date().getFullYear());

  constructor(
    private pedagogiaService: PedagogiaService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.carregar();
  }

  carregar(): void {
    this.carregando.set(true);
    this.pedagogiaService.listarTurmas(this.anoLetivo()).subscribe({
      next: (turmas) => {
        this.turmas.set(turmas);
        this.carregando.set(false);
      },
      error: () => {
        this.turmas.set(this.mockTurmas());
        this.carregando.set(false);
      },
    });
  }

  verTurma(id: number): void {
    this.router.navigate(['/pedagogia/turmas', id]);
  }

  private mockTurmas(): Turma[] {
    const nomes = ['Jardim de Infância', 'Classe 1', 'Classe 2', 'Classe 3',
                   'Classe 4', 'Classe 5', 'Classe 6', 'Classe 7', 'Classe 8'];
    return nomes.map((nome, i) => ({
      id: i + 1,
      nome,
      serie: String(i),
      anoLetivo: this.anoLetivo(),
      totalAlunos: Math.floor(Math.random() * 20) + 10,
      situacao: 'ATIVA' as const,
      professorRegente: { id: i + 1, nomeCompleto: `Prof. Exemplo ${i + 1}` },
    }));
  }
}
