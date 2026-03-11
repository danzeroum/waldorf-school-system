import { Component, inject } from '@angular/core';
import { ToastService, Toast } from '../../services/toast.service';

@Component({
  selector: 'wld-toast',
  templateUrl: './toast.component.html',
  standalone: false,
})
export class ToastComponent {
  toastService = inject(ToastService);

  icone(tipo: Toast['tipo']): string {
    return { success: 'check_circle', error: 'error', info: 'info', warning: 'warning' }[tipo];
  }

  css(tipo: Toast['tipo']): string {
    return {
      success: 'bg-green-50 border-green-200 text-green-800',
      error:   'bg-red-50 border-red-200 text-red-800',
      info:    'bg-blue-50 border-blue-200 text-blue-800',
      warning: 'bg-amber-50 border-amber-200 text-amber-800',
    }[tipo];
  }

  iconeCss(tipo: Toast['tipo']): string {
    return { success: 'text-green-500', error: 'text-red-500', info: 'text-blue-500', warning: 'text-amber-500' }[tipo];
  }
}
