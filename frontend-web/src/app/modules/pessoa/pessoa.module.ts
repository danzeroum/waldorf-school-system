import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { PessoaRoutingModule } from './pessoa-routing.module';
import { AlunoListComponent } from './aluno/aluno-list/aluno-list.component';
import { AlunoWizardComponent } from './aluno/aluno-wizard/aluno-wizard.component';
import { AlunoDetalheComponent } from './aluno/aluno-detalhe/aluno-detalhe.component';
import { ProfessorListComponent } from './professor/professor-list/professor-list.component';
import { SituacaoAlunoPipe } from './pipes/situacao-aluno.pipe';
import { ParentescoPipe } from './pipes/parentesco.pipe';

@NgModule({
  declarations: [
    AlunoListComponent,
    AlunoWizardComponent,
    AlunoDetalheComponent,
    ProfessorListComponent,
    SituacaoAlunoPipe,
    ParentescoPipe,
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    RouterModule,
    PessoaRoutingModule,
    MatSnackBarModule,
  ],
})
export class PessoaModule {}
