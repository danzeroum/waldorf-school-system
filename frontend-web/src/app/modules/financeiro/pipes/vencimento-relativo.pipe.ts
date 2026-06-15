import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'vencimentoRelativo', standalone: false })
export class VencimentoRelativoPipe implements PipeTransform {
  transform(dataVencimento: string | null | undefined): string {
    if (!dataVencimento) return '—';
    const hoje = new Date();
    hoje.setHours(0, 0, 0, 0);
    const venc = new Date(dataVencimento + 'T00:00:00');
    const diff = Math.round((venc.getTime() - hoje.getTime()) / (1000 * 60 * 60 * 24));
    if (diff === 0) return 'hoje';
    if (diff > 0) return `vence em ${diff}d`;
    return `há ${Math.abs(diff)}d`;
  }
}
