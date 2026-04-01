package com.waldorf.application.service;

import com.waldorf.application.dto.observacao.ObservacaoRequestDTO;
import com.waldorf.application.dto.observacao.ObservacaoResponseDTO;
import com.waldorf.domain.entity.ObservacaoDesenvolvimento;
import com.waldorf.domain.entity.Professor;
import com.waldorf.domain.repository.AlunoRepository;
import com.waldorf.domain.repository.ObservacaoRepository;
import com.waldorf.domain.repository.ProfessorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ObservacaoService {

    private final ObservacaoRepository observacaoRepository;
    private final AlunoRepository alunoRepository;
    private final ProfessorRepository professorRepository;

    public ObservacaoService(ObservacaoRepository observacaoRepository,
                             AlunoRepository alunoRepository,
                             ProfessorRepository professorRepository) {
        this.observacaoRepository = observacaoRepository;
        this.alunoRepository = alunoRepository;
        this.professorRepository = professorRepository;
    }

    /**
     * Despacha para filtro por aspecto ou lista completa por aluno.
     * Chamado pelos controllers de ambos os pacotes.
     */
    @Transactional(readOnly = true)
    public List<ObservacaoResponseDTO> listar(Long alunoId, String aspecto) {
        if (alunoId != null && aspecto != null && !aspecto.isBlank()) {
            return observacaoRepository
                    .findByAlunoIdAndAspecto(alunoId, aspecto)
                    .stream().map(this::toDTO).toList();
        }
        if (alunoId != null) {
            return listarPorAluno(alunoId);
        }
        // sem filtro: retorna todas (admin/diretor)
        return observacaoRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<ObservacaoResponseDTO> listarPorAluno(Long alunoId) {
        return observacaoRepository.findByAlunoIdOrderByDataDesc(alunoId)
                .stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public Page<ObservacaoResponseDTO> listarPorAlunoPaginado(Long alunoId, Pageable pageable) {
        return observacaoRepository.findByAlunoId(alunoId, pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public ObservacaoResponseDTO buscarPorId(Long id) {
        return toDTO(buscarEntidade(id));
    }

    public ObservacaoResponseDTO criar(ObservacaoRequestDTO dto) {
        ObservacaoDesenvolvimento o = new ObservacaoDesenvolvimento();
        aplicarDTO(o, dto);
        return toDTO(observacaoRepository.save(o));
    }

    public ObservacaoResponseDTO atualizar(Long id, ObservacaoRequestDTO dto) {
        ObservacaoDesenvolvimento o = buscarEntidade(id);
        aplicarDTO(o, dto);
        return toDTO(observacaoRepository.save(o));
    }

    public void excluir(Long id) {
        observacaoRepository.delete(buscarEntidade(id));
    }

    // --- helpers ---

    private ObservacaoDesenvolvimento buscarEntidade(Long id) {
        return observacaoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Observação não encontrada: " + id));
    }

    private void aplicarDTO(ObservacaoDesenvolvimento o, ObservacaoRequestDTO dto) {
        o.setAluno(alunoRepository.findById(dto.alunoId())
                .orElseThrow(() -> new EntityNotFoundException("Aluno não encontrado: " + dto.alunoId())));

        if (dto.professorId() != null) {
            o.setProfessor(professorRepository.findById(dto.professorId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Professor não encontrado: " + dto.professorId())));
        } else {
            Professor fallback = professorRepository.findAll().stream()
                    .filter(Professor::isAtivo)
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException(
                            "Nenhum professor ativo encontrado para associar à observação"));
            o.setProfessor(fallback);
        }

        o.setAspecto(dto.aspecto());
        o.setConteudo(dto.conteudo());
        o.setPrivada(dto.privada());
        o.setData(dto.data());
    }

    private ObservacaoResponseDTO toDTO(ObservacaoDesenvolvimento o) {
        return new ObservacaoResponseDTO(
                o.getId(),
                o.getAluno().getId(),
                o.getAluno().getNome(),
                o.getProfessor() != null ? o.getProfessor().getId() : null,
                o.getProfessor() != null ? o.getProfessor().getNome() : null,
                o.getAspecto(),
                o.getConteudo(),
                o.isPrivada(),
                o.getData(),
                o.getCreatedAt()
        );
    }
}
