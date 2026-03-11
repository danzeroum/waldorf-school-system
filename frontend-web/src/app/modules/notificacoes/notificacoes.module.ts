import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { NotificacoesComponent } from './notificacoes.component';

const routes: Routes = [{ path: '', component: NotificacoesComponent }];

@NgModule({
  declarations: [NotificacoesComponent],
  imports: [CommonModule, RouterModule.forChild(routes)],
})
export class NotificacoesModule {}
