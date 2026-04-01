package com.waldorf.application.service;

import com.waldorf.application.dto.financeiro.ContratoRequestDTO;
import com.waldorf.application.dto.financeiro.ContratoResponseDTO;
import com.waldorf.domain.entity.Contrato;
import com.waldorf.domain.enums.SituacaoContrato;
import com.waldorf.infrastructure.repository.AlunoRepository;
import com.waldorf.infrastructure.repository.ContratoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContratoService {

    private final ContratoRepository contratoRepository;
    private final AlunoRepository    alunoRepository;
    private final MensalidadeService mensalidadeService;

    public List<ContratoResponseDTO> listar(Long alunoId) {
        if (alunoId != null) {
            return contratoRepository.findByAlunoId(alunoId).stream().map(this::toDTO).toList();
        }
        return contratoRepository.findAll().stream().map(this::toDTO).toList();
    }

    public ContratoResponseDTO buscarPorId(Long id) {
        return toDTO(findOrThrow(id));
    }

    @Transactional
    public ContratoResponseDTO criar(ContratoRequestDTO dto) {
        var aluno = alunoRepository.findById(dto.alunoId())
                .orElseThrow(() -> new EntityNotFoundException("Aluno não encontrado"));
        Contrato c = new Contrato();
        c.setAluno(aluno);
        c.setAnoLetivo(dto.anoLetivo());
        c.setValorMensalidade(dto.valorMensalidade());
        c.setDesconto(dto.desconto());
        c.setTotalParcelas(dto.totalParcelas());
        c.setDiaVencimento(dto.diaVencimento() != null ? dto.diaVencimento() : 10);
        c.setDataInicio(dto.dataInicio());
        c.setDataFim(dto.dataFim());
        c.setSituacao(SituacaoContrato.ATIVO);
        return toDTO(contratoRepository.save(c));
    }

    /**
     * Ativa um contrato e gera automaticamente as mensalidades.
     * Idempotente: se já existem mensalidades para o contrato, não duplica.
     */
    @Transactional
    public ContratoResponseDTO ativar(Long id) {
        Contrato c = findOrThrow(id);
        if (c.getSituacao() == SituacaoContrato.ENCERRADO) {
            throw new IllegalStateException("Contrato encerrado não pode ser reativado.");
        }
        c.setSituacao(SituacaoContrato.ATIVO);
        contratoRepository.save(c);
        mensalidadeService.gerarMensalidades(c);
        return toDTO(c);
    }

    /**
     * Encerra um contrato. Mensalidades PENDENTES permanecem — responsabilidade do financeiro.
     */
    @Transactional
    public ContratoResponseDTO encerrar(Long id, String motivo) {
        Contrato c = findOrThrow(id);
        c.setSituacao(SituacaoContrato.ENCERRADO);
        if (motivo != null && !motivo.isBlank()) {
            c.setObservacoes(motivo);
        }
        return toDTO(contratoRepository.save(c));
    }

    // --- helpers ---

    private Contrato findOrThrow(Long id) {
        return contratoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contrato não encontrado: " + id));
    }

    private ContratoResponseDTO toDTO(Contrato c) {
        return new ContratoResponseDTO(
                c.getId(),
                c.getAluno().getId(),
                c.getAluno().getNome(),
                c.getAnoLetivo(),
                c.getValorMensalidade(),
                c.getDesconto(),
                c.getTotalParcelas(),
                c.getDiaVencimento(),
                c.getDataInicio(),
                c.getDataFim(),
                c.getSituacao(),
                c.getCreatedAt()
        );
    }
}
