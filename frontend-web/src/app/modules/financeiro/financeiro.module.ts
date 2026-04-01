import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatChipsModule } from '@angular/material/chips';
import { MatCardModule } from '@angular/material/card';
import { MatDividerModule } from '@angular/material/divider';

import { FinanceiroRoutingModule } from './financeiro-routing.module';
import { FinanceiroDashboardComponent } from './dashboard/financeiro-dashboard.component';
import { ContratoListComponent } from './contrato/contrato-list/contrato-list.component';
import { ContratoFormComponent } from './contrato/contrato-form/contrato-form.component';
import { ParcelaListComponent } from './parcela/parcela-list/parcela-list.component';
import { StatusParcelaPipe } from './pipes/status-parcela.pipe';
import { StatusContratoPipe } from './pipes/status-contrato.pipe';

@NgModule({
  declarations: [
    FinanceiroDashboardComponent,
    ContratoListComponent,
    ContratoFormComponent,
    ParcelaListComponent,
    StatusParcelaPipe,
    StatusContratoPipe,
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatSnackBarModule,
    MatTooltipModule,
    MatProgressSpinnerModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatTableModule,
    MatPaginatorModule,
    MatChipsModule,
    MatCardModule,
    MatDividerModule,
    FinanceiroRoutingModule,
  ],
})
export class FinanceiroModule {}
