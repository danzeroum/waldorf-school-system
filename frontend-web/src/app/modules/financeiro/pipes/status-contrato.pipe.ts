import { Pipe, PipeTransform } from '@angular/core';

const MAP: Record<string, { label: string; css: string }> = {
  ATIVO:        { label: 'Ativo',        css: 'badge-success' },
  ENCERRADO:    { label: 'Encerrado',    css: 'badge-neutral' },
  SUSPENSO:     { label: 'Suspenso',     css: 'badge-warning' },
  INADIMPLENTE: { label: 'Inadimplente', css: 'badge-danger'  },
};

@Pipe({ name: 'statusContrato', standalone: false })
export class StatusContratoPipe implements PipeTransform {
  transform(value: string, formato: 'label' | 'css' = 'label'): string {
    return MAP[value]?.[formato] ?? value;
  }
}
