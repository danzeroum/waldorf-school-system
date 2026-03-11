import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { NotificacaoPainelComponent } from './painel/notificacao-painel.component';
import { NotificacaoPreferenciasComponent } from './preferencias/notificacao-preferencias.component';

const routes: Routes = [
  { path: '',              component: NotificacaoPainelComponent },
  { path: 'preferencias',  component: NotificacaoPreferenciasComponent },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class NotificacoesRoutingModule {}
