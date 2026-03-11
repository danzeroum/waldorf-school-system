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

    @Mock ContratoRepository contratoRepository;
    @Mock AlunoRepository    alunoRepository;

    @InjectMocks ContratoService contratoService;

    @Test
    @DisplayName("criar contrato deve gerar parcelas mensais automaticamente")
    void criarContratoGerarParcelas() {
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
    }

    @Test
    @DisplayName("buscar contrato inexistente deve lançar exceção")
    void buscarInexistente() {
        when(contratoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> contratoService.buscarPorId(99L))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
