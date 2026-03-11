import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from './core/guards/auth.guard';
import { GuestGuard } from './core/guards/guest.guard';
import { RoleGuard } from './core/guards/role.guard';

export const routes: Routes = [
  // Redirecionar raiz
  {
    path: '',
    redirectTo: '/dashboard',
    pathMatch: 'full',
  },

  // Módulo de autenticação (apenas para não autenticados)
  {
    path: 'auth',
    canActivate: [GuestGuard],
    loadChildren: () =>
      import('./modules/auth/auth.module').then(m => m.AuthModule),
  },

  // Layout principal (requer autenticação)
  {
    path: '',
    canActivate: [AuthGuard],
    loadChildren: () =>
      import('./modules/layout/layout.module').then(m => m.LayoutModule),
  },

  // Dashboard por perfil
  {
    path: 'dashboard',
    canActivate: [AuthGuard],
    loadChildren: () =>
      import('./modules/dashboard/dashboard.module').then(m => m.DashboardModule),
  },

  // Módulo Pessoas / Alunos
  {
    path: 'pessoas',
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['ADMIN', 'SECRETARIA'] },
    loadChildren: () =>
      import('./modules/pessoa/pessoa.module').then(m => m.PessoaModule),
  },

  // Módulo Pedagogia
  {
    path: 'pedagogia',
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['ADMIN', 'SECRETARIA', 'PROFESSOR', 'DIRECAO'] },
    loadChildren: () =>
      import('./modules/pedagogia/pedagogia.module').then(m => m.PedagogiaModule),
  },

  // Módulo Financeiro
  {
    path: 'financeiro',
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['ADMIN', 'SECRETARIA'] },
    loadChildren: () =>
      import('./modules/financeiro/financeiro.module').then(m => m.FinanceiroModule),
  },

  // Módulo Comunidade
  {
    path: 'comunidade',
    canActivate: [AuthGuard],
    loadChildren: () =>
      import('./modules/comunidade/comunidade.module').then(m => m.ComunidadeModule),
  },

  // Módulo LGPD
  {
    path: 'lgpd',
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['ADMIN', 'SECRETARIA'] },
    loadChildren: () =>
      import('./modules/lgpd/lgpd.module').then(m => m.LgpdModule),
  },

  // Fallback — página não encontrada
  {
    path: '**',
    loadChildren: () =>
      import('./modules/not-found/not-found.module').then(m => m.NotFoundModule),
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {
    scrollPositionRestoration: 'enabled',
    anchorScrolling: 'enabled',
  })],
  exports: [RouterModule],
})
export class AppRoutingModule { }
