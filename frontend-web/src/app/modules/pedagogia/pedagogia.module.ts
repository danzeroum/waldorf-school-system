import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTooltipModule } from '@angular/material/tooltip';

import { PedagogiaRoutingModule } from './pedagogia-routing.module';
import { TurmaListComponent } from './turma/turma-list/turma-list.component';
import { TurmaDetalheComponent } from './turma/turma-detalhe/turma-detalhe.component';
import { EpocaListComponent } from './epoca/epoca-list/epoca-list.component';
import { EpocaFormComponent } from './epoca/epoca-form/epoca-form.component';
import { ObservacaoPedagogicaComponent } from './observacao/observacao-pedagogica.component';
import { AspectoPipe } from './pipes/aspecto.pipe';
import { DuracaoEpocaPipe } from './pipes/duracao-epoca.pipe';

@NgModule({
  declarations: [
    TurmaListComponent,
    TurmaDetalheComponent,
    EpocaListComponent,
    EpocaFormComponent,
    ObservacaoPedagogicaComponent,
    AspectoPipe,
    DuracaoEpocaPipe,
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatSnackBarModule,
    MatTooltipModule,
    PedagogiaRoutingModule,
  ],
})
export class PedagogiaModule {}
