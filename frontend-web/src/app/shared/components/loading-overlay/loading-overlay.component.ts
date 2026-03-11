import { Component, inject } from '@angular/core';
import { LoadingService } from '../../services/loading.service';

@Component({
  selector: 'wld-loading-overlay',
  template: `
    <div *ngIf="loadingService.ativo()"
         class="fixed inset-0 bg-white/70 backdrop-blur-sm flex items-center justify-center z-[9998]">
      <div class="flex flex-col items-center gap-3">
        <div class="w-10 h-10 border-4 border-waldorf-cream-200 border-t-waldorf-green-500 rounded-full animate-spin"></div>
        <p class="text-sm text-waldorf-gray-600 font-medium">Carregando...</p>
      </div>
    </div>
  `,
  standalone: false,
})
export class LoadingOverlayComponent {
  loadingService = inject(LoadingService);
}
