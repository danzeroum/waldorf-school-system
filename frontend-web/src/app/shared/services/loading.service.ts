import { Injectable, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class LoadingService {
  private contador = 0;
  ativo = signal(false);

  iniciar(): void {
    this.contador++;
    this.ativo.set(true);
  }

  encerrar(): void {
    this.contador = Math.max(0, this.contador - 1);
    if (this.contador === 0) this.ativo.set(false);
  }
}
