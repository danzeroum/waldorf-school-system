import { Component, OnInit, signal } from '@angular/core';
import { LgpdService, Consentimento } from '../../services/lgpd.service';

@Component({
  selector: 'wld-consentimento-list',
  templateUrl: './consentimento-list.component.html',
  standalone: false,
})
export class ConsentimentoListComponent implements OnInit {
  consentimentos = signal<Consentimento[]>([]);
  carregando     = signal(true);
  filtroStatus   = signal('');

  readonly statusOpcoes = ['', 'ACEITO', 'PENDENTE', 'RECUSADO', 'REVOGADO'];

  constructor(private lgpdService: LgpdService) {}

  ngOnInit(): void { this.carregar(); }

  carregar(): void {
    this.carregando.set(true);
    this.lgpdService.listarConsentimentos(this.filtroStatus() || undefined).subscribe({
      next: (c) => { this.consentimentos.set(c); this.carregando.set(false); },
      error: ()  => { this.consentimentos.set(this.mock()); this.carregando.set(false); },
    });
  }

  filtrar(status: string): void {
    this.filtroStatus.set(status);
    this.carregar();
  }

  private mock(): Consentimento[] {
    return [
      { id: 1, alunoId: 1, alunoNome: 'Ana Clara Souza',  responsavelNome: 'Carlos Souza',  responsavelEmail: 'carlos@email.com', tipo: 'TERMOS_USO',       status: 'ACEITO',   dataAceite: '2026-02-01', versaoTermos: '1.0' },
      { id: 2, alunoId: 2, alunoNome: 'Pedro Santos',     responsavelNome: 'Maria Santos',  responsavelEmail: 'maria@email.com',  tipo: 'FOTOS_EVENTOS',    status: 'ACEITO',   dataAceite: '2026-02-01', versaoTermos: '1.0' },
      { id: 3, alunoId: 3, alunoNome: 'Julia Ferreira',   responsavelNome: 'João Ferreira',  responsavelEmail: 'joao@email.com',   tipo: 'COMPARTILHAMENTO', status: 'PENDENTE', versaoTermos: '1.0' },
      { id: 4, alunoId: 4, alunoNome: 'Lucas Oliveira',   responsavelNome: 'Ana Oliveira',  responsavelEmail: 'ana@email.com',    tipo: 'TERMOS_USO',       status: 'REVOGADO', dataAceite: '2026-01-15', dataRevogacao: '2026-03-01', versaoTermos: '1.0' },
    ];
  }
}
