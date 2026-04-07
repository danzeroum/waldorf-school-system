import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDialogModule } from '@angular/material/dialog';
import { MatSnackBarModule, MatSnackBar } from '@angular/material/snack-bar';
import { MatMenuModule } from '@angular/material/menu';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { UsuarioService, UsuarioItem } from '../usuario.service';
import { AuthService } from '../../../core/auth/auth.service';

@Component({
  selector: 'wld-usuario-list',
  standalone: false,
  templateUrl: './usuario-list.component.html',
  imports: [CommonModule, FormsModule, MatTableModule, MatPaginatorModule, MatIconModule, MatButtonModule, MatChipsModule, MatTooltipModule, MatDialogModule, MatSnackBarModule, MatMenuModule, MatProgressSpinnerModule],
})
export class UsuarioListComponent implements OnInit {
  usuarios: UsuarioItem[] = [];
  filteredUsuarios: UsuarioItem[] = [];
  loading = true;
  filtro = '';
  displayedColumns = ['nome', 'email', 'perfis', 'status', 'acoes'];
  constructor(private usuarioService: UsuarioService, private authService: AuthService, private router: Router, private snackBar: MatSnackBar) {}
  ngOnInit(): void { this.carregarUsuarios(); }
  carregarUsuarios(): void {
    this.loading = true;
    this.usuarioService.listar().subscribe({
      next: (data) => { this.usuarios = data; this.aplicarFiltro(); this.loading = false; },
      error: () => { this.loading = false; this.snackBar.open('Erro ao carregar usuarios', 'Fechar', {duration: 5000}); }
    });
  }
  aplicarFiltro(): void {
    const t = this.filtro.toLowerCase().trim();
    this.filteredUsuarios = t ? this.usuarios.filter(u => u.nome.toLowerCase().includes(t) || u.email.toLowerCase().includes(t) || u.perfis.some(p => p.toLowerCase().includes(t))) : [...this.usuarios];
  }
  getPerfilLabel(p: string): string {
    const m: Record<string,string> = {ADMIN:'Administrador',SECRETARIA:'Secretaria',DIRETOR:'Diretor',PROFESSOR:'Professor',PAIS:'Pais/Responsavel',RESPONSAVEL:'Responsavel',FINANCEIRO:'Financeiro',COORDENADOR:'Coordenador',FUNCIONARIO:'Funcionario'};
    return m[p] || p;
  }
  getPerfilColor(p: string): string {
    const m: Record<string,string> = {ADMIN:'bg-red-100 text-red-700',SECRETARIA:'bg-blue-100 text-blue-700',DIRETOR:'bg-amber-100 text-amber-700',PROFESSOR:'bg-green-100 text-green-700',PAIS:'bg-gray-100 text-gray-700',RESPONSAVEL:'bg-gray-100 text-gray-700',FINANCEIRO:'bg-blue-100 text-blue-700',COORDENADOR:'bg-amber-100 text-amber-700',FUNCIONARIO:'bg-gray-100 text-gray-700'};
    return m[p] || 'bg-gray-100 text-gray-700';
  }
  toggleAtivo(u: UsuarioItem): void {
    const a = u.ativo ? 'desativar' : 'ativar';
    if (!confirm('Deseja ' + a + ' o usuario "' + u.nome + '"?')) return;
    this.usuarioService.toggleAtivo(u.id).subscribe({ next: () => { this.snackBar.open('Usuario ' + a + 'do', 'Fechar', {duration: 3000}); this.carregarUsuarios(); }, error: () => { this.snackBar.open('Erro', 'Fechar', {duration: 5000}); } });
  }
  resetarSenha(u: UsuarioItem): void {
    const s = prompt('Defina a nova senha para "' + u.nome + '":\n(Minimo 6 caracteres)');
    if (!s || s.length < 6) { if (s !== null) this.snackBar.open('Senha deve ter no minimo 6 caracteres', 'Fechar', {duration: 3000}); return; }
    this.usuarioService.resetarSenha(u.id, s).subscribe({ next: () => { this.snackBar.open('Senha alterada com sucesso', 'Fechar', {duration: 3000}); }, error: () => { this.snackBar.open('Erro ao alterar senha', 'Fechar', {duration: 5000}); } });
  }
  deletar(u: UsuarioItem): void {
    if (!confirm('ATENCAO: Deseja excluir o usuario "' + u.nome + '"? Esta acao nao pode ser desfeita.')) return;
    this.usuarioService.deletar(u.id).subscribe({ next: () => { this.snackBar.open('Usuario excluido', 'Fechar', {duration: 3000}); this.carregarUsuarios(); }, error: () => { this.snackBar.open('Erro ao excluir', 'Fechar', {duration: 5000}); } });
  }
  novo(): void { this.router.navigate(['/usuarios/novo']); }
  editar(id: number): void { this.router.navigate(['/usuarios/' + id + '/editar']); }
  isAdmin(): boolean { return this.authService.temPerfil('ADMIN'); }
}
