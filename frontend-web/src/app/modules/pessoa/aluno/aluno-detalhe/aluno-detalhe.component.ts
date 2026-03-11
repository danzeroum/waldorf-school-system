import { Component, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AlunoService } from '../../services/aluno.service';
import { Aluno } from '@models/pessoa.models';

type TabAtiva = 'perfil' | 'observacoes' | 'relatorios' | 'portfolio' | 'financeiro';

@Component({
  selector: 'wld-aluno-detalhe',
  templateUrl: './aluno-detalhe.component.html',
  standalone: false,
})
export class AlunoDetalheComponent implements OnInit {
  aluno = signal<Aluno | null>(null);
  carregando = signal(true);
  erro = signal<string | null>(null);
  tabAtiva = signal<TabAtiva>('perfil');

  readonly tabs: { id: TabAtiva; label: string; icone: string }[] = [
    { id: 'perfil',       label: 'Perfil',        icone: 'person'      },
    { id: 'observacoes',  label: 'Observações',   icone: 'visibility'  },
    { id: 'relatorios',   label: 'Relatórios',    icone: 'description' },
    { id: 'portfolio',    label: 'Portfólio',     icone: 'palette'     },
    { id: 'financeiro',   label: 'Financeiro',    icone: 'payments'    },
  ];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private alunoService: AlunoService,
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (!id) {
      this.router.navigate(['/pessoas/alunos']);
      return;
    }
    this.carregarAluno(id);
  }

  private carregarAluno(id: number): void {
    this.alunoService.buscarPorId(id).subscribe({
      next: (aluno) => {
        this.aluno.set(aluno);
        this.carregando.set(false);
      },
      error: () => {
        this.erro.set('Aluno não encontrado.');
        this.carregando.set(false);
      },
    });
  }

  editar(): void {
    this.router.navigate(['/pessoas/alunos', this.aluno()?.id, 'editar']);
  }

  voltar(): void {
    this.router.navigate(['/pessoas/alunos']);
  }

  calcularIdade(dataNascimento?: string): number | null {
    if (!dataNascimento) return null;
    const hoje = new Date();
    const nasc  = new Date(dataNascimento);
    let idade = hoje.getFullYear() - nasc.getFullYear();
    const m = hoje.getMonth() - nasc.getMonth();
    if (m < 0 || (m === 0 && hoje.getDate() < nasc.getDate())) idade--;
    return idade;
  }
}
