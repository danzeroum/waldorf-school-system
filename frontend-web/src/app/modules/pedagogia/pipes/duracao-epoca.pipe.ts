import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'duracaoEpoca', standalone: false })
export class DuracaoEpocaPipe implements PipeTransform {
  transform(dataInicio: string, dataFim: string): string {
    if (!dataInicio || !dataFim) return '';
    const inicio = new Date(dataInicio);
    const fim    = new Date(dataFim);
    const dias   = Math.round((fim.getTime() - inicio.getTime()) / (1000 * 60 * 60 * 24));
    const semanas = Math.floor(dias / 7);
    if (semanas >= 1) return `${semanas} sem. (${dias} dias)`;
    return `${dias} dias`;
  }
}
