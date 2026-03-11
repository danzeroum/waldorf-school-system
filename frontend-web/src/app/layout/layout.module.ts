import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatListModule } from '@angular/material/list';
import { LayoutComponent } from './layout.component';

const routes: Routes = [
  {
    path: '',
    component: LayoutComponent,
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', loadChildren: () => import('../modules/dashboard/dashboard.module').then(m => m.DashboardModule) },
      { path: 'pessoas',   loadChildren: () => import('../modules/pessoas/pessoas.module').then(m => m.PessoasModule) },
      { path: 'pedagogia', loadChildren: () => import('../modules/pedagogia/pedagogia.module').then(m => m.PedagogiaModule) },
      { path: 'financeiro',loadChildren: () => import('../modules/financeiro/financeiro.module').then(m => m.FinanceiroModule) },
      { path: 'comunidade',loadChildren: () => import('../modules/comunidade/comunidade.module').then(m => m.ComunidadeModule) },
      { path: 'lgpd',      loadChildren: () => import('../modules/lgpd/lgpd.module').then(m => m.LgpdModule) },
      { path: 'notificacoes', loadChildren: () => import('../modules/notificacoes/notificacoes.module').then(m => m.NotificacoesModule) },
    ]
  }
];

@NgModule({
  declarations: [LayoutComponent],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    MatSidenavModule,
    MatToolbarModule,
    MatIconModule,
    MatButtonModule,
    MatListModule,
  ],
})
export class LayoutModule {}
