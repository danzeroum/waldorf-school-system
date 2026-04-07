package com.waldorf.application.service;

import com.waldorf.application.dto.financeiro.ContratoRequestDTO;
import com.waldorf.application.dto.financeiro.ContratoResponseDTO;
import com.waldorf.domain.entity.Contrato;
import com.waldorf.domain.enums.SituacaoContrato;
import com.waldorf.infrastructure.repository.AlunoRepository;
import com.waldorf.infrastructure.repository.ContratoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
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
                .orElseThrow(() -> new EntityNotFoundException("Aluno nao encontrado"));
        Contrato c = Contrato.builder()
                .aluno(aluno)
                .anoLetivo(dto.anoLetivo())
                .valorMensalidade(dto.valorMensalidade())
                .desconto(dto.desconto())
                .valorMatricula(dto.valorMatricula())
                .totalParcelas(dto.totalParcelas())
                .diaVencimento(dto.diaVencimento() != null ? dto.diaVencimento() : 10)
                .dataInicio(dto.dataInicio())
                .dataFim(dto.dataFim())
                .situacao(SituacaoContrato.ATIVO)
                .build();
        c = contratoRepository.save(c);
        mensalidadeService.gerarMensalidades(c);
        log.info("Contrato {} criado com {} parcelas para aluno {}", c.getId(), c.getTotalParcelas(), aluno.getNome());
        return toDTO(c);
    }

    private Contrato findOrThrow(Long id) {
        return contratoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contrato nao encontrado: " + id));
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
