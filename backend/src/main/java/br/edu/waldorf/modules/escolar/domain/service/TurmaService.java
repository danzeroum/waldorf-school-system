package br.edu.waldorf.modules.escolar.domain.service;

import br.edu.waldorf.modules.escolar.domain.model.Turma;
import br.edu.waldorf.modules.escolar.domain.repository.MatriculaRepository;
import br.edu.waldorf.modules.escolar.domain.repository.TurmaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service de domínio para Turma
 *
 * @author Sistema Waldorf
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TurmaService {

    private final TurmaRepository turmaRepository;
    private final MatriculaRepository matriculaRepository;

    @Transactional(readOnly = true)
    public List<Turma> listarPorAnoLetivo(Integer anoLetivo) {
        return turmaRepository.findByAnoLetivo(anoLetivo);
    }

    @Transactional(readOnly = true)
    public Page<Turma> listarComFiltros(Turma.SituacaoTurma situacao, Integer anoLetivo, Long cursoId, Pageable pageable) {
        return turmaRepository.findWithFilters(situacao, anoLetivo, cursoId, pageable);
    }

    @Transactional(readOnly = true)
    public Turma buscarPorId(Long id) {
        return turmaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Turma não encontrada: id=" + id));
    }

    @Transactional(readOnly = true)
    public Turma buscarPorCodigo(String codigo) {
        return turmaRepository.findByCodigo(codigo)
                .orElseThrow(() -> new EntityNotFoundException("Turma não encontrada: codigo=" + codigo));
    }

    @Transactional(readOnly = true)
    public List<Turma> listarPorProfessor(Long professorId) {
        return turmaRepository.findByProfessorTitular(professorId);
    }

    @Transactional
    public Turma criar(Turma turma) {
        if (turmaRepository.existsByCodigo(turma.getCodigo())) {
            throw new IllegalArgumentException("Código de turma já existe: " + turma.getCodigo());
        }
        turma.setVagasDisponiveis(turma.getCapacidadeMaxima());
        Turma salva = turmaRepository.save(turma);
        log.info("Turma criada: id={}, codigo={}", salva.getId(), salva.getCodigo());
        return salva;
    }

    @Transactional
    public Turma atualizar(Long id, Turma dados) {
        Turma existente = buscarPorId(id);
        if (dados.getCodigo() != null && !dados.getCodigo().equals(existente.getCodigo())) {
            if (turmaRepository.existsByCodigo(dados.getCodigo())) {
                throw new IllegalArgumentException("Código de turma já existe: " + dados.getCodigo());
            }
            existente.setCodigo(dados.getCodigo());
        }
        existente.setNome(dados.getNome());
        existente.setSerie(dados.getSerie());
        existente.setTurno(dados.getTurno());
        existente.setSala(dados.getSala());
        existente.setCapacidadeMaxima(dados.getCapacidadeMaxima());
        existente.setDataInicio(dados.getDataInicio());
        existente.setDataFim(dados.getDataFim());
        existente.setCorTurma(dados.getCorTurma());
        return turmaRepository.save(existente);
    }

    @Transactional
    public void iniciarTurma(Long id) {
        Turma turma = buscarPorId(id);
        if (turma.getSituacao() != Turma.SituacaoTurma.ABERTA) {
            throw new IllegalStateException("Somente turmas ABERTAS podem ser iniciadas");
        }
        turma.iniciar();
        turmaRepository.save(turma);
        log.info("Turma iniciada: id={}", id);
    }

    @Transactional
    public void concluirTurma(Long id) {
        Turma turma = buscarPorId(id);
        turma.concluir();
        turmaRepository.save(turma);
        log.info("Turma concluída: id={}", id);
    }

    @Transactional
    public void atualizarVagas(Long turmaId) {
        Turma turma = buscarPorId(turmaId);
        long ocupadas = matriculaRepository.findAtivasByTurma(turmaId).size();
        turma.setVagasDisponiveis((int) (turma.getCapacidadeMaxima() - ocupadas));
        turmaRepository.save(turma);
    }
}
