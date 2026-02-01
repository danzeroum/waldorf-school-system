package br.edu.waldorf.modules.pessoa.domain.repository;

import br.edu.waldorf.modules.pessoa.domain.model.Pessoa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository para a entidade Pessoa
 * Fornece operações CRUD e queries customizadas
 * 
 * @author Daniel Lau
 * @version 1.0.0
 * @since 2026-01-31
 */
@Repository
public interface PessoaRepository extends JpaRepository<Pessoa, Long> {

    /**
     * Busca pessoa por CPF
     * 
     * @param cpf CPF da pessoa
     * @return Optional com a pessoa encontrada
     */
    Optional<Pessoa> findByCpf(String cpf);

    /**
     * Busca pessoa por email
     * 
     * @param email Email da pessoa
     * @return Optional com a pessoa encontrada
     */
    Optional<Pessoa> findByEmail(String email);

    /**
     * Verifica se existe pessoa com o CPF informado
     * 
     * @param cpf CPF a verificar
     * @return true se existe, false caso contrário
     */
    boolean existsByCpf(String cpf);

    /**
     * Verifica se existe pessoa com o email informado
     * 
     * @param email Email a verificar
     * @return true se existe, false caso contrário
     */
    boolean existsByEmail(String email);

    /**
     * Busca pessoas por tipo
     * 
     * @param tipo Tipo da pessoa
     * @param pageable Paginação
     * @return Página de pessoas
     */
    Page<Pessoa> findByTipo(Pessoa.TipoPessoa tipo, Pageable pageable);

    /**
     * Busca pessoas ativas por tipo
     * 
     * @param tipo Tipo da pessoa
     * @param ativo Status ativo
     * @param pageable Paginação
     * @return Página de pessoas
     */
    Page<Pessoa> findByTipoAndAtivo(Pessoa.TipoPessoa tipo, Boolean ativo, Pageable pageable);

    /**
     * Busca pessoas por nome (case insensitive, contém)
     * 
     * @param nome Nome ou parte do nome
     * @param pageable Paginação
     * @return Página de pessoas
     */
    @Query("SELECT p FROM Pessoa p WHERE LOWER(p.nomeCompleto) LIKE LOWER(CONCAT('%', :nome, '%'))")
    Page<Pessoa> findByNomeContainingIgnoreCase(@Param("nome") String nome, Pageable pageable);

    /**
     * Busca pessoas ativas
     * 
     * @param pageable Paginação
     * @return Página de pessoas ativas
     */
    Page<Pessoa> findByAtivoTrue(Pageable pageable);

    /**
     * Busca pessoas inativas
     * 
     * @param pageable Paginação
     * @return Página de pessoas inativas
     */
    Page<Pessoa> findByAtivoFalse(Pageable pageable);

    /**
     * Busca pessoas com consentimento LGPD
     * 
     * @param lgpdConsentimento Status do consentimento
     * @return Lista de pessoas
     */
    List<Pessoa> findByLgpdConsentimentoGeral(Boolean lgpdConsentimento);

    /**
     * Busca pessoas com data de exclusão prevista antes da data informada
     * 
     * @param data Data limite
     * @return Lista de pessoas
     */
    @Query("SELECT p FROM Pessoa p WHERE p.dataExclusaoPrevista <= :data AND p.ativo = true")
    List<Pessoa> findPessoasComExclusaoPrevista(@Param("data") LocalDate data);

    /**
     * Busca pessoas por tipo e consentimento LGPD
     * 
     * @param tipo Tipo da pessoa
     * @param lgpdConsentimento Status do consentimento
     * @return Lista de pessoas
     */
    List<Pessoa> findByTipoAndLgpdConsentimentoGeral(
        Pessoa.TipoPessoa tipo, 
        Boolean lgpdConsentimento
    );

    /**
     * Conta pessoas por tipo
     * 
     * @param tipo Tipo da pessoa
     * @return Quantidade de pessoas
     */
    long countByTipo(Pessoa.TipoPessoa tipo);

    /**
     * Conta pessoas ativas por tipo
     * 
     * @param tipo Tipo da pessoa
     * @param ativo Status ativo
     * @return Quantidade de pessoas
     */
    long countByTipoAndAtivo(Pessoa.TipoPessoa tipo, Boolean ativo);

    /**
     * Busca pessoas com busca avançada (nome, email ou CPF)
     * 
     * @param termo Termo de busca
     * @param pageable Paginação
     * @return Página de pessoas
     */
    @Query("""
        SELECT p FROM Pessoa p 
        WHERE LOWER(p.nomeCompleto) LIKE LOWER(CONCAT('%', :termo, '%'))
           OR LOWER(p.email) LIKE LOWER(CONCAT('%', :termo, '%'))
           OR p.cpf LIKE CONCAT('%', :termo, '%')
        """)
    Page<Pessoa> buscaAvancada(@Param("termo") String termo, Pageable pageable);
}
