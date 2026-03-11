import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { PessoasComponent } from './pessoas.component';

const routes: Routes = [{ path: '', component: PessoasComponent }];

@NgModule({
  declarations: [PessoasComponent],
  imports: [CommonModule, RouterModule.forChild(routes)],
})
export class PessoasModule {}
