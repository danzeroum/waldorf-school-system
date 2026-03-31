import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ComunicadoService, CreateComunicadoRequest } from '../../services/comunicado.service';

@Component({
  selector: 'wld-comunicado-form',
  templateUrl: './comunicado-form.component.html',
  standalone: false,
})
export class ComunicadoFormComponent implements OnInit {
  form!: FormGroup;
  salvando = false;
  erro = '';

  constructor(
    private fb: FormBuilder,
    private comunicadoService: ComunicadoService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      assunto:      ['', [Validators.required, Validators.minLength(5)]],
      corpo:        ['', [Validators.required, Validators.minLength(10)]],
      destinatarios:['TODOS', Validators.required],
      turmaId:      [null],
    });
  }

  salvar(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.salvando = true;
    const req: CreateComunicadoRequest = this.form.value;
    this.comunicadoService.criar(req).subscribe({
      next:  () => this.router.navigate(['/comunidade/comunicados']),
      error: () => { this.erro = 'Erro ao salvar comunicado.'; this.salvando = false; },
    });
  }
}
