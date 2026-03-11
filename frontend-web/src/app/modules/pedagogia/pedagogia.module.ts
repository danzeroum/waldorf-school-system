import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { PedagogiaRoutingModule } from './pedagogia-routing.module';
import { TurmaListComponent } from './turma/turma-list/turma-list.component';
import { TurmaDetalheComponent } from './turma/turma-detalhe/turma-detalhe.component';
import { EpocaListComponent } from './epoca/epoca-list/epoca-list.component';
import { EpocaFormComponent } from './epoca/epoca-form/epoca-form.component';
import { ObservacaoPedagogicaComponent } from './observacao/observacao-pedagogica.component';
import { DuracaoEpocaPipe } from './pipes/duracao-epoca.pipe';
import { AspectoPipe } from './pipes/aspecto.pipe';

@NgModule({
  declarations: [
    TurmaListComponent,
    TurmaDetalheComponent,
    EpocaListComponent,
    EpocaFormComponent,
    ObservacaoPedagogicaComponent,
    DuracaoEpocaPipe,
    AspectoPipe,
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    RouterModule,
    PedagogiaRoutingModule,
  ],
})
export class PedagogiaModule {}
