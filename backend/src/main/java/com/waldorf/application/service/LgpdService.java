package com.waldorf.application.service;

import com.waldorf.domain.entity.SolicitacaoTitular;
import com.waldorf.domain.enums.StatusSolicitacao;
import com.waldorf.infrastructure.repository.SolicitacaoTitularRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class LgpdService {

    private final SolicitacaoTitularRepository repository;

    @Transactional
    public void avancarStatus(Long id) {
        SolicitacaoTitular s = findOrThrow(id);
        s.setStatus(StatusSolicitacao.EM_ANALISE);
        repository.save(s);
    }

    @Transactional
    public void concluir(Long id, String resposta) {
        SolicitacaoTitular s = findOrThrow(id);
        s.setStatus(StatusSolicitacao.CONCLUIDA);
        s.setResposta(resposta);
        repository.save(s);
    }

    @Transactional
    public void rejeitar(Long id, String motivo) {
        SolicitacaoTitular s = findOrThrow(id);
        s.setStatus(StatusSolicitacao.REJEITADA);
        s.setResposta(motivo);
        repository.save(s);
    }

    public LocalDate calcularPrazo() {
        return LocalDate.now().plusDays(15);
    }

    private SolicitacaoTitular findOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Solicitação não encontrada: " + id));
    }
}
