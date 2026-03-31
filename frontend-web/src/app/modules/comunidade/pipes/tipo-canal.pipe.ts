import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'tipoCanal' })
export class TipoCanalPipe implements PipeTransform {
  private readonly MAP: Record<string, { label: string; icone: string; css: string }> = {
    AVISO:      { label: 'Aviso',      icone: 'campaign',    css: 'text-yellow-600' },
    COMUNICADO: { label: 'Comunicado', icone: 'article',     css: 'text-blue-600'   },
    EVENTO:     { label: 'Evento',     icone: 'event',       css: 'text-green-600'  },
    MUTIRAO:    { label: 'Mutirc3a3o', icone: 'groups',      css: 'text-purple-600' },
    FESTIVAL:   { label: 'Festival',   icone: 'celebration', css: 'text-pink-600'   },
    GERAL:      { label: 'Geral',      icone: 'info',        css: 'text-gray-600'   },
    TURMA:      { label: 'Turma',      icone: 'class',       css: 'text-blue-500'   },
    URGENTE:    { label: 'Urgente',    icone: 'priority_high',css: 'text-red-600'   },
    CARDAPIO:   { label: 'Cardápio',   icone: 'restaurant',  css: 'text-orange-500' },
  };

  transform(tipo: string, campo: 'label' | 'icone' | 'css' = 'label'): string {
    return this.MAP[tipo]?.[campo] ?? (campo === 'label' ? tipo : campo === 'icone' ? 'info' : 'text-gray-500');
  }
}
