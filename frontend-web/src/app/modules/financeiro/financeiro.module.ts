import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { FinanceiroComponent } from './financeiro.component';

const routes: Routes = [{ path: '', component: FinanceiroComponent }];

@NgModule({
  declarations: [FinanceiroComponent],
  imports: [CommonModule, RouterModule.forChild(routes)],
})
export class FinanceiroModule {}
