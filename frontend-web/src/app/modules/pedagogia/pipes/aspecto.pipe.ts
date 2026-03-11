import { Pipe, PipeTransform } from '@angular/core';

const ASPECTOS: Record<string, { label: string; css: string; icone: string }> = {
  FISICO:     { label: 'Físico',     css: 'aspect-fisico',     icone: 'directions_run' },
  ANIMICO:    { label: 'Anímico',    css: 'aspect-animico',    icone: 'favorite'       },
  ESPIRITUAL: { label: 'Espiritual', css: 'aspect-espiritual', icone: 'auto_awesome'   },
};

@Pipe({ name: 'aspecto', standalone: false })
export class AspectoPipe implements PipeTransform {
  transform(value: string, formato: 'label' | 'css' | 'icone' = 'label'): string {
    return ASPECTOS[value]?.[formato] ?? value;
  }
}
