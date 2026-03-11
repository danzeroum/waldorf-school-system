import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { FinanceiroRoutingModule } from './financeiro-routing.module';
import { FinanceiroDashboardComponent } from './dashboard/financeiro-dashboard.component';
import { ContratoListComponent } from './contrato/contrato-list/contrato-list.component';
import { ContratoFormComponent } from './contrato/contrato-form/contrato-form.component';
import { ParcelaListComponent } from './parcela/parcela-list/parcela-list.component';
import { StatusContratoPipe } from './pipes/status-contrato.pipe';
import { StatusParcelaPipe } from './pipes/status-parcela.pipe';

@NgModule({
  declarations: [
    FinanceiroDashboardComponent,
    ContratoListComponent,
    ContratoFormComponent,
    ParcelaListComponent,
    StatusContratoPipe,
    StatusParcelaPipe,
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    RouterModule,
    FinanceiroRoutingModule,
  ],
})
export class FinanceiroModule {}
