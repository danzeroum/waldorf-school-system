import { Component, OnInit, signal } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ComunicadoService } from '../../services/comunicado.service';

@Component({
  selector: 'wld-comunicado-form',
  templateUrl: './comunicado-form.component.html',
  standalone: false,
})
export class ComunicadoFormComponent implements OnInit {
  form!: FormGroup;
  salvando = signal(false);
  erro     = signal<string | null>(null);

  constructor(
    private fb: FormBuilder,
    private comunicadoService: ComunicadoService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      assunto:       ['', [Validators.required, Validators.minLength(5)]],
      corpo:         ['', [Validators.required, Validators.minLength(20)]],
      destinatarios: ['TODOS', Validators.required],
      turmaId:       [null],
    });
  }

  enviar(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.salvando.set(true);
    this.comunicadoService.criar(this.form.value).subscribe({
      next: () => { this.salvando.set(false); this.router.navigate(['/comunidade/comunicados']); },
      error: (err) => { this.salvando.set(false); this.erro.set(err.error?.message ?? 'Erro ao enviar.'); },
    });
  }
}
