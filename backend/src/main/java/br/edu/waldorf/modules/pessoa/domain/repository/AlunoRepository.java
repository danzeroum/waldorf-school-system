package br.edu.waldorf.modules.pessoa.domain.repository;

import br.edu.waldorf.modules.pessoa.domain.model.Aluno;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository de Aluno
 *
 * @author Sistema Waldorf
 * @version 1.0.0
 */
@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long> {

    Optional<Aluno> findByNumeroMatricula(String numeroMatricula);

    boolean existsByNumeroMatricula(String numeroMatricula);

    Page<Aluno> findBySituacao(Aluno.SituacaoAluno situacao, Pageable pageable);

    @Query("SELECT a FROM Aluno a JOIN a.turmaAtual t WHERE t.id = :turmaId AND a.situacao = 'ATIVO'")
    List<Aluno> findAtivosByTurma(@Param("turmaId") Long turmaId);

    @Query("SELECT a FROM Aluno a JOIN Pessoa p ON p.id = a.id " +
           "WHERE LOWER(p.nomeCompleto) LIKE LOWER(CONCAT('%', :nome, '%'))")
    Page<Aluno> findByNomeContaining(@Param("nome") String nome, Pageable pageable);

    @Query("SELECT a FROM Aluno a JOIN Pessoa p ON p.id = a.id " +
           "WHERE (:situacao IS NULL OR a.situacao = :situacao) " +
           "AND (:turmaId IS NULL OR a.turmaAtual.id = :turmaId) " +
           "AND (:nome IS NULL OR LOWER(p.nomeCompleto) LIKE LOWER(CONCAT('%', :nome, '%')))")
    Page<Aluno> findWithFilters(
            @Param("situacao") Aluno.SituacaoAluno situacao,
            @Param("turmaId") Long turmaId,
            @Param("nome") String nome,
            Pageable pageable
    );

    @Query("SELECT COUNT(a) FROM Aluno a WHERE a.situacao = 'ATIVO'")
    long countAtivos();
}
