import { Component, OnInit, signal } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { LgpdService, SolicitacaoTitular } from '../../services/lgpd.service';

@Component({
  selector: 'wld-solicitacao-list',
  templateUrl: './solicitacao-list.component.html',
  standalone: false,
})
export class SolicitacaoListComponent implements OnInit {
  solicitacoes    = signal<SolicitacaoTitular[]>([]);
  carregando      = signal(true);
  modalAberto     = signal<SolicitacaoTitular | null>(null);
  salvando        = signal(false);
  formResposta!: FormGroup;

  readonly tiposLabel: Record<string, string> = {
    ACESSO: 'Acesso a Dados', CORRECAO: 'Correção', EXCLUSAO: 'Exclusão',
    PORTABILIDADE: 'Portabilidade', OPOSICAO: 'Oposição',
  };

  constructor(private lgpdService: LgpdService, private fb: FormBuilder) {}

  ngOnInit(): void {
    this.formResposta = this.fb.group({
      resposta: ['', [Validators.required, Validators.minLength(10)]],
      status:   ['CONCLUIDA', Validators.required],
    });
    this.carregar();
  }

  carregar(): void {
    this.carregando.set(true);
    this.lgpdService.listarSolicitacoes().subscribe({
      next: (s) => { this.solicitacoes.set(s); this.carregando.set(false); },
      error: ()  => { this.solicitacoes.set([]); this.carregando.set(false); },
    });
  }

  abrirModal(s: SolicitacaoTitular): void {
    this.formResposta.reset({ status: 'CONCLUIDA' });
    this.modalAberto.set(s);
  }

  responder(): void {
    const s = this.modalAberto();
    if (!s || this.formResposta.invalid) { this.formResposta.markAllAsTouched(); return; }
    this.salvando.set(true);
    this.lgpdService.responderSolicitacao(s.id, this.formResposta.value.resposta, this.formResposta.value.status).subscribe({
      next: (atualizada) => {
        this.solicitacoes.update(list => list.map(x => x.id === atualizada.id ? atualizada : x));
        this.salvando.set(false);
        this.modalAberto.set(null);
      },
      error: () => { this.salvando.set(false); },
    });
  }
}
