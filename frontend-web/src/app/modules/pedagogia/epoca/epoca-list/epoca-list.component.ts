import { Component, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';
import { EpocaService, Epoca } from '../../services/epoca.service';

@Component({
  selector: 'wld-epoca-list',
  templateUrl: './epoca-list.component.html',
  standalone: false,
})
export class EpocaListComponent implements OnInit {
  epocas       = signal<Epoca[]>([]);
  carregando   = signal(true);
  filtroStatus = signal<string>('');
  anoLetivo    = signal(new Date().getFullYear());

  constructor(
    private epocaService: EpocaService,
    private router: Router,
  ) {}

  ngOnInit(): void { this.carregar(); }

  carregar(): void {
    this.carregando.set(true);
    this.epocaService.listar(undefined, this.anoLetivo()).subscribe({
      next: (e) => { this.epocas.set(e); this.carregando.set(false); },
      error: () => { this.epocas.set([]); this.carregando.set(false); },
    });
  }

  novaEpoca(): void { this.router.navigate(['/pedagogia/epocas/nova']); }

  get epocasFiltradas(): Epoca[] {
    const f = this.filtroStatus();
    return f ? this.epocas().filter(e => e.status === f) : this.epocas(); // era: e.situacao
  }
}
