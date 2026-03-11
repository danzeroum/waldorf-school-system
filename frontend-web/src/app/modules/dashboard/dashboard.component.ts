import { Component } from '@angular/core';

@Component({
  selector: 'app-dashboard',
  template: `
    <h1 class="text-2xl font-bold mb-6">Dashboard</h1>
    <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
      <mat-card><mat-card-content><mat-icon>people</mat-icon> Alunos</mat-card-content></mat-card>
      <mat-card><mat-card-content><mat-icon>school</mat-icon> Turmas</mat-card-content></mat-card>
      <mat-card><mat-card-content><mat-icon>payments</mat-icon> Financeiro</mat-card-content></mat-card>
    </div>
  `,
})
export class DashboardComponent {}
