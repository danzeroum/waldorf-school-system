import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { ComunidadeComponent } from './comunidade.component';

const routes: Routes = [{ path: '', component: ComunidadeComponent }];

@NgModule({
  declarations: [ComunidadeComponent],
  imports: [CommonModule, RouterModule.forChild(routes)],
})
export class ComunidadeModule {}
