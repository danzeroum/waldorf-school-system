import { Component, OnInit, signal } from '@angular/core';

@Component({
  selector: 'wld-professor-dashboard',
  templateUrl: './professor-dashboard.component.html',
  standalone: false,
})
export class ProfessorDashboardComponent implements OnInit {
  turmaAtual = signal({ nome: 'Classe 3', totalAlunos: 25, pendentesObservacao: 3 });
  epocaAtual = signal({ titulo: 'Mitologia Nórdica', inicio: '03/02', fim: '28/02' });
  carregando = signal(false);

  ngOnInit(): void {
    // TODO: GET /api/v1/dashboard/teacher
  }
}
