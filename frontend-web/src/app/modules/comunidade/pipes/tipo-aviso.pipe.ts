import { Pipe, PipeTransform } from '@angular/core';
import { TipoAviso } from '../services/aviso.service';

// FIX: adicionados FESTIVAL e MUTIRAO para alinhar com TipoAviso expandido
const MAP: Record<TipoAviso, { label: string; css: string; icone: string }> = {
  GERAL:    { label: 'Geral',     css: 'badge-info',    icone: 'campaign'      },
  TURMA:    { label: 'Turma',     css: 'badge-success', icone: 'school'        },
  URGENTE:  { label: 'Urgente',   css: 'badge-danger',  icone: 'priority_high' },
  EVENTO:   { label: 'Evento',    css: 'badge-warning', icone: 'event'         },
  CARDAPIO: { label: 'Cardápio',  css: 'badge-neutral', icone: 'restaurant'    },
  FESTIVAL: { label: 'Festival',  css: 'badge-purple',  icone: 'celebration'   },
  MUTIRAO:  { label: 'Mutiron',  css: 'badge-orange',  icone: 'groups'        },
};

@Pipe({ name: 'tipoAviso', standalone: false })
export class TipoAvisoPipe implements PipeTransform {
  transform(value: TipoAviso, formato: 'label' | 'css' | 'icone' = 'label'): string {
    return MAP[value]?.[formato] ?? value;
  }
}
