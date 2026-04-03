import { Component, OnInit } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
})
export class LoginComponent implements OnInit {
  form!: FormGroup;
  loading = false;
  errorMessage: string | null = null;
  showPassword = false;

  constructor(
    private fb: FormBuilder,
    private auth: AuthService,
    private router: Router,
    private route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    if (this.auth.isAuthenticated()) {
      this.router.navigate(['/dashboard']);
      return;
    }
    this.form = this.fb.group({
      username: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      rememberMe: [false],
    });
  }

  get emailInvalid(): boolean {
    const c = this.form.get('username');
    return !!(c?.invalid && (c.dirty || c.touched));
  }

  get senhaInvalid(): boolean {
    const c = this.form.get('password');
    return !!(c?.invalid && (c.dirty || c.touched));
  }

  togglePassword(): void {
    this.showPassword = !this.showPassword;
  }

  onSubmit(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.errorMessage = null;
    this.loading = true;

    const payload = {
      email: this.form.value.username,
      password: this.form.value.password,
      deviceType: 'WEB' as const,
    };

    this.auth.login(payload).subscribe({
      next: () => {
        const returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/dashboard';
        this.router.navigateByUrl(returnUrl);
      },
      error: (err) => {
        this.errorMessage = this.translateError(err.status);
        this.loading = false;
      },
    });
  }

  private translateError(status: number): string {
    const map: Record<number, string> = {
      0:   'Servidor indisponível. Verifique sua conexão.',
      401: 'E-mail ou senha incorretos.',
      403: 'Acesso negado.',
      423: 'Conta bloqueada. Entre em contato com a secretaria.',
      429: 'Muitas tentativas. Aguarde alguns minutos.',
    };
    return map[status] || 'Erro ao fazer login. Tente novamente.';
  }
}
