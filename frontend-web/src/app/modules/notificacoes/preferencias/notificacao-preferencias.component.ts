import { Component, OnInit, signal } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import { NotificacaoService, PreferenciaNotificacao } from '../services/notificacao.service';
import { ToastService } from '@shared/services/toast.service';

@Component({
  selector: 'wld-notificacao-preferencias',
  templateUrl: './notificacao-preferencias.component.html',
  standalone: false,
})
export class NotificacaoPreferenciasComponent implements OnInit {
  form!: FormGroup;
  carregando = signal(true);
  salvando   = signal(false);

  constructor(
    private fb: FormBuilder,
    private notificacaoService: NotificacaoService,
    private toastService: ToastService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      email:          [true],
      push:           [true],
      sms:            [false],
      inApp:          [true],
      agregacao:      ['IMEDIATO'],
      silencioInicio: ['22:00'],
      silencioFim:    ['07:00'],
    });

    this.notificacaoService.buscarPreferencias().subscribe({
      next: (p) => { this.form.patchValue(p); this.carregando.set(false); },
      error: ()  => this.carregando.set(false),
    });
  }

  salvar(): void {
    this.salvando.set(true);
    this.notificacaoService.salvarPreferencias(this.form.value as PreferenciaNotificacao).subscribe({
      next: () => {
        this.salvando.set(false);
        this.toastService.success('Preferências salvas com sucesso!');
      },
      error: () => {
        this.salvando.set(false);
        this.toastService.error('Erro ao salvar preferências.');
      },
    });
  }
}
