import { Pipe, PipeTransform } from '@angular/core';

const MAP: Record<string, { label: string; css: string }> = {
  PENDENTE:  { label: 'Pendente',  css: 'badge-warning' },
  PAGA:      { label: 'Paga',      css: 'badge-success' },
  VENCIDA:   { label: 'Vencida',   css: 'badge-danger'  },
  CANCELADA: { label: 'Cancelada', css: 'badge-neutral' },
  PARCIAL:   { label: 'Parcial',   css: 'badge-info'    },
};

@Pipe({ name: 'statusParcela', standalone: false })
export class StatusParcelaPipe implements PipeTransform {
  transform(value: string, formato: 'label' | 'css' = 'label'): string {
    return MAP[value]?.[formato] ?? value;
  }
}
