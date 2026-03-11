package com.waldorf.application.service;

import com.waldorf.domain.entity.SolicitacaoTitular;
import com.waldorf.domain.enums.StatusSolicitacao;
import com.waldorf.domain.enums.TipoSolicitacao;
import com.waldorf.infrastructure.repository.SolicitacaoTitularRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LgpdService — testes unitários")
class LgpdServiceTest {

    @Mock SolicitacaoTitularRepository repository;
    @InjectMocks LgpdService lgpdService;

    private SolicitacaoTitular solicitacao;

    @BeforeEach
    void setUp() {
        solicitacao = new SolicitacaoTitular();
        solicitacao.setId(1L);
        solicitacao.setTipo(TipoSolicitacao.ACESSO);
        solicitacao.setStatus(StatusSolicitacao.ABERTA);
        solicitacao.setPrazo(LocalDate.now().plusDays(15));
        solicitacao.setDescricao("Solicito acesso aos meus dados");
    }

    @Test
    @DisplayName("avançar status de ABERTA para EM_ANALISE deve funcionar")
    void avancarStatusValido() {
        when(repository.findById(1L)).thenReturn(Optional.of(solicitacao));
        when(repository.save(any())).thenReturn(solicitacao);

        lgpdService.avancarStatus(1L);

        assertThat(solicitacao.getStatus()).isEqualTo(StatusSolicitacao.EM_ANALISE);
    }

    @Test
    @DisplayName("concluir solicitação deve setar status CONCLUIDA e resposta")
    void concluirSolicitacao() {
        when(repository.findById(1L)).thenReturn(Optional.of(solicitacao));
        when(repository.save(any())).thenReturn(solicitacao);

        lgpdService.concluir(1L, "Dados enviados por e-mail conforme solicitado.");

        assertThat(solicitacao.getStatus()).isEqualTo(StatusSolicitacao.CONCLUIDA);
        assertThat(solicitacao.getResposta()).contains("e-mail");
    }

    @Test
    @DisplayName("calcular prazo deve ser 15 dias corridos a partir de hoje")
    void calcularPrazo() {
        LocalDate prazo = lgpdService.calcularPrazo();
        assertThat(prazo).isEqualTo(LocalDate.now().plusDays(15));
    }

    @Test
    @DisplayName("concluir solicitação inexistente deve lançar EntityNotFoundException")
    void concluirInexistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> lgpdService.concluir(99L, "resposta"))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
