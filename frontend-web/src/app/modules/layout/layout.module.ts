import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { MainLayoutComponent } from './main-layout/main-layout.component';
import { SidebarComponent } from './sidebar/sidebar.component';
import { HeaderComponent } from './header/header.component';
import { RoleGuard } from '../../core/auth/role.guard';

const routes: Routes = [
  {
    path: '',
    component: MainLayoutComponent,
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      {
        path: 'dashboard',
        loadChildren: () => import('../dashboard/dashboard.module').then(m => m.DashboardModule),
      },
      {
        path: 'pessoas',
        loadChildren: () => import('../pessoas/pessoas.module').then(m => m.PessoasModule),
        canActivate: [RoleGuard],
        data: { roles: ['ADMIN', 'SECRETARIA', 'DIRETOR'] },
      },
      {
        path: 'pedagogia',
        loadChildren: () => import('../pedagogia/pedagogia.module').then(m => m.PedagogiaModule),
        canActivate: [RoleGuard],
        data: { roles: ['ADMIN', 'SECRETARIA', 'DIRETOR', 'PROFESSOR'] },
      },
      {
        path: 'financeiro',
        loadChildren: () => import('../financeiro/financeiro.module').then(m => m.FinanceiroModule),
        canActivate: [RoleGuard],
        data: { roles: ['ADMIN', 'DIRETOR', 'PAIS'] },
      },
      {
        path: 'comunidade',
        loadChildren: () => import('../comunidade/comunidade.module').then(m => m.ComunidadeModule),
      },
      {
        path: 'lgpd',
        loadChildren: () => import('../lgpd/lgpd.module').then(m => m.LgpdModule),
        canActivate: [RoleGuard],
        data: { roles: ['ADMIN', 'DIRETOR'] },
      },
      {
        path: 'notificacoes',
        loadChildren: () => import('../notificacoes/notificacoes.module').then(m => m.NotificacoesModule),
      },
      { path: '**', redirectTo: 'dashboard' },
    ],
  },
];

@NgModule({
  declarations: [MainLayoutComponent, SidebarComponent, HeaderComponent],
  imports: [CommonModule, FormsModule, RouterModule.forChild(routes)],
})
export class LayoutModule {}
