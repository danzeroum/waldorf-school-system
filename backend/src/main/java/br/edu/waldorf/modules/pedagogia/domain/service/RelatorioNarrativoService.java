package br.edu.waldorf.modules.pedagogia.domain.service;

import br.edu.waldorf.modules.pedagogia.domain.model.RelatorioNarrativo;
import br.edu.waldorf.modules.pedagogia.domain.repository.RelatorioNarrativoRepository;
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
 * Service de domínio para Relatório Narrativo Waldorf
 * Controla o fluxo: RASCUNHO -> REVISAO -> APROVADO -> ENTREGUE
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RelatorioNarrativoService {

    private final RelatorioNarrativoRepository repository;

    @Transactional(readOnly = true)
    public List<RelatorioNarrativo> listarPorAluno(Long alunoId) {
        return repository.findByAlunoIdOrderByDataElaboracaoDesc(alunoId);
    }

    @Transactional(readOnly = true)
    public List<RelatorioNarrativo> listarPorTurmaECiclo(Long turmaId, String ciclo) {
        return repository.findByTurmaECiclo(turmaId, ciclo);
    }

    @Transactional(readOnly = true)
    public Page<RelatorioNarrativo> listarPorStatus(RelatorioNarrativo.StatusRelatorio status, Pageable pageable) {
        return repository.findByStatus(status, pageable);
    }

    @Transactional(readOnly = true)
    public RelatorioNarrativo buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Relatório narrativo não encontrado: id=" + id));
    }

    @Transactional
    public RelatorioNarrativo criar(RelatorioNarrativo relatorio) {
        // Verifica duplicidade para o mesmo aluno/ciclo/período
        repository.findByAlunoIdAndCicloAndPeriodo(
                relatorio.getAluno().getId(),
                relatorio.getCiclo(),
                relatorio.getPeriodo()
        ).ifPresent(r -> {
            throw new IllegalStateException(
                    "Já existe relatório para este aluno/ciclo/período: id=" + r.getId()
            );
        });
        if (relatorio.getDataElaboracao() == null) {
            relatorio.setDataElaboracao(LocalDate.now());
        }
        RelatorioNarrativo salvo = repository.save(relatorio);
        log.info("Relatório narrativo criado: id={}, aluno={}, ciclo={}",
                salvo.getId(), salvo.getAluno().getId(), salvo.getCiclo());
        return salvo;
    }

    @Transactional
    public RelatorioNarrativo atualizar(Long id, RelatorioNarrativo dados) {
        RelatorioNarrativo existente = buscarPorId(id);
        if (existente.getStatus() == RelatorioNarrativo.StatusRelatorio.ENTREGUE) {
            throw new IllegalStateException("Relatórios ENTREGUES não podem ser editados");
        }
        existente.setTitulo(dados.getTitulo());
        existente.setTextoDesenvolvimentoFisico(dados.getTextoDesenvolvimentoFisico());
        existente.setTextoDesenvolvimentoAnimico(dados.getTextoDesenvolvimentoAnimico());
        existente.setTextoDesenvolvimentoCognitivo(dados.getTextoDesenvolvimentoCognitivo());
        existente.setTextoRelacaoSocial(dados.getTextoRelacaoSocial());
        existente.setTextoObservacoesArtisticas(dados.getTextoObservacoesArtisticas());
        existente.setTextoTrabalhosManuais(dados.getTextoTrabalhosManuais());
        existente.setTextoConclusaoConvite(dados.getTextoConclusaoConvite());
        return repository.save(existente);
    }

    @Transactional
    public void enviarParaRevisao(Long id) {
        RelatorioNarrativo rel = buscarPorId(id);
        rel.enviarParaRevisao();
        repository.save(rel);
        log.info("Relatório enviado para revisão: id={}", id);
    }

    @Transactional
    public void aprovar(Long id) {
        RelatorioNarrativo rel = buscarPorId(id);
        rel.aprovar();
        repository.save(rel);
        log.info("Relatório aprovado: id={}", id);
    }

    @Transactional
    public void entregar(Long id) {
        RelatorioNarrativo rel = buscarPorId(id);
        rel.entregar();
        repository.save(rel);
        log.info("Relatório entregue: id={}", id);
    }

    @Transactional
    public void confirmarLeitura(Long id) {
        RelatorioNarrativo rel = buscarPorId(id);
        rel.confirmarLeitura();
        repository.save(rel);
        log.info("Leitura do relatório confirmada pelo responsável: id={}", id);
    }
}
