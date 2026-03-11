import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { LgpdRoutingModule } from './lgpd-routing.module';
import { ConsentimentoListComponent } from './consentimento/consentimento-list/consentimento-list.component';
import { SolicitacaoListComponent } from './solicitacao/solicitacao-list/solicitacao-list.component';
import { RelatorioLgpdComponent } from './relatorio/relatorio-lgpd.component';
import { StatusConsentimentoPipe } from './pipes/status-consentimento.pipe';

@NgModule({
  declarations: [
    ConsentimentoListComponent,
    SolicitacaoListComponent,
    RelatorioLgpdComponent,
    StatusConsentimentoPipe,
  ],
  imports: [CommonModule, ReactiveFormsModule, FormsModule, RouterModule, LgpdRoutingModule],
})
export class LgpdModule {}
