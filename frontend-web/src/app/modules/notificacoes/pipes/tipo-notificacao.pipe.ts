import { Pipe, PipeTransform } from '@angular/core';
import { TipoNotificacao } from '../services/notificacao.service';

const CONFIG: Record<TipoNotificacao, { icone: string; css: string }> = {
  MENSALIDADE_VENCENDO: { icone: 'schedule',   css: 'text-yellow-500' },
  MENSALIDADE_VENCIDA:  { icone: 'warning',    css: 'text-red-500'    },
  NOVA_OBSERVACAO:      { icone: 'edit_note',  css: 'text-blue-500'   },
  COMUNICADO:           { icone: 'campaign',   css: 'text-purple-500' },
  EVENTO:               { icone: 'event',      css: 'text-green-500'  },
  SOLICITACAO_LGPD:     { icone: 'privacy_tip',css: 'text-orange-500' },
  SISTEMA:              { icone: 'info',       css: 'text-gray-500'   },
};

@Pipe({ name: 'tipoNotificacao' })
export class TipoNotificacaoPipe implements PipeTransform {
  transform(tipo: TipoNotificacao, campo: 'icone' | 'css'): string {
    return CONFIG[tipo]?.[campo] ?? (campo === 'icone' ? 'notifications' : 'text-gray-400');
  }
}
