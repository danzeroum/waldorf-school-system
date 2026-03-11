import { Component, OnInit, signal } from '@angular/core';

@Component({
  selector: 'wld-pais-dashboard',
  templateUrl: './pais-dashboard.component.html',
  standalone: false,
})
export class PaisDashboardComponent implements OnInit {
  filhos = signal<{ nome: string; turma: string }[]>([]);
  carregando = signal(false);

  ngOnInit(): void {
    // TODO: GET /api/v1/dashboard/parent
    this.filhos.set([
      { nome: 'Lucas Ferreira', turma: 'Classe 3' },
      { nome: 'Ana Ferreira', turma: 'Jardim' },
    ]);
  }
}
