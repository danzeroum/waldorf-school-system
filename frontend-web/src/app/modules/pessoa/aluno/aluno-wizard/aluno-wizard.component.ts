import { Component, OnInit, signal, computed } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { AlunoService } from '../../services/aluno.service';
import { TurmaService, TurmaDTO } from '../../services/turma.service';
import { BuscaCepService } from '../../services/busca-cep.service';

@Component({
  selector: 'wld-aluno-wizard',
  templateUrl: './aluno-wizard.component.html',
  standalone: false,
})
export class AlunoWizardComponent implements OnInit {
  passoAtual = signal(0);
  totalPassos = 4;
  salvando = signal(false);
  erroSalvar = signal<string | null>(null);
  alunoIdCriado = signal<number | null>(null);
  buscandoCep = signal(false);
  modoEdicao = signal(false);
  carregando = signal(false);

  turmas = signal<TurmaDTO[]>([]);
  carregandoTurmas = signal(false);

  formPasso1!: FormGroup;
  formPasso2!: FormGroup;
  formPasso3!: FormGroup;
  formPasso4!: FormGroup;

  readonly passos = [
    { titulo: 'Dados Pessoais', icone: 'person'   },
    { titulo: 'Saúde',          icone: 'favorite' },
    { titulo: 'Responsáveis',   icone: 'group'    },
    { titulo: 'Matrícula',      icone: 'school'   },
  ];

  constructor(
    private fb: FormBuilder,
    private alunoService: AlunoService,
    private turmaService: TurmaService,
    private cepService: BuscaCepService,
    readonly router: Router,
    private route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    this.modoEdicao.set(!!idParam);
    this.initForms();
    this.carregarTurmas();

    if (idParam) {
      const id = +idParam;
      this.carregando.set(true);
      this.alunoService.buscarPorId(id).subscribe({
        next: (aluno: any) => {
          this.alunoIdCriado.set(aluno.id);
          this.formPasso1.patchValue({
            nomeCompleto:   aluno.nomeCompleto  || aluno.nome || '',
            nomeSocial:     aluno.nomeSocial    || '',
            dataNascimento: aluno.dataNascimento || '',
            cpf:            aluno.cpf           || '',
            nomePai:        aluno.nomePai       || '',
            nomeMae:        aluno.nomeMae       || '',
            naturalidade:   aluno.naturalidade  || '',
            nacionalidade:  aluno.nacionalidade || 'Brasileira',
          });
          this.formPasso2.patchValue({
            tipoSanguineo:           aluno.tipoSanguineo           || '',
            planoSaude:              aluno.planoSaude              || '',
            alergias:                aluno.alergias                || '',
            medicamentosControlados: aluno.medicamentosControlados || '',
            necessidadesEspeciais:   aluno.necessidadesEspeciais   || '',
            observacoesMedicas:      aluno.observacoesMedicas      || aluno.observacoes || '',
          });
          this.formPasso4.patchValue({
            turmaId:       aluno.turmaAtual?.id   || aluno.turmaId    || '',
            anoLetivo:     aluno.anoIngresso      || new Date().getFullYear(),
            formaIngresso: aluno.formaIngresso    || 'NOVO',
            cep:           aluno.enderecoCep      || '',
            logradouro:    aluno.enderecoRua      || '',
            numero:        aluno.enderecoNumero   || '',
            complemento:   aluno.enderecoComplemento || '',
            bairro:        aluno.enderecoBairro   || '',
            cidade:        aluno.enderecoCidade   || '',
            estado:        aluno.enderecoEstado   || '',
            aceiteTermosLgpd: true,
          });
          this.carregando.set(false);
        },
        error: () => {
          this.carregando.set(false);
          this.erroSalvar.set('Não foi possível carregar os dados do aluno.');
        },
      });
    }
  }

  // =============================================
  // NAVEGAÇÃO LIVRE: clique direto na aba
  // =============================================

  /** Vai para qualquer aba sem validação */
  irParaPasso(i: number): void {
    this.erroSalvar.set(null);
    this.passoAtual.set(i);
  }

  anterior(): void {
    this.passoAtual.update(v => Math.max(0, v - 1));
  }

  proximo(): void {
    // No passo 0 em modo novo, precisa salvar para obter o ID do aluno
    if (this.passoAtual() === 0 && !this.modoEdicao() && !this.alunoIdCriado()) {
      if (this.formPasso1.invalid) {
        this.formPasso1.markAllAsTouched();
        return;
      }
      this.salvarPasso1();
      return;
    }
    // No passo 0 em modo edição: atualiza antes de avançar
    if (this.passoAtual() === 0 && this.modoEdicao()) {
      if (this.formPasso1.invalid) {
        this.formPasso1.markAllAsTouched();
        return;
      }
      this.atualizarPasso1();
      return;
    }
    // Demais passos: navega livremente
    this.passoAtual.update(v => Math.min(this.totalPassos - 1, v + 1));
  }

  /** Salvar passo atual sem avançar de aba */
  salvarAtual(): void {
    const form = this.formAtual;
    if (form.invalid) {
      form.markAllAsTouched();
      return;
    }
    const id = this.alunoIdCriado();
    if (!id) return;
    this.salvando.set(true);
    this.alunoService.atualizar(id, form.value).subscribe({
      next: () => {
        this.salvando.set(false);
        this.erroSalvar.set(null);
      },
      error: (err: any) => {
        this.salvando.set(false);
        this.erroSalvar.set(err.error?.message ?? 'Erro ao salvar.');
      },
    });
  }

