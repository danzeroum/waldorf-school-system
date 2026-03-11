import { Component, OnInit, signal } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { EpocaService } from '../../services/epoca.service';
import { PedagogiaService, Turma } from '../../services/pedagogia.service';

@Component({
  selector: 'wld-epoca-form',
  templateUrl: './epoca-form.component.html',
  standalone: false,
})
export class EpocaFormComponent implements OnInit {
  form!: FormGroup;
  turmas = signal<Turma[]>([]);
  salvando = signal(false);
  erro = signal<string | null>(null);
  modoEdicao = signal(false);
  epocaId = signal<number | null>(null);

  readonly aspectos = [
    { value: 'FISICO',     label: 'Físico',     descricao: 'Corpo físico, movimento, habilidades manuais' },
    { value: 'ANIMICO',    label: 'Anímico',    descricao: 'Emoções, sentimentos, arte, música' },
    { value: 'ESPIRITUAL', label: 'Espiritual', descricao: 'Pensamento, cognição, espiritualidade' },
  ];

  readonly materias = [
    'Português', 'Matemática', 'História', 'Geografia', 'Ciências',
    'Arte', 'Música', 'Educação Física', 'Euritmia', 'Inglês', 'Alemão', 'Outra',
  ];

  constructor(
    private fb: FormBuilder,
    private epocaService: EpocaService,
    private pedagogiaService: PedagogiaService,
    private router: Router,
    private route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    const turmaIdQuery = this.route.snapshot.queryParamMap.get('turmaId');
    this.modoEdicao.set(!!id);
    if (id) this.epocaId.set(+id);

    this.form = this.fb.group({
      titulo:     ['', [Validators.required, Validators.minLength(3)]],
      materia:    ['', Validators.required],
      aspecto:    ['FISICO', Validators.required],
      turmaId:    [turmaIdQuery ?? '', Validators.required],
      dataInicio: ['', Validators.required],
      dataFim:    ['', Validators.required],
      descricao:  [''],
      objetivos:  [''],
    });

    this.pedagogiaService.listarTurmas().subscribe({
      next: (t) => this.turmas.set(t),
      error: () => {},
    });

    if (id) {
      this.epocaService.buscarPorId(+id).subscribe({
        next: (e) => this.form.patchValue(e),
        error: () => {},
      });
    }
  }

  salvar(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.salvando.set(true);
    this.erro.set(null);

    const req$ = this.modoEdicao() && this.epocaId()
      ? this.epocaService.atualizar(this.epocaId()!, this.form.value)
      : this.epocaService.criar(this.form.value);

    req$.subscribe({
      next: (e) => {
        this.salvando.set(false);
        this.router.navigate(['/pedagogia/turmas', e.turmaId]);
      },
      error: (err) => {
        this.salvando.set(false);
        this.erro.set(err.error?.message ?? 'Erro ao salvar época.');
      },
    });
  }

  cancelar(): void {
    this.router.navigate(['/pedagogia/epocas']);
  }
}
