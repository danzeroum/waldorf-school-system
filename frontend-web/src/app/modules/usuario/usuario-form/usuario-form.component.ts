import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBarModule, MatSnackBar } from '@angular/material/snack-bar';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { UsuarioService, UsuarioItem } from '../usuario.service';
import { AuthService } from '../../../core/auth/auth.service';

@Component({
  selector: 'wld-usuario-form',
  standalone: false,
  templateUrl: './usuario-form.component.html',
  imports: [CommonModule, FormsModule, ReactiveFormsModule, MatIconModule, MatButtonModule, MatProgressSpinnerModule, MatSnackBarModule, MatCheckboxModule, MatInputModule, MatFormFieldModule],
})
export class UsuarioFormComponent implements OnInit {
  form!: FormGroup;
  isEdit = false;
  usuarioId: number | null = null;
  loading = false;
  saving = false;
  perfisDisponiveis: string[] = [];
  selectedPerfis: string[] = [];
  senhaVisivel = false;
  labelsPerfis: Record<string,string> = {ADMIN:'Administrador',SECRETARIA:'Secretaria',DIRETOR:'Diretor',PROFESSOR:'Professor',PAIS:'Pais / Responsavel',RESPONSAVEL:'Responsavel',FINANCEIRO:'Financeiro',COORDENADOR:'Coordenador',FUNCIONARIO:'Funcionario'};
  constructor(private fb: FormBuilder, private usuarioService: UsuarioService, private authService: AuthService, private route: ActivatedRoute, private router: Router, private snackBar: MatSnackBar) {}
  ngOnInit(): void {
    this.usuarioId = this.route.snapshot.params['id'] || null;
    this.isEdit = !!this.usuarioId;
    this.form = this.fb.group({nome: ['',[Validators.required,Validators.minLength(3),Validators.maxLength(200)]],email: ['',[Validators.required,Validators.email]],senha: ['', this.isEdit ? [] : [Validators.required,Validators.minLength(6)]],ativo: [true]});
    this.carregarPerfis();
    if (this.isEdit && this.usuarioId) this.carregarUsuario(this.usuarioId);
  }
  carregarPerfis(): void { this.usuarioService.listarPerfis().subscribe({next: (d) => {this.perfisDisponiveis = d;}, error: () => {}}); }
  carregarUsuario(id: number): void {
    this.loading = true;
    this.usuarioService.buscarPorId(id).subscribe({next: (d) => {this.form.patchValue({nome:d.nome,email:d.email,ativo:d.ativo}); this.selectedPerfis = [...d.perfis]; this.loading = false;}, error: () => {this.loading = false; this.snackBar.open('Erro ao carregar usuario','Fechar',{duration:5000});}});
  }
  togglePerfil(p: string): void { const i = this.selectedPerfis.indexOf(p); if (i >= 0) this.selectedPerfis.splice(i,1); else this.selectedPerfis.push(p); }
  onSubmit(): void {
    if (this.form.invalid) { this.snackBar.open('Preencha todos os campos obrigatorios','Fechar',{duration:5000}); return; }
    if (this.selectedPerfis.length === 0) { this.snackBar.open('Selecione pelo menos um perfil','Fechar',{duration:5000}); return; }
    this.saving = true;
    const payload: any = {nome:this.form.value.nome,email:this.form.value.email,perfis:this.selectedPerfis,ativo:this.form.value.ativo};
    if (this.form.value.senha && this.form.value.senha.length >= 6) payload.senha = this.form.value.senha;
    const req = this.isEdit ? this.usuarioService.atualizar(this.usuarioId!, payload) : this.usuarioService.criar(payload);
    req.subscribe({next: () => {this.snackBar.open(this.isEdit?'Usuario atualizado':'Usuario criado','Fechar',{duration:3000}); this.router.navigate(['/usuarios']);}, error: (e) => {this.saving = false; this.snackBar.open(e?.error?.message||'Erro ao salvar','Fechar',{duration:5000});}});
  }
  cancelar(): void { this.router.navigate(['/usuarios']); }
  isAdmin(): boolean { return this.authService.temPerfil('ADMIN'); }
}
