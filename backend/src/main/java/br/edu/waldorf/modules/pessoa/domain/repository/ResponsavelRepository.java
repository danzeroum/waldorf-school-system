package br.edu.waldorf.modules.pessoa.domain.repository;

import br.edu.waldorf.modules.pessoa.domain.model.Responsavel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository de Responsável
 *
 * @author Sistema Waldorf
 * @version 1.0.0
 */
@Repository
public interface ResponsavelRepository extends JpaRepository<Responsavel, Long> {

    @Query("SELECT r FROM Responsavel r JOIN r.alunos ra WHERE ra.aluno.id = :alunoId ORDER BY ra.prioridadeContato")
    List<Responsavel> findByAlunoId(@Param("alunoId") Long alunoId);

    @Query("SELECT r FROM Responsavel r JOIN Pessoa p ON p.id = r.id " +
           "WHERE LOWER(p.nomeCompleto) LIKE LOWER(CONCAT('%', :nome, '%'))")
    Page<Responsavel> findByNomeContaining(@Param("nome") String nome, Pageable pageable);

    @Query("SELECT r FROM Responsavel r JOIN r.alunos ra WHERE ra.contatoEmergencia = true AND ra.aluno.id = :alunoId")
    List<Responsavel> findContatosEmergenciaByAluno(@Param("alunoId") Long alunoId);
}
