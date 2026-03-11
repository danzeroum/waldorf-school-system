import { Pipe, PipeTransform } from '@angular/core';

const MAP: Record<string, { label: string; css: string }> = {
  PENDENTE:  { label: 'Pendente',  css: 'badge-warning' },
  ACEITO:    { label: 'Aceito',    css: 'badge-success' },
  RECUSADO:  { label: 'Recusado', css: 'badge-danger'  },
  REVOGADO:  { label: 'Revogado', css: 'badge-neutral' },
};

@Pipe({ name: 'statusConsentimento', standalone: false })
export class StatusConsentimentoPipe implements PipeTransform {
  transform(value: string, formato: 'label' | 'css' = 'label'): string {
    return MAP[value]?.[formato] ?? value;
  }
}
