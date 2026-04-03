import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AlunoListComponent } from './aluno/aluno-list/aluno-list.component';
import { AlunoWizardComponent } from './aluno/aluno-wizard/aluno-wizard.component';
import { AlunoDetalheComponent } from './aluno/aluno-detalhe/aluno-detalhe.component';
import { ProfessorListComponent } from './professor/professor-list/professor-list.component';

const routes: Routes = [
  { path: 'alunos',            component: AlunoListComponent },
  { path: 'alunos/novo',       component: AlunoWizardComponent },
  { path: 'alunos/:id',        component: AlunoDetalheComponent },
  { path: 'alunos/:id/editar', component: AlunoWizardComponent },
  { path: 'professores',       component: ProfessorListComponent },
  { path: '',                  redirectTo: 'alunos', pathMatch: 'full' },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class PessoaRoutingModule {}
