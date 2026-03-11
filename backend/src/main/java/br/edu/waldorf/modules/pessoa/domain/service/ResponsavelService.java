package br.edu.waldorf.modules.pessoa.domain.service;

import br.edu.waldorf.modules.pessoa.domain.model.Pessoa;
import br.edu.waldorf.modules.pessoa.domain.model.Responsavel;
import br.edu.waldorf.modules.pessoa.domain.model.ResponsavelAluno;
import br.edu.waldorf.modules.pessoa.domain.repository.ResponsavelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service de domínio para Responsável
 *
 * @author Sistema Waldorf
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ResponsavelService {

    private final ResponsavelRepository responsavelRepository;

    @Transactional(readOnly = true)
    public Responsavel buscarPorId(Long id) {
        return responsavelRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Responsável não encontrado: id=" + id));
    }

    @Transactional(readOnly = true)
    public List<Responsavel> listarPorAluno(Long alunoId) {
        return responsavelRepository.findByAlunoId(alunoId);
    }

    @Transactional(readOnly = true)
    public List<Responsavel> listarContatosEmergencia(Long alunoId) {
        return responsavelRepository.findContatosEmergenciaByAluno(alunoId);
    }

    @Transactional
    public Responsavel criar(Responsavel responsavel) {
        responsavel.setTipo(Pessoa.TipoPessoa.RESPONSAVEL);
        Responsavel salvo = responsavelRepository.save(responsavel);
        log.info("Responsável criado: id={}", salvo.getId());
        return salvo;
    }

    @Transactional
    public Responsavel atualizar(Long id, Responsavel dados) {
        Responsavel existente = buscarPorId(id);
        existente.setNomeCompleto(dados.getNomeCompleto());
        existente.setEmail(dados.getEmail());
        existente.setTelefonePrincipal(dados.getTelefonePrincipal());
        existente.setTelefoneSecundario(dados.getTelefoneSecundario());
        existente.setProfissao(dados.getProfissao());
        existente.setLocalTrabalho(dados.getLocalTrabalho());
        existente.setAutorizadoBuscar(dados.getAutorizadoBuscar());
        existente.setContatoEmergencia(dados.getContatoEmergencia());
        return responsavelRepository.save(existente);
    }
}
