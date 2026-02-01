package br.edu.waldorf.modules.pessoa.domain.service;

import br.edu.waldorf.modules.pessoa.domain.model.Endereco;
import br.edu.waldorf.modules.pessoa.domain.model.Pessoa;
import br.edu.waldorf.modules.pessoa.domain.repository.EnderecoRepository;
import br.edu.waldorf.modules.pessoa.domain.repository.PessoaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service para gerência de Pessoas
 * Contém lógica de negócio e validações
 * 
 * @author Daniel Lau
 * @version 1.0.0
 * @since 2026-01-31
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PessoaService {

    private final PessoaRepository pessoaRepository;
    private final EnderecoRepository enderecoRepository;

    // ===== OPERAÇÕES DE CONSULTA =====

    /**
     * Busca pessoa por ID
     * 
     * @param id ID da pessoa
     * @return Optional com a pessoa encontrada
     */
    public Optional<Pessoa> findById(Long id) {
        log.debug("Buscando pessoa por ID: {}", id);
        return pessoaRepository.findById(id);
    }

    /**
     * Busca pessoa por CPF
     * 
     * @param cpf CPF da pessoa
     * @return Optional com a pessoa encontrada
     */
    public Optional<Pessoa> findByCpf(String cpf) {
        log.debug("Buscando pessoa por CPF: {}", cpf);
        return pessoaRepository.findByCpf(cpf);
    }

    /**
     * Busca pessoa por email
     * 
     * @param email Email da pessoa
     * @return Optional com a pessoa encontrada
     */
    public Optional<Pessoa> findByEmail(String email) {
        log.debug("Buscando pessoa por email: {}", email);
        return pessoaRepository.findByEmail(email);
    }

    /**
     * Lista todas as pessoas com paginação
     * 
     * @param pageable Paginação
     * @return Página de pessoas
     */
    public Page<Pessoa> findAll(Pageable pageable) {
        log.debug("Listando todas as pessoas - Página: {}", pageable.getPageNumber());
        return pessoaRepository.findAll(pageable);
    }

    /**
     * Busca pessoas por tipo
     * 
     * @param tipo Tipo da pessoa
     * @param pageable Paginação
     * @return Página de pessoas
     */
    public Page<Pessoa> findByTipo(Pessoa.TipoPessoa tipo, Pageable pageable) {
        log.debug("Buscando pessoas por tipo: {}", tipo);
        return pessoaRepository.findByTipo(tipo, pageable);
    }

    /**
     * Busca pessoas ativas
     * 
     * @param pageable Paginação
     * @return Página de pessoas ativas
     */
    public Page<Pessoa> findAtivas(Pageable pageable) {
        log.debug("Buscando pessoas ativas");
        return pessoaRepository.findByAtivoTrue(pageable);
    }

    /**
     * Busca avançada por termo (nome, email ou CPF)
     * 
     * @param termo Termo de busca
     * @param pageable Paginação
     * @return Página de pessoas
     */
    public Page<Pessoa> buscaAvancada(String termo, Pageable pageable) {
        log.debug("Busca avançada por termo: {}", termo);
        return pessoaRepository.buscaAvancada(termo, pageable);
    }

    // ===== OPERAÇÕES DE ESCRITA =====

    /**
     * Cria uma nova pessoa
     * 
     * @param pessoa Pessoa a ser criada
     * @return Pessoa criada
     * @throws IllegalArgumentException se CPF ou email já existir
     */
    @Transactional
    public Pessoa create(Pessoa pessoa) {
        log.info("Criando nova pessoa: {}", pessoa.getNomeCompleto());

        // Validações
        validarPessoaNova(pessoa);

        // Salva a pessoa
        Pessoa pessoaSalva = pessoaRepository.save(pessoa);
        log.info("Pessoa criada com ID: {}", pessoaSalva.getId());

        return pessoaSalva;
    }

    /**
     * Atualiza uma pessoa existente
     * 
     * @param id ID da pessoa
     * @param pessoaAtualizada Dados atualizados
     * @return Pessoa atualizada
     * @throws IllegalArgumentException se pessoa não existir
     */
    @Transactional
    public Pessoa update(Long id, Pessoa pessoaAtualizada) {
        log.info("Atualizando pessoa ID: {}", id);

        Pessoa pessoaExistente = pessoaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Pessoa não encontrada com ID: " + id));

        // Valida CPF e email se foram alterados
        if (!pessoaExistente.getCpf().equals(pessoaAtualizada.getCpf())) {
            validarCpfUnico(pessoaAtualizada.getCpf());
        }
        if (!pessoaExistente.getEmail().equals(pessoaAtualizada.getEmail())) {
            validarEmailUnico(pessoaAtualizada.getEmail());
        }

        // Atualiza os campos
        pessoaExistente.setNomeCompleto(pessoaAtualizada.getNomeCompleto());
        pessoaExistente.setCpf(pessoaAtualizada.getCpf());
        pessoaExistente.setRg(pessoaAtualizada.getRg());
        pessoaExistente.setDataNascimento(pessoaAtualizada.getDataNascimento());
        pessoaExistente.setEmail(pessoaAtualizada.getEmail());
        pessoaExistente.setTelefonePrincipal(pessoaAtualizada.getTelefonePrincipal());
        pessoaExistente.setTelefoneSecundario(pessoaAtualizada.getTelefoneSecundario());
        pessoaExistente.setFotoUrl(pessoaAtualizada.getFotoUrl());

        Pessoa pessoaSalva = pessoaRepository.save(pessoaExistente);
        log.info("Pessoa atualizada: {}", pessoaSalva.getId());

        return pessoaSalva;
    }

    /**
     * Inativa uma pessoa (soft delete)
     * 
     * @param id ID da pessoa
     */
    @Transactional
    public void inativar(Long id) {
        log.info("Inativando pessoa ID: {}", id);

        Pessoa pessoa = pessoaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Pessoa não encontrada com ID: " + id));

        pessoa.inativar();
        pessoaRepository.save(pessoa);

        log.info("Pessoa inativada: {}", id);
    }

    /**
     * Reativa uma pessoa
     * 
     * @param id ID da pessoa
     */
    @Transactional
    public void reativar(Long id) {
        log.info("Reativando pessoa ID: {}", id);

        Pessoa pessoa = pessoaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Pessoa não encontrada com ID: " + id));

        pessoa.reativar();
        pessoaRepository.save(pessoa);

        log.info("Pessoa reativada: {}", id);
    }

    /**
     * Deleta uma pessoa permanentemente
     * 
     * @param id ID da pessoa
     */
    @Transactional
    public void delete(Long id) {
        log.warn("Deletando permanentemente pessoa ID: {}", id);

        if (!pessoaRepository.existsById(id)) {
            throw new IllegalArgumentException("Pessoa não encontrada com ID: " + id);
        }

        pessoaRepository.deleteById(id);
        log.warn("Pessoa deletada permanentemente: {}", id);
    }

    // ===== OPERAÇÕES LGPD =====

    /**
     * Registra consentimento LGPD
     * 
     * @param id ID da pessoa
     */
    @Transactional
    public void registrarConsentimentoLGPD(Long id) {
        log.info("Registrando consentimento LGPD para pessoa ID: {}", id);

        Pessoa pessoa = pessoaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Pessoa não encontrada com ID: " + id));

        pessoa.registrarConsentimentoLGPD();
        pessoaRepository.save(pessoa);

        log.info("Consentimento LGPD registrado para pessoa: {}", id);
    }

    /**
     * Revoga consentimento LGPD
     * 
     * @param id ID da pessoa
     */
    @Transactional
    public void revogarConsentimentoLGPD(Long id) {
        log.info("Revogando consentimento LGPD para pessoa ID: {}", id);

        Pessoa pessoa = pessoaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Pessoa não encontrada com ID: " + id));

        pessoa.revogarConsentimentoLGPD();
        pessoaRepository.save(pessoa);

        log.info("Consentimento LGPD revogado para pessoa: {}", id);
    }

    /**
     * Busca pessoas com exclusão prevista para hoje ou anterior
     * 
     * @return Lista de pessoas
     */
    public List<Pessoa> findPessoasComExclusaoPendente() {
        log.debug("Buscando pessoas com exclusão pendente");
        return pessoaRepository.findPessoasComExclusaoPrevista(LocalDate.now());
    }

    // ===== OPERAÇÕES DE ENDEREÇO =====

    /**
     * Adiciona endereço a uma pessoa
     * 
     * @param pessoaId ID da pessoa
     * @param endereco Endereço a adicionar
     * @return Endereço criado
     */
    @Transactional
    public Endereco adicionarEndereco(Long pessoaId, Endereco endereco) {
        log.info("Adicionando endereço à pessoa ID: {}", pessoaId);

        Pessoa pessoa = pessoaRepository.findById(pessoaId)
            .orElseThrow(() -> new IllegalArgumentException("Pessoa não encontrada com ID: " + pessoaId));

        // Se for o primeiro endereço ou marcado como principal, garante que seja o único principal
        if (endereco.getPrincipal()) {
            desmarcarEnderecoPrincipal(pessoaId);
        }

        pessoa.adicionarEndereco(endereco);
        pessoaRepository.save(pessoa);

        log.info("Endereço adicionado à pessoa: {}", pessoaId);
        return endereco;
    }

    /**
     * Lista endereços de uma pessoa
     * 
     * @param pessoaId ID da pessoa
     * @return Lista de endereços
     */
    public List<Endereco> listarEnderecos(Long pessoaId) {
        log.debug("Listando endereços da pessoa ID: {}", pessoaId);
        return enderecoRepository.findByPessoaId(pessoaId);
    }

    // ===== MÉTODOS PRIVADOS DE VALIDAÇÃO =====

    private void validarPessoaNova(Pessoa pessoa) {
        validarCpfUnico(pessoa.getCpf());
        validarEmailUnico(pessoa.getEmail());
    }

    private void validarCpfUnico(String cpf) {
        if (cpf != null && pessoaRepository.existsByCpf(cpf)) {
            throw new IllegalArgumentException("Já existe uma pessoa cadastrada com o CPF: " + cpf);
        }
    }

    private void validarEmailUnico(String email) {
        if (pessoaRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Já existe uma pessoa cadastrada com o email: " + email);
        }
    }

    private void desmarcarEnderecoPrincipal(Long pessoaId) {
        List<Endereco> enderecos = enderecoRepository.findByPessoaId(pessoaId);
        enderecos.forEach(end -> {
            end.tornarSecundario();
            enderecoRepository.save(end);
        });
    }

    // ===== ESTATÍSTICAS =====

    /**
     * Conta pessoas por tipo
     * 
     * @param tipo Tipo da pessoa
     * @return Quantidade
     */
    public long contarPorTipo(Pessoa.TipoPessoa tipo) {
        return pessoaRepository.countByTipo(tipo);
    }

    /**
     * Conta pessoas ativas por tipo
     * 
     * @param tipo Tipo da pessoa
     * @return Quantidade
     */
    public long contarAtivasPorTipo(Pessoa.TipoPessoa tipo) {
        return pessoaRepository.countByTipoAndAtivo(tipo, true);
    }
}
