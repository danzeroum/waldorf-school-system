import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { RoleGuard } from '../../core/guards/role.guard';
import { SecretariaDashboardComponent } from './containers/secretaria-dashboard/secretaria-dashboard.component';
import { ProfessorDashboardComponent } from './containers/professor-dashboard/professor-dashboard.component';
import { PaisDashboardComponent } from './containers/pais-dashboard/pais-dashboard.component';

const routes: Routes = [
  {
    path: 'secretaria',
    component: SecretariaDashboardComponent,
    canActivate: [RoleGuard],
    data: { roles: ['ADMIN', 'SECRETARIA'] },
  },
  {
    path: 'professor',
    component: ProfessorDashboardComponent,
    canActivate: [RoleGuard],
    data: { roles: ['ADMIN', 'PROFESSOR', 'DIRECAO'] },
  },
  {
    path: 'pais',
    component: PaisDashboardComponent,
    canActivate: [RoleGuard],
    data: { roles: ['PAIS'] },
  },
  {
    path: '',
    redirectTo: 'secretaria',
    pathMatch: 'full',
  },
];

@NgModule({
  declarations: [
    SecretariaDashboardComponent,
    ProfessorDashboardComponent,
    PaisDashboardComponent,
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
  ],
})
export class DashboardModule { }
