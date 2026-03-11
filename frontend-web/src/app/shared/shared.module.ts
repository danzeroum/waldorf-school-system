import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';

import { ToastComponent } from './components/toast/toast.component';
import { LoadingOverlayComponent } from './components/loading-overlay/loading-overlay.component';
import { EmptyStateComponent } from './components/empty-state/empty-state.component';

@NgModule({
  declarations: [
    ToastComponent,
    LoadingOverlayComponent,
    EmptyStateComponent,
  ],
  imports: [CommonModule, RouterModule, ReactiveFormsModule, FormsModule],
  exports: [
    CommonModule,
    RouterModule,
    ReactiveFormsModule,
    FormsModule,
    ToastComponent,
    LoadingOverlayComponent,
    EmptyStateComponent,
  ],
})
export class SharedModule {}
