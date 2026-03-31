import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { ComunidadeRoutingModule } from './comunidade-routing.module';
import { ComunidadeComponent } from './comunidade.component';
import { MuralComponent } from './mural/mural.component';
import { ComunicadoListComponent } from './comunicado/comunicado-list/comunicado-list.component';
import { ComunicadoFormComponent } from './comunicado/comunicado-form/comunicado-form.component';
import { PortalPaisComponent } from './portal-pais/portal-pais.component';
import { TipoCanalPipe } from './pipes/tipo-canal.pipe';

@NgModule({
  declarations: [
    ComunidadeComponent,
    MuralComponent,
    ComunicadoListComponent,
    ComunicadoFormComponent,
    PortalPaisComponent,
    TipoCanalPipe,
  ],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule,
    ComunidadeRoutingModule,
  ],
})
export class ComunidadeModule {}
