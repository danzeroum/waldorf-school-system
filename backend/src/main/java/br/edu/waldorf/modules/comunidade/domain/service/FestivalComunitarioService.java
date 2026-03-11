package br.edu.waldorf.modules.comunidade.domain.service;

import br.edu.waldorf.modules.comunidade.domain.model.FestivalComunitario;
import br.edu.waldorf.modules.comunidade.domain.model.InscricaoEvento;
import br.edu.waldorf.modules.comunidade.domain.repository.FestivalComunitarioRepository;
import br.edu.waldorf.modules.comunidade.domain.repository.InscricaoEventoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Service de dominio para Festival Comunitario
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FestivalComunitarioService {

    private final FestivalComunitarioRepository festivalRepository;
    private final InscricaoEventoRepository inscricaoRepository;

    @Transactional(readOnly = true)
    public List<FestivalComunitario> listarProximos() {
        return festivalRepository.findProximos(LocalDate.now());
    }

    @Transactional(readOnly = true)
    public FestivalComunitario buscarPorId(Long id) {
        return festivalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Festival nao encontrado: id=" + id));
    }

    @Transactional
    public FestivalComunitario criar(FestivalComunitario festival) {
        FestivalComunitario salvo = festivalRepository.save(festival);
        log.info("Festival criado: id={}, nome={}", salvo.getId(), salvo.getNome());
        return salvo;
    }

    @Transactional
    public void confirmar(Long id) {
        FestivalComunitario f = buscarPorId(id);
        f.confirmar();
        festivalRepository.save(f);
    }

    @Transactional
    public void concluir(Long id) {
        FestivalComunitario f = buscarPorId(id);
        f.concluir();
        festivalRepository.save(f);
    }

    @Transactional
    public void cancelar(Long id) {
        FestivalComunitario f = buscarPorId(id);
        f.cancelar();
        festivalRepository.save(f);
    }

    @Transactional
    public InscricaoEvento inscrever(Long festivalId, Long pessoaId, int numPessoas, int criancas) {
        FestivalComunitario festival = buscarPorId(festivalId);

        if (!festival.possuiVagas()) {
            throw new IllegalStateException("Festival sem vagas disponiveis: " + festival.getNome());
        }
        if (inscricaoRepository.existsByTipoEventoAndEventoIdAndPessoaId(
                InscricaoEvento.TipoEvento.FESTIVAL, festivalId, pessoaId)) {
            throw new IllegalStateException("Pessoa ja inscrita neste festival");
        }

        InscricaoEvento inscricao = InscricaoEvento.builder()
                .tipoEvento(InscricaoEvento.TipoEvento.FESTIVAL)
                .eventoId(festivalId)
                .numeroPessoas(numPessoas)
                .criancasIncluidas(criancas)
                .confirmado(false)
                .build();
        // pessoa sera injetada no controller
        return inscricaoRepository.save(inscricao);
    }
}
