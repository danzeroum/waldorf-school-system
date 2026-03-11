import { Pipe, PipeTransform } from '@angular/core';
import { SituacaoAluno } from '@models/pessoa.models';

const LABELS: Record<SituacaoAluno, { label: string; css: string }> = {
  [SituacaoAluno.ATIVO]:       { label: 'Ativo',       css: 'badge-success' },
  [SituacaoAluno.INATIVO]:     { label: 'Inativo',     css: 'badge-neutral' },
  [SituacaoAluno.TRANSFERIDO]: { label: 'Transferido', css: 'badge-info'    },
  [SituacaoAluno.DESLIGADO]:   { label: 'Desligado',   css: 'badge-danger'  },
  [SituacaoAluno.PENDENTE]:    { label: 'Pendente',    css: 'badge-warning' },
};

@Pipe({ name: 'situacaoAluno', standalone: false })
export class SituacaoAlunoPipe implements PipeTransform {
  transform(value: SituacaoAluno, formato: 'label' | 'css' = 'label'): string {
    return LABELS[value]?.[formato] ?? value;
  }
}
