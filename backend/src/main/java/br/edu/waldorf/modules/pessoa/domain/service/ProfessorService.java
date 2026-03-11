package br.edu.waldorf.modules.pessoa.domain.service;

import br.edu.waldorf.modules.pessoa.domain.model.Pessoa;
import br.edu.waldorf.modules.pessoa.domain.model.Professor;
import br.edu.waldorf.modules.pessoa.domain.repository.ProfessorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service de domínio para Professor
 *
 * @author Sistema Waldorf
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProfessorService {

    private final ProfessorRepository professorRepository;

    @Transactional(readOnly = true)
    public List<Professor> listarAtivos() {
        return professorRepository.findAllAtivos();
    }

    @Transactional(readOnly = true)
    public Page<Professor> listarComSituacao(Professor.SituacaoProfessor situacao, Pageable pageable) {
        return professorRepository.findBySituacao(situacao, pageable);
    }

    @Transactional(readOnly = true)
    public Professor buscarPorId(Long id) {
        return professorRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Professor não encontrado: id=" + id));
    }

    @Transactional
    public Professor criar(Professor professor) {
        if (professor.getRegistroProfissional() != null &&
                professorRepository.existsByRegistroProfissional(professor.getRegistroProfissional())) {
            throw new IllegalArgumentException("Registro profissional já cadastrado: " + professor.getRegistroProfissional());
        }
        professor.setTipo(Pessoa.TipoPessoa.PROFESSOR);
        Professor salvo = professorRepository.save(professor);
        log.info("Professor criado: id={}", salvo.getId());
        return salvo;
    }

    @Transactional
    public Professor atualizar(Long id, Professor dados) {
        Professor existente = buscarPorId(id);
        existente.setNomeCompleto(dados.getNomeCompleto());
        existente.setEmail(dados.getEmail());
        existente.setTelefonePrincipal(dados.getTelefonePrincipal());
        existente.setFormacao(dados.getFormacao());
        existente.setEspecializacaoWaldorf(dados.getEspecializacaoWaldorf());
        existente.setBiografia(dados.getBiografia());
        return professorRepository.save(existente);
    }

    @Transactional
    public void desligar(Long id) {
        Professor professor = buscarPorId(id);
        professor.setSituacao(Professor.SituacaoProfessor.DESLIGADO);
        professor.inativar();
        professorRepository.save(professor);
        log.info("Professor desligado: id={}", id);
    }
}
