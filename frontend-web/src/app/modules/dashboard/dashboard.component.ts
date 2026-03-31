import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { AuthService } from '../../core/auth/auth.service';
import { catchError, of } from 'rxjs';

interface DashboardSecretaria {
  totalAlunosAtivos:     number;
  matriculasAtivas:      number;
  contratosPendentes:    number;
  mensalidadesAtrasadas: number;
  lgpdPendentes:         number;
}

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
})
export class DashboardComponent implements OnInit {

  usuario      = this.authService.getUsuario();
  loading      = true;
  erro         = false;

  stats: DashboardSecretaria = {
    totalAlunosAtivos:     0,
    matriculasAtivas:      0,
    contratosPendentes:    0,
    mensalidadesAtrasadas: 0,
    lgpdPendentes:         0,
  };

  cards = [
    { label: 'Alunos Ativos',          icon: 'people',       color: 'text-blue-600',   key: 'totalAlunosAtivos',     rota: '/pessoas/alunos' },
    { label: 'Matrículas Ativas',       icon: 'school',       color: 'text-green-600',  key: 'matriculasAtivas',      rota: '/escolar/turmas' },
    { label: 'Contratos Pendentes',     icon: 'description',  color: 'text-orange-500', key: 'contratosPendentes',    rota: '/financeiro/contratos' },
    { label: 'Mensalidades em Atraso',  icon: 'payments',     color: 'text-red-600',    key: 'mensalidadesAtrasadas', rota: '/financeiro/mensalidades' },
    { label: 'Consentimentos LGPD',     icon: 'policy',       color: 'text-purple-600', key: 'lgpdPendentes',         rota: '/lgpd' },
  ];

  constructor(
    private http:        HttpClient,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.http
      .get<DashboardSecretaria>(`${environment.apiUrl}/analytics/dashboard/secretaria`)
      .pipe(
        catchError(() => {
          this.erro = true;
          // Mock data para desenvolvimento sem backend
          return of<DashboardSecretaria>({
            totalAlunosAtivos:     42,
            matriculasAtivas:      38,
            contratosPendentes:     5,
            mensalidadesAtrasadas:  3,
            lgpdPendentes:          2,
          });
        })
      )
      .subscribe(data => {
        this.stats   = data;
        this.loading = false;
      });
  }

  getStatValue(key: string): number {
    return (this.stats as any)[key] ?? 0;
  }
}
