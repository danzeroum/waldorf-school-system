import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'iniciais', standalone: false })
export class IniciaisPipe implements PipeTransform {
  transform(nome: string | null | undefined): string {
    if (!nome) return '?';
    const partes = nome.trim().split(/\s+/);
    if (partes.length === 1) return partes[0][0].toUpperCase();
    return (partes[0][0] + partes[partes.length - 1][0]).toUpperCase();
  }
}
