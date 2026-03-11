import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { FinanceiroDashboardComponent } from './dashboard/financeiro-dashboard.component';
import { ContratoListComponent } from './contrato/contrato-list/contrato-list.component';
import { ContratoFormComponent } from './contrato/contrato-form/contrato-form.component';
import { ParcelaListComponent } from './parcela/parcela-list/parcela-list.component';

const routes: Routes = [
  { path: '',                   component: FinanceiroDashboardComponent },
  { path: 'contratos',          component: ContratoListComponent },
  { path: 'contratos/novo',     component: ContratoFormComponent },
  { path: 'contratos/:id',      component: ContratoFormComponent },
  { path: 'parcelas',           component: ParcelaListComponent },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class FinanceiroRoutingModule {}
