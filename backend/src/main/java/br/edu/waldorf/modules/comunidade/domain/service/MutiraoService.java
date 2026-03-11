package br.edu.waldorf.modules.comunidade.domain.service;

import br.edu.waldorf.modules.comunidade.domain.model.InscricaoEvento;
import br.edu.waldorf.modules.comunidade.domain.model.Mutirao;
import br.edu.waldorf.modules.comunidade.domain.repository.InscricaoEventoRepository;
import br.edu.waldorf.modules.comunidade.domain.repository.MutiraoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Service de dominio para Mutirao Comunitario
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MutiraoService {

    private final MutiraoRepository mutiraoRepository;
    private final InscricaoEventoRepository inscricaoRepository;

    @Transactional(readOnly = true)
    public List<Mutirao> listarProximos() {
        return mutiraoRepository.findProximos(LocalDate.now());
    }

    @Transactional(readOnly = true)
    public Mutirao buscarPorId(Long id) {
        return mutiraoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Mutirao nao encontrado: id=" + id));
    }

    @Transactional
    public Mutirao criar(Mutirao mutirao) {
        Mutirao salvo = mutiraoRepository.save(mutirao);
        log.info("Mutirao criado: id={}, nome={}", salvo.getId(), salvo.getNome());
        return salvo;
    }

    @Transactional
    public void confirmar(Long id) {
        Mutirao m = buscarPorId(id);
        m.confirmar();
        mutiraoRepository.save(m);
    }

    @Transactional
    public void concluir(Long id) {
        Mutirao m = buscarPorId(id);
        m.concluir();
        mutiraoRepository.save(m);
    }

    @Transactional
    public void cancelar(Long id) {
        Mutirao m = buscarPorId(id);
        m.cancelar();
        mutiraoRepository.save(m);
    }

    @Transactional
    public InscricaoEvento inscrever(Long mutiraoId, Long pessoaId, int numPessoas, int criancas, String materiais) {
        Mutirao mutirao = buscarPorId(mutiraoId);

        if (!mutirao.possuiVagas()) {
            throw new IllegalStateException("Mutirao sem vagas disponiveis: " + mutirao.getNome());
        }
        if (inscricaoRepository.existsByTipoEventoAndEventoIdAndPessoaId(
                InscricaoEvento.TipoEvento.MUTIRAO, mutiraoId, pessoaId)) {
            throw new IllegalStateException("Pessoa ja inscrita neste mutirao");
        }

        InscricaoEvento inscricao = InscricaoEvento.builder()
                .tipoEvento(InscricaoEvento.TipoEvento.MUTIRAO)
                .eventoId(mutiraoId)
                .numeroPessoas(numPessoas)
                .criancasIncluidas(criancas)
                .materiaisTrazidos(materiais)
                .confirmado(false)
                .build();
        return inscricaoRepository.save(inscricao);
    }
}
