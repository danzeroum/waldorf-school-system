package com.waldorf.application.service;

import com.waldorf.application.dto.financeiro.ContratoRequestDTO;
import com.waldorf.application.dto.financeiro.ContratoResponseDTO;
import com.waldorf.domain.entity.Contrato;
import com.waldorf.infrastructure.repository.AlunoRepository;
import com.waldorf.infrastructure.repository.ContratoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContratoService {

    private final ContratoRepository contratoRepository;
    private final AlunoRepository    alunoRepository;

    @Transactional
    public ContratoResponseDTO criar(ContratoRequestDTO dto) {
        var aluno = alunoRepository.findById(dto.alunoId())
                .orElseThrow(() -> new EntityNotFoundException("Aluno não encontrado"));

        var contrato = new Contrato();
        contrato.setAluno(aluno);
        contrato.setAnoLetivo(dto.anoLetivo());
        contrato.setValorMensalidade(dto.valorMensalidade());
        contrato.setValorMatricula(dto.valorMatricula());
        contrato.setTotalParcelas(dto.totalParcelas());
        contrato.setDiaVencimento(dto.diaVencimento());
        contrato.setDataInicio(dto.dataInicio());

        return toDTO(contratoRepository.save(contrato));
    }

    public ContratoResponseDTO buscarPorId(Long id) {
        return toDTO(contratoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contrato não encontrado: " + id)));
    }

    private ContratoResponseDTO toDTO(Contrato c) {
        return new ContratoResponseDTO(
                c.getId(),
                c.getAluno().getId(),
                c.getAluno().getNome(),
                c.getAnoLetivo(),
                c.getValorMensalidade(),
                c.getValorMatricula(),
                c.getTotalParcelas(),
                c.getDiaVencimento(),
                c.getDataInicio(),
                c.getSituacao(),
                c.getCreatedAt()
        );
    }
}
