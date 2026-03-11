package com.waldorf.application.service;

import com.waldorf.application.dto.aluno.AlunoRequestDTO;
import com.waldorf.application.dto.aluno.AlunoResponseDTO;
import com.waldorf.domain.entity.Aluno;
import com.waldorf.domain.entity.Responsavel;
import com.waldorf.domain.entity.ResponsavelAluno;
import com.waldorf.infrastructure.repository.AlunoRepository;
import com.waldorf.infrastructure.repository.ResponsavelRepository;
import com.waldorf.infrastructure.repository.TurmaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AlunoService {

    private final AlunoRepository       alunoRepository;
    private final ResponsavelRepository responsavelRepository;
    private final TurmaRepository       turmaRepository;

    public Page<AlunoResponseDTO> listar(String nome, Long turmaId, Boolean ativo, Pageable pageable) {
        return alunoRepository.findWithFilters(nome, turmaId, ativo, pageable)
                .map(this::toDTO);
    }

    public AlunoResponseDTO buscarPorId(Long id) {
        return toDTO(findOrThrow(id));
    }

    @Transactional
    public AlunoResponseDTO criar(AlunoRequestDTO dto) {
        Aluno aluno = new Aluno();
        aplicarDTO(aluno, dto);
        aluno.setMatricula(gerarMatricula(dto.anoIngresso()));
        return toDTO(alunoRepository.save(aluno));
    }

    @Transactional
    public AlunoResponseDTO atualizar(Long id, AlunoRequestDTO dto) {
        Aluno aluno = findOrThrow(id);
        aplicarDTO(aluno, dto);
        return toDTO(alunoRepository.save(aluno));
    }

    @Transactional
    public void inativar(Long id) {
        Aluno aluno = findOrThrow(id);
        aluno.setAtivo(false);
        alunoRepository.save(aluno);
    }

    public List<AlunoResponseDTO.ResponsavelResumoDTO> listarResponsaveis(Long alunoId) {
        return alunoRepository.findResponsaveisByAlunoId(alunoId).stream()
                .map(r -> new AlunoResponseDTO.ResponsavelResumoDTO(
                        r.getId(), r.getNome(), null, r.getTelefone(), r.getEmail()))
                .toList();
    }

    @Transactional
    public void vincularResponsavel(Long alunoId, Long responsavelId, String parentesco) {
        Aluno aluno           = findOrThrow(alunoId);
        Responsavel responsavel = responsavelRepository.findById(responsavelId)
                .orElseThrow(() -> new EntityNotFoundException("Responsável não encontrado"));
        ResponsavelAluno vinculo = new ResponsavelAluno();
        vinculo.setAluno(aluno);
        vinculo.setResponsavel(responsavel);
        vinculo.setParentesco(parentesco);
        aluno.getResponsaveis().add(vinculo);
        alunoRepository.save(aluno);
    }

    // ---------- helpers ----------

    private Aluno findOrThrow(Long id) {
        return alunoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Aluno não encontrado: " + id));
    }

    private void aplicarDTO(Aluno a, AlunoRequestDTO dto) {
        a.setNome(dto.nome());
        a.setDataNascimento(dto.dataNascimento());
        a.setGenero(dto.genero());
        a.setEmail(dto.email());
        a.setTelefone(dto.telefone());
        a.setTemperamento(dto.temperamento());
        a.setAtivo(true);
        if (dto.turmaId() != null) {
            a.setTurma(turmaRepository.findById(dto.turmaId()).orElse(null));
        }
    }

    private String gerarMatricula(int ano) {
        return "%d%05d".formatted(ano, (int)(Math.random() * 99999));
    }

    private AlunoResponseDTO toDTO(Aluno a) {
        return new AlunoResponseDTO(
                a.getId(), a.getMatricula(), a.getNome(), a.getDataNascimento(),
                a.getGenero(), a.getEmail(), a.getTelefone(),
                a.getTurma() != null ? a.getTurma().getId() : null,
                a.getTurma() != null ? a.getTurma().getNome() : null,
                a.getAnoIngresso(), a.getTemperamento(), a.isAtivo(),
                List.of(),
                a.getCreatedAt(), a.getUpdatedAt()
        );
    }
}
