package br.edu.waldorf.modules.financeiro.domain.service;

import br.edu.waldorf.modules.financeiro.domain.model.Contrato;
import br.edu.waldorf.modules.financeiro.domain.model.Mensalidade;
import br.edu.waldorf.modules.financeiro.domain.repository.ContratoRepository;
import br.edu.waldorf.modules.financeiro.domain.repository.MensalidadeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Service de domínio para Contrato
 * Regras: criar, ativar, suspender, encerrar + gerar mensalidades
 *
 * @author Sistema Waldorf
 * @version 1.0.0
 * @since 2026-03-11
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContratoService {

    private final ContratoRepository contratoRepository;
    private final MensalidadeRepository mensalidadeRepository;

    @Transactional(readOnly = true)
    public Page<Contrato> listarComFiltros(Contrato.SituacaoContrato situacao, Integer anoLetivo, Pageable pageable) {
        return contratoRepository.findWithFilters(situacao, anoLetivo, pageable);
    }

    @Transactional(readOnly = true)
    public Contrato buscarPorId(Long id) {
        return contratoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contrato não encontrado: id=" + id));
    }

    @Transactional(readOnly = true)
    public List<Contrato> historicoAluno(Long alunoId) {
        return contratoRepository.findByAlunoIdOrderByAnoLetivoDesc(alunoId);
    }

    @Transactional(readOnly = true)
    public List<Contrato> listarPendentes() {
        return contratoRepository.findPendentes();
    }

    @Transactional
    public Contrato criar(Contrato contrato) {
        // Verifica se já existe contrato ativo/pendente para este aluno no ano
        contratoRepository.findByAlunoIdAndAnoLetivoAndSituacaoNot(
                contrato.getAluno().getId(),
                contrato.getAnoLetivo(),
                Contrato.SituacaoContrato.CANCELADO
        ).ifPresent(c -> {
            if (c.getSituacao() != Contrato.SituacaoContrato.ENCERRADO) {
                throw new IllegalStateException(
                        "Já existe contrato para este aluno no ano " + contrato.getAnoLetivo() + ": id=" + c.getId()
                );
            }
        });

        contrato.setNumeroContrato(gerarNumeroContrato(contrato));
        Contrato salvo = contratoRepository.save(contrato);
        log.info("Contrato criado: id={}, numero={}, aluno={}",
                salvo.getId(), salvo.getNumeroContrato(), salvo.getAluno().getId());
        return salvo;
    }

    @Transactional
    public Contrato ativar(Long id) {
        Contrato contrato = buscarPorId(id);
        contrato.ativar();
        Contrato ativo = contratoRepository.save(contrato);
        // Gera as mensalidades ao ativar
        gerarMensalidades(ativo);
        log.info("Contrato ativado e mensalidades geradas: id={}", id);
        return ativo;
    }

    @Transactional
    public void suspender(Long id) {
        Contrato c = buscarPorId(id);
        c.suspender();
        contratoRepository.save(c);
        log.info("Contrato suspenso: id={}", id);
    }

    @Transactional
    public void reativar(Long id) {
        Contrato c = buscarPorId(id);
        c.reativar();
        contratoRepository.save(c);
        log.info("Contrato reativado: id={}", id);
    }

    @Transactional
    public void cancelar(Long id) {
        Contrato c = buscarPorId(id);
        c.cancelar();
        // Cancela mensalidades em aberto
        mensalidadeRepository.findByContratoIdOrderByNumeroParcela(id)
                .stream()
                .filter(Mensalidade::isEmAberto)
                .forEach(m -> {
                    m.cancelar();
                    mensalidadeRepository.save(m);
                });
        contratoRepository.save(c);
        log.info("Contrato cancelado: id={}", id);
    }

    // --- Geração de mensalidades ---

    private void gerarMensalidades(Contrato contrato) {
        int parcelas = contrato.getPlano().getNumeroParcelas();
        YearMonth inicio = YearMonth.from(contrato.getDataInicioVigencia());
        List<Mensalidade> lista = new ArrayList<>();

        for (int i = 0; i < parcelas; i++) {
            YearMonth ym = inicio.plusMonths(i);
            // Vencimento no dia 10 de cada mês
            LocalDate vencimento = ym.atDay(10);

            lista.add(Mensalidade.builder()
                    .contrato(contrato)
                    .numeroParcela(i + 1)
                    .mesReferencia(ym.getMonthValue())
                    .anoReferencia(ym.getYear())
                    .valorParcela(contrato.getValorFinal())
                    .dataVencimento(vencimento)
                    .status(Mensalidade.StatusMensalidade.ABERTA)
                    .build());
        }
        mensalidadeRepository.saveAll(lista);
        log.info("{} mensalidades geradas para contrato id={}", lista.size(), contrato.getId());
    }

    private String gerarNumeroContrato(Contrato contrato) {
        String ano  = String.valueOf(contrato.getAnoLetivo());
        String seq  = String.format("%05d", contrato.getAluno().getId());
        String data = LocalDate.now().format(DateTimeFormatter.ofPattern("MMdd"));
        return "CTR-" + ano + "-" + seq + "-" + data;
    }
}
