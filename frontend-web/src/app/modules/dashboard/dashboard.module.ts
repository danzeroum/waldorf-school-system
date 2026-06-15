import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { SecretariaDashboardComponent } from './containers/secretaria-dashboard/secretaria-dashboard.component';

const routes: Routes = [{ path: '', component: SecretariaDashboardComponent }];

@NgModule({
  declarations: [SecretariaDashboardComponent],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
  ],
})
export class DashboardModule {}
