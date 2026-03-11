package br.edu.waldorf.modules.escolar.domain.service;

import br.edu.waldorf.modules.escolar.domain.model.Matricula;
import br.edu.waldorf.modules.escolar.domain.model.Turma;
import br.edu.waldorf.modules.escolar.domain.repository.MatriculaRepository;
import br.edu.waldorf.modules.pessoa.domain.model.Aluno;
import br.edu.waldorf.modules.pessoa.domain.repository.AlunoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Service de domínio para Matrícula
 * Contém as regras de negócio do processo de matrícula
 *
 * @author Sistema Waldorf
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MatriculaService {

    private final MatriculaRepository matriculaRepository;
    private final AlunoRepository alunoRepository;
    private final TurmaService turmaService;

    @Transactional(readOnly = true)
    public Page<Matricula> listarComFiltros(Long turmaId, Integer anoLetivo, Matricula.SituacaoMatricula situacao, Pageable pageable) {
        return matriculaRepository.findWithFilters(turmaId, anoLetivo, situacao, pageable);
    }

    @Transactional(readOnly = true)
    public Matricula buscarPorId(Long id) {
        return matriculaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Matrícula não encontrada: id=" + id));
    }

    @Transactional(readOnly = true)
    public List<Matricula> historicoAluno(Long alunoId) {
        return matriculaRepository.findByAlunoIdOrderByAnoLetivoDesc(alunoId);
    }

    @Transactional(readOnly = true)
    public List<Matricula> listarAtivasPorTurma(Long turmaId) {
        return matriculaRepository.findAtivasByTurma(turmaId);
    }

    @Transactional
    public Matricula matricular(Long alunoId, Long turmaId, Matricula.FormaIngresso formaIngresso) {
        // 1. Valida aluno
        Aluno aluno = alunoRepository.findById(alunoId)
                .orElseThrow(() -> new EntityNotFoundException("Aluno não encontrado: id=" + alunoId));

        // 2. Valida turma
        Turma turma = turmaService.buscarPorId(turmaId);

        // 3. Verifica duplicidade
        int anoAtual = LocalDate.now().getYear();
        if (matriculaRepository.existsByAlunoIdAndTurmaIdAndAnoLetivo(alunoId, turmaId, anoAtual)) {
            throw new IllegalStateException("Aluno já matriculado nesta turma no ano corrente");
        }

        // 4. Verifica vagas
        if (!turma.possuiVagas()) {
            throw new IllegalStateException("Turma sem vagas disponíveis: " + turma.getNome());
        }

        // 5. Cria matrícula
        Matricula matricula = Matricula.builder()
                .aluno(aluno)
                .turma(turma)
                .anoLetivo(anoAtual)
                .dataMatricula(LocalDate.now())
                .formaIngresso(formaIngresso)
                .tipoEnsino(Matricula.TipoEnsino.REGULAR)
                .situacao(Matricula.SituacaoMatricula.EM_ANDAMENTO)
                .build();

        Matricula salva = matriculaRepository.save(matricula);

        // 6. Atualiza vagas da turma
        turmaService.atualizarVagas(turmaId);

        log.info("Aluno {} matriculado na turma {} ({})", alunoId, turmaId, anoAtual);
        return salva;
    }

    @Transactional
    public void cancelar(Long id, String motivo) {
        Matricula matricula = buscarPorId(id);
        if (!matricula.isAtiva()) {
            throw new IllegalStateException("Somente matrículas ativas podem ser canceladas");
        }
        matricula.cancelar(motivo);
        matriculaRepository.save(matricula);
        turmaService.atualizarVagas(matricula.getTurma().getId());
        log.info("Matrícula {} cancelada", id);
    }

    @Transactional
    public void transferir(Long id, Long novaTurmaId) {
        Matricula matriculaOrigem = buscarPorId(id);
        if (!matriculaOrigem.isAtiva()) {
            throw new IllegalStateException("Somente matrículas ativas podem ser transferidas");
        }
        Long alunoId = matriculaOrigem.getAluno().getId();
        matriculaOrigem.transferir();
        matriculaRepository.save(matriculaOrigem);
        turmaService.atualizarVagas(matriculaOrigem.getTurma().getId());

        // Re-matricula na nova turma
        matricular(alunoId, novaTurmaId, Matricula.FormaIngresso.TRANSFERENCIA);
        log.info("Aluno {} transferido para turma {}", alunoId, novaTurmaId);
    }
}
