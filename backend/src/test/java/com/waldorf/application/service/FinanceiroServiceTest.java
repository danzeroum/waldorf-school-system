package com.waldorf.application.service;

import com.waldorf.application.dto.financeiro.ContratoRequestDTO;
import com.waldorf.domain.entity.Aluno;
import com.waldorf.domain.entity.Contrato;
import com.waldorf.domain.enums.SituacaoContrato;
import com.waldorf.infrastructure.repository.AlunoRepository;
import com.waldorf.infrastructure.repository.ContratoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FinanceiroService — testes unitários")
class FinanceiroServiceTest {

    @Mock ContratoRepository  contratoRepository;
    @Mock AlunoRepository     alunoRepository;
    @Mock MensalidadeService  mensalidadeService;  // obrigatório: ContratoService tem 3 deps

    @InjectMocks ContratoService contratoService;

    @Test
    @DisplayName("criar contrato deve persistir sem gerar mensalidades")
    void criarContrato() {
        Aluno aluno = new Aluno();
        aluno.setId(1L);
        aluno.setNome("Pedro Santos");

        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(contratoRepository.save(any())).thenAnswer(inv -> {
            Contrato c = inv.getArgument(0);
            c.setId(1L);
            c.setCreatedAt(LocalDateTime.now());
            return c;
        });

        var dto = new ContratoRequestDTO(
                1L, null, 2026,
                new BigDecimal("1200.00"),
                new BigDecimal("300.00"),
                12, 10,
                LocalDate.of(2026, 2, 1), null, null);

        var resp = contratoService.criar(dto);

        assertThat(resp.valorMensalidade()).isEqualByComparingTo(new BigDecimal("1200.00"));
        assertThat(resp.totalParcelas()).isEqualTo(12);
        verify(contratoRepository).save(any());
        // criar() não deve gerar mensalidades — isso só acontece em ativar()
        verifyNoInteractions(mensalidadeService);
    }

    @Test
    @DisplayName("buscar contrato inexistente deve lançar EntityNotFoundException")
    void buscarInexistente() {
        when(contratoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> contratoService.buscarPorId(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("encerrar contrato ativo deve mudar situação para ENCERRADO")
    void encerrarContrato() {
        Contrato c = new Contrato();
        c.setId(2L);
        c.setSituacao(SituacaoContrato.ATIVO);
        Aluno aluno = new Aluno();
        aluno.setId(1L);
        aluno.setNome("Maria");
        c.setAluno(aluno);
        c.setAnoLetivo(2026);
        c.setValorMensalidade(new BigDecimal("800.00"));
        c.setDesconto(BigDecimal.ZERO);
        c.setTotalParcelas(10);
        c.setDiaVencimento(10);
        c.setCreatedAt(LocalDateTime.now());

        when(contratoRepository.findById(2L)).thenReturn(Optional.of(c));
        when(contratoRepository.save(any())).thenReturn(c);

        var resp = contratoService.encerrar(2L, "Desistiu");

        assertThat(resp.situacao()).isEqualTo(SituacaoContrato.ENCERRADO);
        verify(contratoRepository).save(c);
        verifyNoInteractions(mensalidadeService);
    }
}
