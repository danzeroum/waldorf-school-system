import { Component, OnInit, signal, computed } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { AlunoService } from '../../services/aluno.service';
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

  // Forms por passo
  formPasso1!: FormGroup; // Dados pessoais
  formPasso2!: FormGroup; // Dados médicos
  formPasso3!: FormGroup; // Responsáveis
  formPasso4!: FormGroup; // Matrícula + Endereço

  readonly passos = [
    { titulo: 'Dados Pessoais',  icone: 'person'   },
    { titulo: 'Saúde',           icone: 'favorite' },
    { titulo: 'Responsáveis',    icone: 'group'    },
    { titulo: 'Matrícula',       icone: 'school'   },
  ];

  constructor(
    private fb: FormBuilder,
    private alunoService: AlunoService,
    private cepService: BuscaCepService,
    readonly router: Router,
    private route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.modoEdicao.set(!!this.route.snapshot.paramMap.get('id'));
    this.initForms();
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
      resp1Nome:        ['', Validators.required],
      resp1Parentesco:  ['MAE', Validators.required],
      resp1Cpf:         [''],
      resp1Telefone:    [''],
      resp1Celular:     ['', Validators.required],
      resp1Email:       ['', Validators.email],
      resp1Buscar:      [true],
      resp1Emergencia:  [true],
    });

    this.formPasso4 = this.fb.group({
      turmaId:          ['', Validators.required],
      anoLetivo:        [new Date().getFullYear(), Validators.required],
      formaIngresso:    ['NOVO', Validators.required],
      cep:              [''],
      logradouro:       [''],
      numero:           [''],
      complemento:      [''],
      bairro:           [''],
      cidade:           [''],
      estado:           [''],
      aceiteTermosLgpd: [false, Validators.requiredTrue],
    });
  }

  // === NAVEGAÇÃO ===

  proximo(): void {
    if (this.formAtual.invalid) {
      this.formAtual.markAllAsTouched();
      return;
    }

    if (this.passoAtual() === 0) {
      // Salvar aluno ao avançar do passo 1
      this.salvarPasso1();
      return;
    }

    if (this.passoAtual() === 1 && this.alunoIdCriado()) {
      this.salvarPasso2();
      return;
    }

    this.passoAtual.update(v => v + 1);
  }

  anterior(): void {
    this.passoAtual.update(v => Math.max(0, v - 1));
  }

  get formAtual(): FormGroup {
    return [this.formPasso1, this.formPasso2, this.formPasso3, this.formPasso4][this.passoAtual()];
  }

  get progresso(): number {
    return ((this.passoAtual() + 1) / this.totalPassos) * 100;
  }

  // === SALVAR ===

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
        this.erroSalvar.set(
          err.error?.message ?? 'Erro ao salvar dados pessoais. Tente novamente.'
        );
      },
    });
  }

  private salvarPasso2(): void {
    const id = this.alunoIdCriado();
    if (!id) return;
    this.salvando.set(true);

    this.alunoService.atualizar(id, this.formPasso2.value).subscribe({
      next: () => {
        this.salvando.set(false);
        this.passoAtual.set(2);
      },
      error: () => {
        this.salvando.set(false);
        this.passoAtual.set(2); // não bloquear por dados opcionais
      },
    });
  }

  concluir(): void {
    if (this.formPasso4.invalid) {
      this.formPasso4.markAllAsTouched();
      return;
    }

    const id = this.alunoIdCriado();
    if (!id) return;

    this.salvando.set(true);
    const v4 = this.formPasso4.value;

    this.alunoService.matricular({
      alunoId: id,
      turmaId: +v4.turmaId,
      anoLetivo: +v4.anoLetivo,
      formaIngresso: v4.formaIngresso,
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

  // === CEP ===

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
}
