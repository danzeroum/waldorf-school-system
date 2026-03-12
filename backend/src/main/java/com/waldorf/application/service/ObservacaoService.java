package com.waldorf.application.service;

import com.waldorf.application.dto.observacao.ObservacaoRequestDTO;
import com.waldorf.application.dto.observacao.ObservacaoResponseDTO;
import com.waldorf.domain.entity.ObservacaoDesenvolvimento;
import com.waldorf.infrastructure.repository.AlunoRepository;
import com.waldorf.infrastructure.repository.ObservacaoRepository;
import com.waldorf.infrastructure.repository.ProfessorRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ObservacaoService {

    private final ObservacaoRepository  observacaoRepository;
    private final AlunoRepository       alunoRepository;
    private final ProfessorRepository   professorRepository;

    public List<ObservacaoResponseDTO> listarPorAluno(Long alunoId, String aspecto) {
        List<ObservacaoDesenvolvimento> obs = aspecto != null
                ? observacaoRepository.findByAlunoIdAndAspecto(alunoId, aspecto)
                : observacaoRepository.findByAlunoIdOrderByDataDesc(alunoId);
        return obs.stream().map(this::toDTO).toList();
    }

    public ObservacaoResponseDTO buscarPorId(Long id) {
        return toDTO(findOrThrow(id));
    }

    @Transactional
    public ObservacaoResponseDTO criar(ObservacaoRequestDTO dto) {
        ObservacaoDesenvolvimento obs = new ObservacaoDesenvolvimento();
        aplicarDTO(obs, dto);
        return toDTO(observacaoRepository.save(obs));
    }

    @Transactional
    public ObservacaoResponseDTO atualizar(Long id, ObservacaoRequestDTO dto) {
        ObservacaoDesenvolvimento obs = findOrThrow(id);
        aplicarDTO(obs, dto);
        return toDTO(observacaoRepository.save(obs));
    }

    @Transactional
    public void excluir(Long id) {
        observacaoRepository.delete(findOrThrow(id));
    }

    private ObservacaoDesenvolvimento findOrThrow(Long id) {
        return observacaoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Observação não encontrada: " + id));
    }

    private void aplicarDTO(ObservacaoDesenvolvimento o, ObservacaoRequestDTO dto) {
        o.setAluno(alunoRepository.findById(dto.alunoId())
                .orElseThrow(() -> new EntityNotFoundException("Aluno não encontrado")));
        o.setProfessor(professorRepository.findById(dto.professorId())
                .orElseThrow(() -> new EntityNotFoundException("Professor não encontrado")));
        o.setAspecto(dto.aspecto());
        o.setConteudo(dto.conteudo());
        o.setPrivada(dto.privada());
        o.setData(dto.data());
    }

    private ObservacaoResponseDTO toDTO(ObservacaoDesenvolvimento o) {
        return new ObservacaoResponseDTO(
                o.getId(),
                o.getAluno().getId(), o.getAluno().getNome(),
                o.getProfessor().getId(), o.getProfessor().getNome(),
                o.getAspecto(), o.getConteudo(), o.isPrivada(),
                o.getData(), o.getCreatedAt()
        );
    }
}
