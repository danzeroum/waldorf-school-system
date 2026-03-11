import { Component, OnInit, signal } from '@angular/core';
import { AvisoService, Aviso } from '../services/aviso.service';
import { AuthService } from '@core/auth/auth.service';

@Component({
  selector: 'wld-portal-pais',
  templateUrl: './portal-pais.component.html',
  standalone: false,
})
export class PortalPaisComponent implements OnInit {
  avisos     = signal<Aviso[]>([]);
  carregando = signal(true);

  constructor(
    private avisoService: AvisoService,
    public authService: AuthService,
  ) {}

  ngOnInit(): void {
    this.avisoService.listar().subscribe({
      next: (a) => { this.avisos.set(a); this.carregando.set(false); },
      error: ()  => { this.avisos.set([]); this.carregando.set(false); },
    });
  }
}
