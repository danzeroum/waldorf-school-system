import { Component, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';
import { ComunicadoService, Comunicado } from '../../services/comunicado.service';

@Component({
  selector: 'wld-comunicado-list',
  templateUrl: './comunicado-list.component.html',
  standalone: false,
})
export class ComunicadoListComponent implements OnInit {
  comunicados = signal<Comunicado[]>([]);
  carregando  = signal(true);

  constructor(private comunicadoService: ComunicadoService, private router: Router) {}

  ngOnInit(): void {
    this.comunicadoService.listar().subscribe({
      next: (c) => { this.comunicados.set(c); this.carregando.set(false); },
      error: ()  => { this.comunicados.set([]); this.carregando.set(false); },
    });
  }

  novoComunicado(): void { this.router.navigate(['/comunidade/comunicados/novo']); }

  taxaLeitura(c: Comunicado): number {
    if (!c.totalDestinatarios || c.totalDestinatarios === 0) return 0;
    return Math.round(((c.totalLidos ?? 0) / c.totalDestinatarios) * 100);
  }
}
