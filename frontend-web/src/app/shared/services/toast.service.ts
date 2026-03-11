import { Injectable, signal } from '@angular/core';

export type ToastTipo = 'success' | 'error' | 'info' | 'warning';

export interface Toast {
  id:        number;
  tipo:      ToastTipo;
  mensagem:  string;
  duracao:   number;
}

@Injectable({ providedIn: 'root' })
export class ToastService {
  private counter = 0;
  toasts = signal<Toast[]>([]);

  private push(tipo: ToastTipo, mensagem: string, duracao = 4000): void {
    const id = ++this.counter;
    this.toasts.update(list => [...list, { id, tipo, mensagem, duracao }]);
    setTimeout(() => this.remover(id), duracao);
  }

  success(mensagem: string, duracao?: number): void { this.push('success', mensagem, duracao); }
  error  (mensagem: string, duracao?: number): void { this.push('error',   mensagem, duracao ?? 6000); }
  info   (mensagem: string, duracao?: number): void { this.push('info',    mensagem, duracao); }
  warning(mensagem: string, duracao?: number): void { this.push('warning', mensagem, duracao); }

  remover(id: number): void {
    this.toasts.update(list => list.filter(t => t.id !== id));
  }
}
