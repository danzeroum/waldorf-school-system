import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { PedagogiaComponent } from './pedagogia.component';

const routes: Routes = [{ path: '', component: PedagogiaComponent }];

@NgModule({
  declarations: [PedagogiaComponent],
  imports: [CommonModule, RouterModule.forChild(routes)],
})
export class PedagogiaModule {}
