import { Component, OnInit, signal } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { NotificacaoService, PreferenciaNotificacao } from '../services/notificacao.service';

@Component({
  selector: 'wld-notificacao-preferencias',
  templateUrl: './notificacao-preferencias.component.html',
  standalone: false,
})
export class NotificacaoPreferenciasComponent implements OnInit {
  form!: FormGroup;
  carregando = signal(true);
  salvando   = signal(false);
  salvo      = signal(false);

  constructor(private svc: NotificacaoService, private fb: FormBuilder) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      email:          [true],
      push:           [true],
      sms:            [false],
      inApp:          [true],
      agregacao:      ['IMEDIATO'],
      silencioInicio: [''],
      silencioFim:    [''],
    });
    this.svc.buscarPreferencias().subscribe({
      next:  (p) => { this.form.patchValue(p); this.carregando.set(false); },
      error: ()  => { this.carregando.set(false); },
    });
  }

  salvar(): void {
    this.salvando.set(true);
    this.svc.salvarPreferencias(this.form.value as PreferenciaNotificacao).subscribe({
      next: () => {
        this.salvando.set(false);
        this.salvo.set(true);
        setTimeout(() => this.salvo.set(false), 3000);
      },
      error: () => this.salvando.set(false),
    });
  }
}
