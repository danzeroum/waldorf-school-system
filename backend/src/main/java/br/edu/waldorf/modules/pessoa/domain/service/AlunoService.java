package br.edu.waldorf.modules.pessoa.domain.service;

import br.edu.waldorf.modules.pessoa.domain.model.Aluno;
import br.edu.waldorf.modules.pessoa.domain.model.Pessoa;
import br.edu.waldorf.modules.pessoa.domain.repository.AlunoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service de domínio para Aluno
 * Contém as regras de negócio relacionadas a alunos
 *
 * @author Sistema Waldorf
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AlunoService {

    private final AlunoRepository alunoRepository;

    @Transactional(readOnly = true)
    public Page<Aluno> listarComFiltros(Aluno.SituacaoAluno situacao, Long turmaId, String nome, Pageable pageable) {
        log.debug("Listando alunos - situacao={}, turmaId={}, nome={}", situacao, turmaId, nome);
        return alunoRepository.findWithFilters(situacao, turmaId, nome, pageable);
    }

    @Transactional(readOnly = true)
    public Aluno buscarPorId(Long id) {
        return alunoRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Aluno não encontrado: id=" + id));
    }

    @Transactional(readOnly = true)
    public Aluno buscarPorMatricula(String numeroMatricula) {
        return alunoRepository.findByNumeroMatricula(numeroMatricula)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Aluno não encontrado: matricula=" + numeroMatricula));
    }

    @Transactional(readOnly = true)
    public List<Aluno> listarPorTurma(Long turmaId) {
        return alunoRepository.findAtivosByTurma(turmaId);
    }

    @Transactional
    public Aluno criar(Aluno aluno) {
        validarMatriculaUnica(aluno.getNumeroMatricula(), null);
        aluno.setTipo(Pessoa.TipoPessoa.ALUNO);
        if (!aluno.getLgpdConsentimentoGeral()) {
            throw new IllegalStateException("Consentimento LGPD é obrigatório para cadastro de aluno");
        }
        Aluno salvo = alunoRepository.save(aluno);
        log.info("Aluno criado: id={}, matricula={}", salvo.getId(), salvo.getNumeroMatricula());
        return salvo;
    }

    @Transactional
    public Aluno atualizar(Long id, Aluno dados) {
        Aluno existente = buscarPorId(id);
        if (dados.getNumeroMatricula() != null &&
                !dados.getNumeroMatricula().equals(existente.getNumeroMatricula())) {
            validarMatriculaUnica(dados.getNumeroMatricula(), id);
        }
        // Atualizar campos permitidos
        existente.setNomeCompleto(dados.getNomeCompleto());
        existente.setNomeSocial(dados.getNomeSocial());
        existente.setEmail(dados.getEmail());
        existente.setTelefonePrincipal(dados.getTelefonePrincipal());
        existente.setFotoUrl(dados.getFotoUrl());
        existente.setAlergias(dados.getAlergias());
        existente.setMedicamentosControlados(dados.getMedicamentosControlados());
        existente.setNecessidadesEspeciais(dados.getNecessidadesEspeciais());
        existente.setObservacoesMedicas(dados.getObservacoesMedicas());
        existente.setTemperamento(dados.getTemperamento());
        return alunoRepository.save(existente);
    }

    @Transactional
    public void desligar(Long id) {
        Aluno aluno = buscarPorId(id);
        aluno.desligar();
        alunoRepository.save(aluno);
        log.info("Aluno desligado: id={}", id);
    }

    @Transactional(readOnly = true)
    public long contarAtivos() {
        return alunoRepository.countAtivos();
    }

    private void validarMatriculaUnica(String numeroMatricula, Long idIgnorar) {
        if (numeroMatricula != null && alunoRepository.existsByNumeroMatricula(numeroMatricula)) {
            Aluno existente = alunoRepository.findByNumeroMatricula(numeroMatricula).orElse(null);
            if (existente != null && !existente.getId().equals(idIgnorar)) {
                throw new IllegalArgumentException("Número de matrícula já existe: " + numeroMatricula);
            }
        }
    }
}
