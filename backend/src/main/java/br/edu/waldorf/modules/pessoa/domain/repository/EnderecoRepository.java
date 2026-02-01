package br.edu.waldorf.modules.pessoa.domain.repository;

import br.edu.waldorf.modules.pessoa.domain.model.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para a entidade Endereco
 * Fornece operações CRUD e queries customizadas
 * 
 * @author Daniel Lau
 * @version 1.0.0
 * @since 2026-01-31
 */
@Repository
public interface EnderecoRepository extends JpaRepository<Endereco, Long> {

    /**
     * Busca todos os endereços de uma pessoa
     * 
     * @param pessoaId ID da pessoa
     * @return Lista de endereços
     */
    List<Endereco> findByPessoaId(Long pessoaId);

    /**
     * Busca o endereço principal de uma pessoa
     * 
     * @param pessoaId ID da pessoa
     * @return Optional com o endereço principal
     */
    Optional<Endereco> findByPessoaIdAndPrincipalTrue(Long pessoaId);

    /**
     * Busca endereços por tipo
     * 
     * @param pessoaId ID da pessoa
     * @param tipo Tipo do endereço
     * @return Lista de endereços
     */
    List<Endereco> findByPessoaIdAndTipo(Long pessoaId, Endereco.TipoEndereco tipo);

    /**
     * Busca endereços por cidade
     * 
     * @param cidade Nome da cidade
     * @return Lista de endereços
     */
    List<Endereco> findByCidade(String cidade);

    /**
     * Busca endereços por cidade e estado
     * 
     * @param cidade Nome da cidade
     * @param estado Sigla do estado (UF)
     * @return Lista de endereços
     */
    List<Endereco> findByCidadeAndEstado(String cidade, String estado);

    /**
     * Busca endereços por CEP
     * 
     * @param cep CEP do endereço
     * @return Lista de endereços
     */
    List<Endereco> findByCep(String cep);

    /**
     * Conta endereços de uma pessoa
     * 
     * @param pessoaId ID da pessoa
     * @return Quantidade de endereços
     */
    long countByPessoaId(Long pessoaId);

    /**
     * Verifica se existe endereço principal para uma pessoa
     * 
     * @param pessoaId ID da pessoa
     * @return true se existe, false caso contrário
     */
    boolean existsByPessoaIdAndPrincipalTrue(Long pessoaId);

    /**
     * Busca endereços em uma região (cidade ou estado)
     * 
     * @param termo Termo de busca (cidade ou estado)
     * @return Lista de endereços
     */
    @Query("""
        SELECT e FROM Endereco e 
        WHERE LOWER(e.cidade) LIKE LOWER(CONCAT('%', :termo, '%'))
           OR LOWER(e.estado) LIKE LOWER(CONCAT('%', :termo, '%'))
        """)
    List<Endereco> buscarPorRegiao(@Param("termo") String termo);

    /**
     * Busca todos os endereços não principais de uma pessoa
     * 
     * @param pessoaId ID da pessoa
     * @return Lista de endereços secundários
     */
    List<Endereco> findByPessoaIdAndPrincipalFalse(Long pessoaId);
}
