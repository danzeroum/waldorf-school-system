import { Component, OnInit, computed, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';

interface MetricasSecretaria {
  totalAlunosAtivos: number;
  alunosNovosMes: number;
  matriculasPendentes: number;
  contratosVencendo: number;
  mensalidadesAtrasadas: number;
  lgpdPendentes: number;
  valorAReceber: number;
  valorRecebido: number;
  valorTotal: number;
}

interface AtividadeRecente {
  tipo: string;
  descricao: string;
  hora: string;
  icone: string;
  origem: 'FINANCEIRO' | 'PEDAGOGIA' | 'MATRICULA' | 'CONTRATO';
}

interface RecebimentoMensal {
  mes: string;
  valor: number;
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

  // TODO: integrar endpoint GET /api/v1/financeiro/recebimento-mensal
  recebimentoMensal = signal<RecebimentoMensal[]>([
    { mes: 'Jan', valor: 38200 },
    { mes: 'Fev', valor: 41500 },
    { mes: 'Mar', valor: 39800 },
    { mes: 'Abr', valor: 43200 },
    { mes: 'Mai', valor: 40100 },
    { mes: 'Jun', valor: 32800 },
  ]);

  temPendencias = computed(() => {
    const m = this.metricas();
    if (!m) return false;
    return (m.mensalidadesAtrasadas + m.contratosVencendo + m.lgpdPendentes) > 0;
  });

  percentualRecebido = computed(() => {
    const m = this.metricas();
    if (!m || m.valorTotal === 0) return 0;
    return Math.round((m.valorRecebido / m.valorTotal) * 100);
  });

  maxRecebimento = computed(() => {
    const dados = this.recebimentoMensal();
    return Math.max(...dados.map(d => d.valor), 1);
  });

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.carregarDados();
  }

  private carregarDados(): void {
    this.carregando.set(true);
    // TODO: conectar aos endpoints reais
    // GET /api/v1/dashboard/secretary (adicionar: alunosNovosMes, valorRecebido, valorTotal)
    setTimeout(() => {
      this.metricas.set({
        totalAlunosAtivos: 342,
        alunosNovosMes: 4,
        matriculasPendentes: 12,
        contratosVencendo: 5,
        mensalidadesAtrasadas: 8,
        lgpdPendentes: 3,
        valorAReceber: 8500,
        valorRecebido: 32800,
        valorTotal: 41300,
      });
      this.atividades.set([
        { tipo: 'matricula',  descricao: 'Matrícula #2024-089 criada (Classe 3)',         hora: '14:30', icone: 'assignment',  origem: 'MATRICULA'  },
        { tipo: 'contrato',  descricao: 'Contrato #C-456 assinado (Família Silva)',        hora: '13:15', icone: 'description', origem: 'CONTRATO'   },
        { tipo: 'pagamento', descricao: 'Boleto pago: R$ 850,00 (Família Santos)',         hora: '11:00', icone: 'payments',    origem: 'FINANCEIRO' },
        { tipo: 'aluno',     descricao: 'Novo aluno cadastrado: Pedro Oliveira',           hora: '09:45', icone: 'school',      origem: 'MATRICULA'  },
        { tipo: 'observacao',descricao: 'Observação registrada: Ana Clara — Classe 5',    hora: '09:10', icone: 'edit_note',   origem: 'PEDAGOGIA'  },
      ]);
      this.carregando.set(false);
    }, 500);
  }

  formatarMoeda(valor: number): string {
    return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(valor);
  }

  corOrigem(origem: string): string {
    const mapa: Record<string, string> = {
      FINANCEIRO: 'bg-waldorf-amber-400',
      PEDAGOGIA:  'bg-waldorf-green-500',
      MATRICULA:  'bg-waldorf-terra-400',
      CONTRATO:   'bg-blue-400',
    };
    return mapa[origem] ?? 'bg-waldorf-gray-400';
  }
}
