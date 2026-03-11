package com.waldorf.application.service;

import com.waldorf.application.dto.aluno.AlunoRequestDTO;
import com.waldorf.application.dto.aluno.AlunoResponseDTO;
import com.waldorf.domain.entity.Aluno;
import com.waldorf.infrastructure.repository.AlunoRepository;
import com.waldorf.infrastructure.repository.ResponsavelRepository;
import com.waldorf.infrastructure.repository.TurmaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;

@Service
@RequiredArgsConstructor
public class AlunoService {

    private final AlunoRepository       alunoRepository;
    private final TurmaRepository       turmaRepository;
    private final ResponsavelRepository responsavelRepository;

    public Page<AlunoResponseDTO> listar(String nome, Long turmaId, Boolean ativo, Pageable pageable) {
        return alunoRepository.findWithFilters(nome, turmaId, ativo, pageable)
                .map(this::toDTO);
    }

    public AlunoResponseDTO buscarPorId(Long id) {
        return toDTO(findOrThrow(id));
    }

    @Transactional
    public AlunoResponseDTO criar(AlunoRequestDTO dto) {
        var aluno = new Aluno();
        aluno.setNome(dto.nome());
        aluno.setDataNascimento(dto.dataNascimento());
        aluno.setGenero(dto.genero());
        aluno.setEmail(dto.email());
        aluno.setAnoIngresso(dto.anoIngresso());
        aluno.setAtivo(true);
        if (dto.turmaId() != null) {
            aluno.setTurma(turmaRepository.findById(dto.turmaId()).orElse(null));
        }
        aluno = alunoRepository.save(aluno);
        aluno.setMatricula(gerarMatricula(aluno.getId()));
        return toDTO(alunoRepository.save(aluno));
    }

    @Transactional
    public void inativar(Long id) {
        Aluno aluno = findOrThrow(id);
        aluno.setAtivo(false);
        alunoRepository.save(aluno);
    }

    private Aluno findOrThrow(Long id) {
        return alunoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Aluno não encontrado: " + id));
    }

    private String gerarMatricula(Long id) {
        return Year.now().getValue() + String.format("%05d", id);
    }

    private AlunoResponseDTO toDTO(Aluno a) {
        return new AlunoResponseDTO(
                a.getId(), a.getMatricula(), a.getNome(),
                a.getDataNascimento(), a.getGenero(), a.getEmail(),
                a.getAnoIngresso(),
                a.getTurma() != null ? a.getTurma().getNome() : null,
                a.getTemperamento(), a.isAtivo(),
                a.getCreatedAt(), a.getUpdatedAt()
        );
    }
}
