package com.waldorf.application.service;

import com.waldorf.application.dto.financeiro.ContratoRequestDTO;
import com.waldorf.domain.entity.Aluno;
import com.waldorf.domain.entity.Contrato;
import com.waldorf.domain.enums.SituacaoContrato;
import com.waldorf.infrastructure.repository.AlunoRepository;
import com.waldorf.infrastructure.repository.ContratoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FinanceiroServiceTest {

    @Mock private ContratoRepository contratoRepository;
    @Mock private AlunoRepository alunoRepository;
    @Mock private MensalidadeService mensalidadeService;

    private ContratoService contratoService;

    @BeforeEach
    void setUp() {
        contratoService = new ContratoService(contratoRepository, alunoRepository, mensalidadeService);
    }

    @Test
    void criarContrato_comDadosValidos() {
        Aluno aluno = new Aluno();
        aluno.setId(1L);
        aluno.setNome("Teste");
        aluno.setMatricula("202600001");

        ContratoRequestDTO dto = new ContratoRequestDTO(
                1L, null, 2026, new BigDecimal("1500.00"),
                BigDecimal.ZERO, BigDecimal.ZERO, 12, 10,
                LocalDate.of(2026, 1, 1), null, null
        );

        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(contratoRepository.save(any(Contrato.class))).thenAnswer(inv -> {
            Contrato c = inv.getArgument(0);
            c.setId(1L);
            return c;
        });

        var result = contratoService.criar(dto);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals(SituacaoContrato.ATIVO, result.situacao());
        verify(mensalidadeService).gerarMensalidades(any());
    }
}
