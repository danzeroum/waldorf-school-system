package br.edu.waldorf.modules.pedagogia.domain.service;

import br.edu.waldorf.modules.pedagogia.domain.model.ObservacaoDesenvolvimento;
import br.edu.waldorf.modules.pedagogia.domain.repository.ObservacaoDesenvolvimentoRepository;
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
 * Service de domínio para Observação de Desenvolvimento
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ObservacaoDesenvolvimentoService {

    private final ObservacaoDesenvolvimentoRepository repository;

    @Transactional(readOnly = true)
    public Page<ObservacaoDesenvolvimento> listarPorAluno(Long alunoId, Pageable pageable) {
        return repository.findByAlunoIdOrderByDataObservacaoDesc(alunoId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<ObservacaoDesenvolvimento> listarComFiltros(
            Long alunoId,
            ObservacaoDesenvolvimento.AspectoDensenvolvimento aspecto,
            Long professorId,
            Pageable pageable
    ) {
        return repository.findWithFilters(alunoId, aspecto, professorId, pageable);
    }

    @Transactional(readOnly = true)
    public ObservacaoDesenvolvimento buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Observação não encontrada: id=" + id));
    }

    @Transactional(readOnly = true)
    public List<ObservacaoDesenvolvimento> listarRecentesProfessor(Long professorId, int dias) {
        LocalDate desde = LocalDate.now().minusDays(dias);
        return repository.findRecentesByProfessor(professorId, desde);
    }

    @Transactional(readOnly = true)
    public List<ObservacaoDesenvolvimento> listarPorTurmaEPeriodo(Long turmaId, LocalDate inicio, LocalDate fim) {
        return repository.findByTurmaAndPeriodo(turmaId, inicio, fim);
    }

    @Transactional
    public ObservacaoDesenvolvimento registrar(ObservacaoDesenvolvimento obs) {
        if (obs.getDataObservacao() == null) {
            obs.setDataObservacao(LocalDate.now());
        }
        ObservacaoDesenvolvimento salva = repository.save(obs);
        log.info("Observação registrada: id={}, aluno={}, aspecto={}",
                salva.getId(), salva.getAluno().getId(), salva.getAspecto());
        return salva;
    }

    @Transactional
    public ObservacaoDesenvolvimento atualizar(Long id, ObservacaoDesenvolvimento dados) {
        ObservacaoDesenvolvimento existente = buscarPorId(id);
        existente.setTitulo(dados.getTitulo());
        existente.setDescricao(dados.getDescricao());
        existente.setAspecto(dados.getAspecto());
        existente.setEvidencias(dados.getEvidencias());
        existente.setSugestoesApoio(dados.getSugestoesApoio());
        existente.setPrivado(dados.getPrivado());
        existente.setCompartilharPais(dados.getCompartilharPais());
        return repository.save(existente);
    }

    @Transactional
    public void excluir(Long id) {
        ObservacaoDesenvolvimento obs = buscarPorId(id);
        repository.delete(obs);
        log.info("Observação excluída: id={}", id);
    }
}
