import { Component, OnInit, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '@environments/environment';

interface MetricasSecretaria {
  totalAlunosAtivos: number;
  matriculasPendentes: number;
  contratosVencendo: number;
  mensalidadesAtrasadas: number;
  lgpdPendentes: number;
  valorAReceber: number;
}

interface AtividadeRecente {
  tipo: string;
  descricao: string;
  hora: string;
  icone: string;
}

@Component({
  selector: 'wld-secretaria-dashboard',
  templateUrl: './secretaria-dashboard.component.html',
  standalone: false,
})
export class SecretariaDashboardComponent implements OnInit {
  metricas = signal<MetricasSecretaria | null>(null);
  atividades = signal<AtividadeRecente[]>([]);
  carregando = signal(true);
  dataHoje = new Date();

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.carregarDados();
  }

  private carregarDados(): void {
    this.carregando.set(true);
    // TODO: conectar aos endpoints reais
    // GET /api/v1/dashboard/secretary
    setTimeout(() => {
      this.metricas.set({
        totalAlunosAtivos: 342,
        matriculasPendentes: 12,
        contratosVencendo: 5,
        mensalidadesAtrasadas: 8,
        lgpdPendentes: 3,
        valorAReceber: 8500,
      });
      this.atividades.set([
        { tipo: 'matricula', descricao: 'Matrícula #2024-089 criada (Classe 3)', hora: '14:30', icone: 'assignment' },
        { tipo: 'contrato', descricao: 'Contrato #C-456 assinado (Família Silva)', hora: '13:15', icone: 'description' },
        { tipo: 'pagamento', descricao: 'Boleto pago: R$ 850,00 (Família Santos)', hora: '11:00', icone: 'payments' },
        { tipo: 'aluno', descricao: 'Novo aluno cadastrado: Pedro Oliveira', hora: '09:45', icone: 'school' },
      ]);
      this.carregando.set(false);
    }, 500);
  }

  formatarMoeda(valor: number): string {
    return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(valor);
  }
}
