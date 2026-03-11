import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ConsentimentoListComponent } from './consentimento/consentimento-list/consentimento-list.component';
import { SolicitacaoListComponent } from './solicitacao/solicitacao-list/solicitacao-list.component';
import { RelatorioLgpdComponent } from './relatorio/relatorio-lgpd.component';

const routes: Routes = [
  { path: '',              component: ConsentimentoListComponent },
  { path: 'solicitacoes',  component: SolicitacaoListComponent },
  { path: 'relatorio',     component: RelatorioLgpdComponent },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class LgpdRoutingModule {}
