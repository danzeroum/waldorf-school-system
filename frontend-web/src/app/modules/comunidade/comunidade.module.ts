import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { ComunidadeRoutingModule } from './comunidade-routing.module';
import { MuralComponent } from './mural/mural.component';
import { ComunicadoListComponent } from './comunicado/comunicado-list/comunicado-list.component';
import { ComunicadoFormComponent } from './comunicado/comunicado-form/comunicado-form.component';
import { PortalPaisComponent } from './portal-pais/portal-pais.component';
import { TipoAvisoPipe } from './pipes/tipo-aviso.pipe';

@NgModule({
  declarations: [
    MuralComponent,
    ComunicadoListComponent,
    ComunicadoFormComponent,
    PortalPaisComponent,
    TipoAvisoPipe,
  ],
  imports: [CommonModule, ReactiveFormsModule, FormsModule, RouterModule, ComunidadeRoutingModule],
})
export class ComunidadeModule {}
