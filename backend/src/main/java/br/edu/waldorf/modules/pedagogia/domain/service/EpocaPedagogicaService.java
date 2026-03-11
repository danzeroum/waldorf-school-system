package br.edu.waldorf.modules.pedagogia.domain.service;

import br.edu.waldorf.modules.pedagogia.domain.model.EpocaPedagogica;
import br.edu.waldorf.modules.pedagogia.domain.repository.EpocaPedagogicaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service de domínio para Época Pedagógica
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EpocaPedagogicaService {

    private final EpocaPedagogicaRepository repository;

    @Transactional(readOnly = true)
    public List<EpocaPedagogica> listarPorTurma(Long turmaId) {
        return repository.findByTurmaIdOrderByDataInicio(turmaId);
    }

    @Transactional(readOnly = true)
    public Optional<EpocaPedagogica> buscarEmAndamento(Long turmaId) {
        return repository.findEmAndamentoByTurma(turmaId);
    }

    @Transactional(readOnly = true)
    public EpocaPedagogica buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Época pedagógica não encontrada: id=" + id));
    }

    @Transactional
    public EpocaPedagogica criar(EpocaPedagogica epoca) {
        validarPeriodo(epoca);
        EpocaPedagogica salva = repository.save(epoca);
        log.info("Época criada: id={}, turma={}, titulo={}", salva.getId(), salva.getTurma().getId(), salva.getTitulo());
        return salva;
    }

    @Transactional
    public EpocaPedagogica atualizar(Long id, EpocaPedagogica dados) {
        EpocaPedagogica existente = buscarPorId(id);
        existente.setTitulo(dados.getTitulo());
        existente.setDescricao(dados.getDescricao());
        existente.setDataInicio(dados.getDataInicio());
        existente.setDataFim(dados.getDataFim());
        existente.setTemaCentral(dados.getTemaCentral());
        existente.setNarrativaIntrodutoria(dados.getNarrativaIntrodutoria());
        existente.setAtividadesPrincipais(dados.getAtividadesPrincipais());
        existente.setMateriaisNecessarios(dados.getMateriaisNecessarios());
        existente.setObjetivoDesenvolvimento(dados.getObjetivosDesenvolvimento());
        existente.setCorEpoca(dados.getCorEpoca());
        return repository.save(existente);
    }

    @Transactional
    public void iniciar(Long id) {
        EpocaPedagogica epoca = buscarPorId(id);
        // Garante que não há outra em andamento na mesma turma
        repository.findEmAndamentoByTurma(epoca.getTurma().getId())
                .ifPresent(e -> {
                    if (!e.getId().equals(id)) {
                        throw new IllegalStateException("Já existe uma época em andamento nesta turma: id=" + e.getId());
                    }
                });
        epoca.iniciar();
        repository.save(epoca);
        log.info("Época iniciada: id={}", id);
    }

    @Transactional
    public void concluir(Long id) {
        EpocaPedagogica epoca = buscarPorId(id);
        epoca.concluir();
        repository.save(epoca);
        log.info("Época concluída: id={}", id);
    }

    private void validarPeriodo(EpocaPedagogica epoca) {
        if (epoca.getDataFim().isBefore(epoca.getDataInicio())) {
            throw new IllegalArgumentException("Data fim não pode ser anterior à data início");
        }
    }
}
