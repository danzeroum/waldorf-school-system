import { Pipe, PipeTransform } from '@angular/core';

const PARENTESCOS: Record<string, string> = {
  PAI:               'Pai',
  MAE:               'Mãe',
  RESPONSAVEL_LEGAL: 'Responsável Legal',
  AVO_PATERNO:       'Avô Paterno',
  AVO_PATERNA:       'Avó Paterna',
  AVO_MATERNO:       'Avô Materno',
  AVO_MATERNA:       'Avó Materna',
  TIO:               'Tio',
  TIA:               'Tia',
  OUTRO:             'Outro',
};

@Pipe({ name: 'parentesco', standalone: false })
export class ParentescoPipe implements PipeTransform {
  transform(value: string): string {
    return PARENTESCOS[value] ?? value;
  }
}
