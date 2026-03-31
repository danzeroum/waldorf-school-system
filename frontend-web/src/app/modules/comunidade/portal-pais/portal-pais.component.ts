import { Component, OnInit, signal } from '@angular/core';
import { AvisoService, Aviso } from '../services/aviso.service';
import { AuthService } from '../../../core/auth/auth.service';

@Component({
  selector: 'wld-portal-pais',
  templateUrl: './portal-pais.component.html',
  standalone: false,
})
export class PortalPaisComponent implements OnInit {
  avisos     = signal<Aviso[]>([]);
  carregando = signal(true);
  usuario    = this.auth.getUsuario();

  constructor(private avisoService: AvisoService, private auth: AuthService) {}

  ngOnInit(): void {
    this.avisoService.listar().subscribe({
      next: (a) => { this.avisos.set(a); this.carregando.set(false); },
      error: ()  => {
        this.avisos.set([
          { id: 1, titulo: 'Reunião de pais — Classe 3', conteudo: 'Sexta-feira, 19h. Sala dos professores.', tipo: 'EVENTO',   autorNome: 'Secretaria', fixado: false, dataPublicacao: '2026-03-30' },
          { id: 2, titulo: 'Festival de Artes — Abril',   conteudo: 'O Festival Anual de Artes acontece nos dias 18 e 19 de abril.', tipo: 'FESTIVAL', autorNome: 'Direção', fixado: true, dataPublicacao: '2026-03-28' },
          { id: 3, titulo: 'Mutirc3a3o de limpeza',         conteudo: 'Sábado, 8h às 12h. Traga luvas e entusiasmo!', tipo: 'GERAL', autorNome: 'Secretaria', fixado: false, dataPublicacao: '2026-03-25' },
        ]);
        this.carregando.set(false);
      },
    });
  }
}
