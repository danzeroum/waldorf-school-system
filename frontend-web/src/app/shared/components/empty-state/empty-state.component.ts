import { Component, Input } from '@angular/core';

@Component({
  selector: 'wld-empty-state',
  template: `
    <div class="flex flex-col items-center justify-center py-14 text-center">
      <span class="material-icons text-5xl text-waldorf-gray-200 mb-3">{{ icone }}</span>
      <h3 class="text-base font-semibold text-waldorf-gray-700">{{ titulo }}</h3>
      <p *ngIf="descricao" class="text-sm text-waldorf-gray-400 mt-1 max-w-xs">{{ descricao }}</p>
      <ng-content></ng-content>
    </div>
  `,
  standalone: false,
})
export class EmptyStateComponent {
  @Input() icone      = 'inbox';
  @Input() titulo     = 'Nenhum item encontrado';
  @Input() descricao  = '';
}
