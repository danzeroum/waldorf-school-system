import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
})
export class LoginComponent {
  form: FormGroup;
  loading = false;
  error   = '';

  constructor(
    private fb:          FormBuilder,
    private authService: AuthService,
    private router:      Router
  ) {
    this.form = this.fb.group({
      username: ['', [Validators.required]],
      password: ['', Validators.required],
    });
  }

  submit(): void {
    if (this.form.invalid) return;
    this.loading = true;
    this.error   = '';
    this.authService.login(this.form.value).subscribe({
      next: ()         => this.router.navigate(['/dashboard']),
      error: (e: any)  => {
        this.error   = e?.error?.message || 'Credenciais inválidas. Verifique usuário e senha.';
        this.loading = false;
      },
    });
  }

  get usernameCtrl() { return this.form.get('username'); }
  get passwordCtrl() { return this.form.get('password'); }
}
