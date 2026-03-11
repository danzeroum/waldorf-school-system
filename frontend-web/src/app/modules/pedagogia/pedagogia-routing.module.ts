import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { TurmaListComponent } from './turma/turma-list/turma-list.component';
import { TurmaDetalheComponent } from './turma/turma-detalhe/turma-detalhe.component';
import { EpocaListComponent } from './epoca/epoca-list/epoca-list.component';
import { EpocaFormComponent } from './epoca/epoca-form/epoca-form.component';
import { ObservacaoPedagogicaComponent } from './observacao/observacao-pedagogica.component';

const routes: Routes = [
  { path: 'turmas',                         component: TurmaListComponent },
  { path: 'turmas/:id',                     component: TurmaDetalheComponent },
  { path: 'epocas',                         component: EpocaListComponent },
  { path: 'epocas/nova',                    component: EpocaFormComponent },
  { path: 'epocas/:id/editar',              component: EpocaFormComponent },
  { path: 'observacoes/:alunoId',           component: ObservacaoPedagogicaComponent },
  { path: '', redirectTo: 'turmas', pathMatch: 'full' },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class PedagogiaRoutingModule {}
