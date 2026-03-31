import { Pipe, PipeTransform } from '@angular/core';

type StatusSolicitacao = 'PENDENTE' | 'EM_ANALISE' | 'CONCLUIDA' | 'NEGADA';

const LABEL: Record<StatusSolicitacao, string> = {
  PENDENTE:   'Pendente',
  EM_ANALISE: 'Em Análise',
  CONCLUIDA:  'Concluída',
  NEGADA:     'Negada',
};

const CSS: Record<StatusSolicitacao, string> = {
  PENDENTE:   'badge-warning',
  EM_ANALISE: 'badge-info',
  CONCLUIDA:  'badge-success',
  NEGADA:     'badge-danger',
};

@Pipe({ name: 'statusSolicitacao' })
export class StatusSolicitacaoPipe implements PipeTransform {
  transform(status: StatusSolicitacao, campo: 'label' | 'css' = 'label'): string {
    return campo === 'css' ? (CSS[status] ?? 'badge-default') : (LABEL[status] ?? status);
  }
}
