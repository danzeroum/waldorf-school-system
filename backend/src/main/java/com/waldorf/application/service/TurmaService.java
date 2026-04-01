package com.waldorf.application.service;

import com.waldorf.application.dto.aluno.AlunoResponseDTO;
import com.waldorf.application.dto.turma.TurmaRequestDTO;
import com.waldorf.application.dto.turma.TurmaResponseDTO;
import com.waldorf.domain.entity.Aluno;
import com.waldorf.domain.entity.Professor;
import com.waldorf.domain.entity.Turma;
import com.waldorf.infrastructure.repository.AlunoRepository;
import com.waldorf.infrastructure.repository.ProfessorRepository;
import com.waldorf.infrastructure.repository.TurmaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TurmaService {

    private final TurmaRepository turmaRepository;
    private final AlunoRepository alunoRepository;
    private final ProfessorRepository professorRepository;

    public TurmaService(TurmaRepository turmaRepository,
                        AlunoRepository alunoRepository,
                        ProfessorRepository professorRepository) {
        this.turmaRepository = turmaRepository;
        this.alunoRepository = alunoRepository;
        this.professorRepository = professorRepository;
    }

    @Transactional(readOnly = true)
    public List<TurmaResponseDTO> listar(Integer anoLetivo) {
        if (anoLetivo != null) {
            return turmaRepository.findByAnoLetivo(anoLetivo).stream().map(this::toDTO).toList();
        }
        return turmaRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public Page<TurmaResponseDTO> listar(Pageable pageable) {
        return turmaRepository.findAll(pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public TurmaResponseDTO buscarPorId(Long id) {
        return toDTO(buscarEntidade(id));
    }

    public TurmaResponseDTO criar(TurmaRequestDTO dto) {
        Turma t = new Turma();
        aplicarDTO(t, dto);
        return toDTO(turmaRepository.save(t));
    }

    public TurmaResponseDTO atualizar(Long id, TurmaRequestDTO dto) {
        Turma t = buscarEntidade(id);
        aplicarDTO(t, dto);
        return toDTO(turmaRepository.save(t));
    }

    public TurmaResponseDTO toggleAtiva(Long id) {
        Turma t = buscarEntidade(id);
        t.setAtiva(!t.isAtiva());
        return toDTO(turmaRepository.save(t));
    }

    public void excluir(Long id) {
        turmaRepository.delete(buscarEntidade(id));
    }

    @Transactional(readOnly = true)
    public List<AlunoResponseDTO> listarAlunos(Long turmaId) {
        buscarEntidade(turmaId);
        return alunoRepository.findByTurmaId(turmaId)
                .stream()
                .map(this::alunoToDTO)
                .toList();
    }

    // --- helpers ---

    private Turma buscarEntidade(Long id) {
        return turmaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Turma não encontrada: " + id));
    }

    /**
     * TurmaResponseDTO — 11 campos:
     * id, nome, anoLetivo, anoEscolar, capacidadeMaxima,
     * professorRegenteId, professorRegenteNome, totalAlunos,
     * ativa, createdAt, updatedAt
     */
    private TurmaResponseDTO toDTO(Turma t) {
        int total = alunoRepository.countByTurmaIdAndAtivoTrue(t.getId());
        return new TurmaResponseDTO(
                t.getId(),
                t.getNome(),
                t.getAnoLetivo(),
                t.getAnoEscolar(),
                t.getCapacidadeMaxima(),
                t.getProfessorRegente() != null ? t.getProfessorRegente().getId()   : null,
                t.getProfessorRegente() != null ? t.getProfessorRegente().getNome() : null,
                total,
                t.isAtiva(),
                t.getCreatedAt(),
                t.getUpdatedAt()
        );
    }

    /**
     * AlunoResponseDTO — 12 campos:
     * id, matricula, nome, dataNascimento, genero, email,
     * anoIngresso, turmaNome, temperamento, ativo, createdAt, updatedAt
     */
    private AlunoResponseDTO alunoToDTO(Aluno a) {
        return new AlunoResponseDTO(
                a.getId(),
                a.getMatricula(),
                a.getNome(),
                a.getDataNascimento(),
                a.getGenero(),
                a.getEmail(),
                a.getAnoIngresso(),
                a.getTurma() != null ? a.getTurma().getNome() : null,
                a.getTemperamento(),
                a.isAtivo(),
                a.getCreatedAt(),
                a.getUpdatedAt()
        );
    }

    /**
     * TurmaRequestDTO — 6 campos: nome, anoLetivo, anoEscolar,
     * professorRegenteId, capacidadeMaxima, ativa
     */
    private void aplicarDTO(Turma t, TurmaRequestDTO dto) {
        t.setNome(dto.nome());
        t.setAnoLetivo(dto.anoLetivo());
        t.setAnoEscolar(dto.anoEscolar());
        t.setCapacidadeMaxima(dto.capacidadeMaxima());
        if (dto.professorRegenteId() != null) {
            Professor prof = professorRepository.findById(dto.professorRegenteId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Professor não encontrado: " + dto.professorRegenteId()));
            t.setProfessorRegente(prof);
        } else {
            t.setProfessorRegente(null);
        }
        if (dto.ativa() != null) {
            t.setAtiva(dto.ativa());
        }
    }
}
