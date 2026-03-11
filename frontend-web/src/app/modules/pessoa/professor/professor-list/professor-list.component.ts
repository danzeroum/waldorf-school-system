import { Component, OnInit, signal } from '@angular/core';

@Component({
  selector: 'wld-professor-list',
  templateUrl: './professor-list.component.html',
  standalone: false,
})
export class ProfessorListComponent implements OnInit {
  carregando = signal(false);

  ngOnInit(): void {
    // TODO: GET /api/v1/teachers
  }
}
