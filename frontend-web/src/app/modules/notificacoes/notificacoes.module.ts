import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NotificacoesRoutingModule } from './notificacoes-routing.module';
import { NotificacoesComponent } from './notificacoes.component';
import { NotificacaoPainelComponent } from './painel/notificacao-painel.component';
import { NotificacaoPreferenciasComponent } from './preferencias/notificacao-preferencias.component';
import { TipoNotificacaoPipe } from './pipes/tipo-notificacao.pipe';

@NgModule({
  declarations: [
    NotificacoesComponent,
    NotificacaoPainelComponent,
    NotificacaoPreferenciasComponent,
    TipoNotificacaoPipe,
  ],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    NotificacoesRoutingModule,
  ],
})
export class NotificacoesModule {}
