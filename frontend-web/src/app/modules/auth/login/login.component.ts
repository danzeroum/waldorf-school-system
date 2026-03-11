import { Component, OnInit, signal } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { LoginRequest } from '../../../shared/models/auth.models';

@Component({
  selector: 'wld-login',
  templateUrl: './login.component.html',
  standalone: false,
})
export class LoginComponent implements OnInit {
  form!: FormGroup;
  mostrarSenha = signal(false);
  carregando = signal(false);
  erroLogin = signal<string | null>(null);

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      login: ['', [Validators.required, Validators.minLength(3)]],
      senha: ['', [Validators.required, Validators.minLength(6)]],
      lembrarMe: [false],
    });
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.carregando.set(true);
    this.erroLogin.set(null);

    const request: LoginRequest = this.form.value;

    this.authService.login(request).subscribe({
      next: () => {
        this.carregando.set(false);
        const returnUrl = sessionStorage.getItem('waldorf_return_url') || this.authService.rotaInicial();
        sessionStorage.removeItem('waldorf_return_url');
        this.router.navigateByUrl(returnUrl);
      },
      error: (err) => {
        this.carregando.set(false);
        if (err.status === 401) {
          this.erroLogin.set('Usuário ou senha inválidos. Verifique suas credenciais.');
        } else if (err.status === 423) {
          this.erroLogin.set('Conta bloqueada. Entre em contato com a secretaria.');
        } else if (err.status === 0) {
          this.erroLogin.set('Sem conexão com o servidor. Tente novamente.');
        } else {
          this.erroLogin.set('Ocorreu um erro inesperado. Tente novamente.');
        }
      },
    });
  }

  toggleSenha(): void {
    this.mostrarSenha.update(v => !v);
  }

  get loginCtrl() { return this.form.get('login')!; }
  get senhaCtrl() { return this.form.get('senha')!; }
}
