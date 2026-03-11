import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { MuralComponent } from './mural/mural.component';
import { ComunicadoListComponent } from './comunicado/comunicado-list/comunicado-list.component';
import { ComunicadoFormComponent } from './comunicado/comunicado-form/comunicado-form.component';
import { PortalPaisComponent } from './portal-pais/portal-pais.component';

const routes: Routes = [
  { path: '',              component: MuralComponent },
  { path: 'comunicados',   component: ComunicadoListComponent },
  { path: 'comunicados/novo', component: ComunicadoFormComponent },
  { path: 'portal-pais',   component: PortalPaisComponent },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ComunidadeRoutingModule {}
