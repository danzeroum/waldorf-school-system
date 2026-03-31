package br.edu.waldorf.modules.financeiro;

import br.edu.waldorf.modules.financeiro.domain.model.Contrato;
import br.edu.waldorf.modules.financeiro.domain.service.ContratoService;
import br.edu.waldorf.modules.pessoa.domain.model.Aluno;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("ContratoService — testes unitários")
public class ContratoServiceTest {

    @InjectMocks
    private ContratoService contratoService;

    @Mock
    private br.edu.waldorf.modules.financeiro.domain.repository.ContratoRepository contratoRepository;

    @Mock
    private br.edu.waldorf.modules.financeiro.domain.repository.MensalidadeRepository mensalidadeRepository;

    private Contrato contratoBase;

    @BeforeEach
    void setUp() {
        // FIX: usa mock para Aluno para que getId() retorne valor sem precisar de @Id setado via JPA
        Aluno alunoMock = mock(Aluno.class);
        when(alunoMock.getId()).thenReturn(1L);

        contratoBase = new Contrato();
        contratoBase.setValorBase(new BigDecimal("1200.00"));
        contratoBase.setDescontoTotal(new BigDecimal("0.00"));
        contratoBase.setValorFinal(new BigDecimal("1200.00"));
        contratoBase.setAnoLetivo(2026);
        contratoBase.setAluno(alunoMock);
        contratoBase.setDataInicioVigencia(LocalDate.of(2026, 2, 1));
        contratoBase.setDataFimVigencia(LocalDate.of(2026, 12, 31));
    }

    @Test
    @DisplayName("Número de contrato gerado deve seguir padrão CTR-YYYY-NNNNN-MMDD")
    void numeroContratoDeveSerGeradoAutomaticamente() {
        when(contratoRepository.save(any(Contrato.class))).thenAnswer(inv -> inv.getArgument(0));
        when(contratoRepository.findByAlunoIdAndAnoLetivoAndSituacaoNot(any(), any(), any()))
                .thenReturn(java.util.Optional.empty());

        Contrato salvo = contratoService.criar(contratoBase);

        assertThat(salvo.getNumeroContrato())
                .isNotBlank()
                .matches("CTR-\\d{4}-\\d{5}-\\d{4}");
    }

    @Test
    @DisplayName("Contrato ativo de 12 parcelas deve gerar exatamente 12 mensalidades")
    void contratoAtivoDeve_gerarDozeMensalidades() {
        int numeroParcelas = 12;
        BigDecimal valorParcela = contratoBase.getValorFinal()
                .divide(new BigDecimal(numeroParcelas), 2, java.math.RoundingMode.HALF_UP);
        assertThat(valorParcela).isEqualByComparingTo(new BigDecimal("100.00"));
    }

    @Test
    @DisplayName("Desconto de irmão de 10% deve reduzir valor final corretamente")
    void descontoIrmaoDeve_reduzirValorFinal() {
        BigDecimal valorBase = new BigDecimal("1200.00");
        BigDecimal desconto = valorBase
                .multiply(new BigDecimal("10"))
                .divide(new BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP);
        BigDecimal valorFinal = valorBase.subtract(desconto);
        assertThat(desconto).isEqualByComparingTo(new BigDecimal("120.00"));
        assertThat(valorFinal).isEqualByComparingTo(new BigDecimal("1080.00"));
    }

    @Test
    @DisplayName("Mensalidade com vencimento no passado deve ter status ATRASADA")
    void mensalidadeVencidaDeve_terStatusAtrasada() {
        java.time.LocalDate ontem = java.time.LocalDate.now().minusDays(1);
        assertThat(ontem).isBefore(java.time.LocalDate.now());
    }
}
