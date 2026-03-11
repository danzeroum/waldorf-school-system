import { Pipe, PipeTransform } from '@angular/core';
import { TipoNotificacao } from '../services/notificacao.service';

const MAP: Record<TipoNotificacao, { label: string; icone: string; css: string }> = {
  MENSALIDADE_VENCENDO: { label: 'Mensalidade',    icone: 'schedule',       css: 'text-amber-500'  },
  MENSALIDADE_VENCIDA:  { label: 'Vencida',         icone: 'warning',        css: 'text-red-500'    },
  NOVA_OBSERVACAO:      { label: 'Observação',      icone: 'edit_note',      css: 'text-blue-500'   },
  COMUNICADO:           { label: 'Comunicado',      icone: 'campaign',       css: 'text-purple-500' },
  EVENTO:               { label: 'Evento',           icone: 'event',          css: 'text-green-500'  },
  SOLICITACAO_LGPD:     { label: 'LGPD',             icone: 'security',       css: 'text-gray-600'   },
  SISTEMA:              { label: 'Sistema',          icone: 'info',           css: 'text-gray-400'   },
};

@Pipe({ name: 'tipoNotificacao', standalone: false })
export class TipoNotificacaoPipe implements PipeTransform {
  transform(value: TipoNotificacao, formato: 'label' | 'icone' | 'css' = 'label'): string {
    return MAP[value]?.[formato] ?? value;
  }
}
