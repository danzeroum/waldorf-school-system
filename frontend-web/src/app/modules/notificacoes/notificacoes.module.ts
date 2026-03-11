import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { NotificacoesRoutingModule } from './notificacoes-routing.module';
import { NotificacaoPainelComponent } from './painel/notificacao-painel.component';
import { NotificacaoPreferenciasComponent } from './preferencias/notificacao-preferencias.component';
import { TipoNotificacaoPipe } from './pipes/tipo-notificacao.pipe';

@NgModule({
  declarations: [
    NotificacaoPainelComponent,
    NotificacaoPreferenciasComponent,
    TipoNotificacaoPipe,
  ],
  imports: [CommonModule, ReactiveFormsModule, RouterModule, NotificacoesRoutingModule],
})
export class NotificacoesModule {}
