package com.waldorf.application.service;

import com.waldorf.application.dto.aluno.AlunoResponseDTO;
import com.waldorf.application.dto.turma.TurmaRequestDTO;
import com.waldorf.application.dto.turma.TurmaResponseDTO;
import com.waldorf.domain.entity.Turma;
import com.waldorf.infrastructure.repository.AlunoRepository;
import com.waldorf.infrastructure.repository.ProfessorRepository;
import com.waldorf.infrastructure.repository.TurmaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TurmaService {

    private final TurmaRepository    turmaRepository;
    private final ProfessorRepository professorRepository;
    private final AlunoRepository    alunoRepository;

    public List<TurmaResponseDTO> listar(Integer anoLetivo) {
        List<Turma> turmas = anoLetivo != null
                ? turmaRepository.findByAnoLetivo(anoLetivo)
                : turmaRepository.findAll();
        return turmas.stream().map(this::toDTO).toList();
    }

    public TurmaResponseDTO buscarPorId(Long id) {
        return toDTO(findOrThrow(id));
    }

    @Transactional
    public TurmaResponseDTO criar(TurmaRequestDTO dto) {
        Turma t = new Turma();
        aplicarDTO(t, dto);
        return toDTO(turmaRepository.save(t));
    }

    @Transactional
    public TurmaResponseDTO atualizar(Long id, TurmaRequestDTO dto) {
        Turma t = findOrThrow(id);
        aplicarDTO(t, dto);
        return toDTO(turmaRepository.save(t));
    }

    public List<AlunoResponseDTO> listarAlunos(Long turmaId) {
        return alunoRepository.findByTurmaIdAndAtivoTrue(turmaId).stream()
                .map(a -> new AlunoResponseDTO(
                        a.getId(), a.getMatricula(), a.getNome(), a.getDataNascimento(),
                        a.getGenero(), a.getEmail(), a.getTelefone(),
                        turmaId, null, a.getAnoIngresso(), a.getTemperamento(),
                        a.isAtivo(), List.of(), a.getCreatedAt(), a.getUpdatedAt()))
                .toList();
    }

    private Turma findOrThrow(Long id) {
        return turmaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Turma não encontrada: " + id));
    }

    private void aplicarDTO(Turma t, TurmaRequestDTO dto) {
        t.setNome(dto.nome());
        t.setAnoLetivo(dto.anoLetivo());
        t.setProfessorRegente(professorRepository.findById(dto.professorRegenteId())
                .orElseThrow(() -> new EntityNotFoundException("Professor não encontrado")));
    }

    private TurmaResponseDTO toDTO(Turma t) {
        int total = alunoRepository.countByTurmaIdAndAtivoTrue(t.getId());
        return new TurmaResponseDTO(
                t.getId(), t.getNome(), t.getAnoLetivo(),
                t.getProfessorRegente() != null ? t.getProfessorRegente().getId() : null,
                t.getProfessorRegente() != null ? t.getProfessorRegente().getNome() : null,
                total, t.isAtiva(), t.getCreatedAt()
        );
    }
}