  get formAtual(): FormGroup {
    return [this.formPasso1, this.formPasso2, this.formPasso3, this.formPasso4][this.passoAtual()];
  }

  get progresso(): number {
    return ((this.passoAtual() + 1) / this.totalPassos) * 100;
  }

  /** Indica se uma aba tem campos obrigatórios inválidos já tocados */
  passoComErro(i: number): boolean {
    const forms = [this.formPasso1, this.formPasso2, this.formPasso3, this.formPasso4];
    const f = forms[i];
    return f?.invalid && f?.dirty;
  }

  // =============================================
  // SALVAR / API
  // =============================================

  private salvarPasso1(): void {
    this.salvando.set(true);
    this.erroSalvar.set(null);
    this.alunoService.criar(this.formPasso1.value).subscribe({
      next: (aluno) => {
        this.alunoIdCriado.set(aluno.id);
        this.salvando.set(false);
        this.passoAtual.set(1);
      },
      error: (err) => {
        this.salvando.set(false);
        this.erroSalvar.set(err.error?.message ?? 'Erro ao salvar dados pessoais.');
      },
    });
  }

  private atualizarPasso1(): void {
    const id = this.alunoIdCriado();
    if (!id) return;
    this.salvando.set(true);
    this.erroSalvar.set(null);
    this.alunoService.atualizar(id, this.formPasso1.value).subscribe({
      next: () => {
        this.salvando.set(false);
        this.passoAtual.set(1);
      },
      error: (err) => {
        this.salvando.set(false);
        this.erroSalvar.set(err.error?.message ?? 'Erro ao atualizar dados pessoais.');
      },
    });
  }

  concluir(): void {
    if (this.formPasso4.invalid) {
      this.formPasso4.markAllAsTouched();
      return;
    }
    const id = this.alunoIdCriado();
    if (!id) {
      this.erroSalvar.set('Salve os Dados Pessoais antes de concluir a matrícula.');
      this.passoAtual.set(0);
      return;
    }
    this.salvando.set(true);
    const v4 = this.formPasso4.value;
    this.alunoService.matricular({
      alunoId:          id,
      turmaId:          +v4.turmaId,
      anoLetivo:        +v4.anoLetivo,
      formaIngresso:    v4.formaIngresso,
      aceiteTermosLgpd: v4.aceiteTermosLgpd,
    }).subscribe({
      next: () => {
        this.salvando.set(false);
        this.router.navigate(['/pessoas/alunos', id]);
      },
      error: (err) => {
        this.salvando.set(false);
        this.erroSalvar.set(err.error?.message ?? 'Erro ao finalizar matrícula.');
      },
    });
  }

  // =============================================
  // TURMAS / CEP
  // =============================================

  private carregarTurmas(): void {
    this.carregandoTurmas.set(true);
    this.turmaService.listar().subscribe({
      next: (lista) => { this.turmas.set(lista); this.carregandoTurmas.set(false); },
      error: () => { this.carregandoTurmas.set(false); },
    });
  }

  onCepChange(cep: string): void {
    if (cep.replace(/\D/g, '').length !== 8) return;
    this.buscandoCep.set(true);
    this.cepService.buscar(cep).subscribe(end => {
      this.buscandoCep.set(false);
      if (end) {
        this.formPasso4.patchValue({
          logradouro: end.logradouro,
          bairro:     end.bairro,
          cidade:     end.cidade,
          estado:     end.estado,
        });
      }
    });
  }

  private initForms(): void {
    this.formPasso1 = this.fb.group({
      nomeCompleto:  ['', [Validators.required, Validators.minLength(3)]],
      nomeSocial:    [''],
      dataNascimento:['', Validators.required],
      cpf:           [''],
      nomePai:       [''],
      nomeMae:       ['', Validators.required],
      naturalidade:  [''],
      nacionalidade: ['Brasileira'],
    });
    this.formPasso2 = this.fb.group({
      tipoSanguineo:           [''],
      planoSaude:              [''],
      alergias:                [''],
      medicamentosControlados: [''],
      necessidadesEspeciais:   [''],
      observacoesMedicas:      [''],
    });
    this.formPasso3 = this.fb.group({
      resp1Nome:       [''],
      resp1Parentesco: ['MAE'],
      resp1Cpf:        [''],
      resp1Telefone:   [''],
      resp1Celular:    [''],
      resp1Email:      ['', Validators.email],
      resp1Buscar:     [true],
      resp1Emergencia: [true],
    });
    this.formPasso4 = this.fb.group({
      turmaId:         ['', Validators.required],
      anoLetivo:       [new Date().getFullYear(), Validators.required],
      formaIngresso:   ['NOVO', Validators.required],
      cep:             [''],
      logradouro:      [''],
      numero:          [''],
      complemento:     [''],
      bairro:          [''],
      cidade:          [''],
      estado:          [''],
      aceiteTermosLgpd:[false, Validators.requiredTrue],
    });
  }
}
