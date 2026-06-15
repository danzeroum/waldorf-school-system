import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { LgpdRoutingModule } from './lgpd-routing.module';
import { LgpdComponent } from './lgpd.component';
import { ConsentimentoListComponent } from './consentimento/consentimento-list/consentimento-list.component';
import { SolicitacaoListComponent } from './solicitacao/solicitacao-list/solicitacao-list.component';
import { RelatorioLgpdComponent } from './relatorio/relatorio-lgpd.component';
import { StatusSolicitacaoPipe } from './pipes/status-solicitacao.pipe';
import { StatusConsentimentoPipe } from './pipes/status-consentimento.pipe';

@NgModule({
  declarations: [
    LgpdComponent,
    ConsentimentoListComponent,
    SolicitacaoListComponent,
    RelatorioLgpdComponent,
    StatusSolicitacaoPipe,
    StatusConsentimentoPipe,
  ],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    LgpdRoutingModule,
  ],
})
export class LgpdModule {}
