import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from './core/auth/auth.guard';
import { RoleGuard } from './core/auth/role.guard';

const routes: Routes = [
  // --- Pública ---
  {
    path: 'auth',
    loadChildren: () => import('./modules/auth/auth.module').then(m => m.AuthModule),
  },

  // --- Área autenticada (layout principal) ---
  {
    path: '',
    canActivate: [AuthGuard],
    loadChildren: () => import('./layout/layout.module').then(m => m.LayoutModule),
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },

      {
        path: 'dashboard',
        loadChildren: () => import('./modules/dashboard/dashboard.module').then(m => m.DashboardModule),
      },

      // Pessoas
      {
        path: 'pessoas',
        loadChildren: () => import('./modules/pessoas/pessoas.module').then(m => m.PessoasModule),
        canActivate: [RoleGuard],
        data: { roles: ['ADMIN', 'SECRETARIA', 'DIRETOR'] },
      },

      // Pedagógico
      {
        path: 'pedagogia',
        loadChildren: () => import('./modules/pedagogia/pedagogia.module').then(m => m.PedagogiaModule),
        canActivate: [RoleGuard],
        data: { roles: ['ADMIN', 'SECRETARIA', 'DIRETOR', 'PROFESSOR'] },
      },

      // Financeiro
      {
        path: 'financeiro',
        loadChildren: () => import('./modules/financeiro/financeiro.module').then(m => m.FinanceiroModule),
        canActivate: [RoleGuard],
        data: { roles: ['ADMIN', 'SECRETARIA', 'DIRETOR', 'PAIS'] },
      },

      // Comunidade
      {
        path: 'comunidade',
        loadChildren: () => import('./modules/comunidade/comunidade.module').then(m => m.ComunidadeModule),
      },

      // LGPD
      {
        path: 'lgpd',
        loadChildren: () => import('./modules/lgpd/lgpd.module').then(m => m.LgpdModule),
        canActivate: [RoleGuard],
        data: { roles: ['ADMIN', 'DIRETOR'] },
      },

      // Notificações
      {
        path: 'notificacoes',
        loadChildren: () => import('./modules/notificacoes/notificacoes.module').then(m => m.NotificacoesModule),
      },
    ],
  },

  // Fallback
  { path: '**', redirectTo: 'auth/login' },
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {
    scrollPositionRestoration: 'top',
    enableTracing: false,
  })],
  exports: [RouterModule],
})
export class AppRoutingModule {}
