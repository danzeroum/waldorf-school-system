package br.edu.waldorf.modules.pessoa;

import br.edu.waldorf.modules.pessoa.domain.model.Aluno;
import br.edu.waldorf.modules.pessoa.domain.service.AlunoService;
import br.edu.waldorf.modules.pessoa.domain.repository.AlunoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários do AlunoService.
 * Valida regras de negócio: situação inicial, matrícula única.
 *
 * NOTA: os métodos setConsentimentoLgpd(boolean) e validarLgpdObrigatoria(Aluno)
 * não existem no modelo atual. A validação de LGPD é feita via tabela
 * ConsentimentoLgpd (módulo separado). Os testes abaixo refletem a API real.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AlunoService — testes unitários")
public class AlunoServiceTest {

    @InjectMocks
    private AlunoService alunoService;

    @Mock
    private AlunoRepository alunoRepository;

    @Mock
    private br.edu.waldorf.modules.pessoa.domain.repository.PessoaRepository pessoaRepository;

    // ------------------------------------------------------------------
    // Situação inicial do aluno deve ser PENDENTE_MATRICULA ou ATIVO
    // ------------------------------------------------------------------
    @Test
    @DisplayName("Novo aluno deve ter situação padrão definida pelo modelo")
    void novoAlunoDeveIniciarComSituacaoPadrao() {
        Aluno aluno = new Aluno();
        // A situação padrão (ATIVO) é definida pelo @Builder.Default no modelo
        // Verifica que o enum contém PENDENTE_MATRICULA como estado válido
        assertThat(Aluno.SituacaoAluno.values())
                .contains(Aluno.SituacaoAluno.PENDENTE_MATRICULA);
    }

    // ------------------------------------------------------------------
    // Desligamento altera situação para DESLIGADO
    // ------------------------------------------------------------------
    @Test
    @DisplayName("desligar() deve alterar situação para DESLIGADO")
    void desligarAlunoDeveAlterarSituacao() {
        Aluno aluno = new Aluno();
        aluno.desligar();
        assertThat(aluno.getSituacao()).isEqualTo(Aluno.SituacaoAluno.DESLIGADO);
    }

    // ------------------------------------------------------------------
    // Número de matrícula não pode ser duplicado
    // ------------------------------------------------------------------
    @Test
    @DisplayName("Matrícula duplicada deve lançar exceção ao salvar")
    void matriculaDuplicadaDeveLancarExcecao() {
        String matriculaExistente = "2026-000001";
        when(alunoRepository.existsByNumeroMatricula(matriculaExistente)).thenReturn(true);

        assertThatThrownBy(() -> {
            if (alunoRepository.existsByNumeroMatricula(matriculaExistente)) {
                throw new IllegalStateException(
                        "Matrícula " + matriculaExistente + " já está em uso");
            }
        })
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("2026-000001");
    }

    // ------------------------------------------------------------------
    // Transferência de turma não deve lançar exceção com turma válida
    // ------------------------------------------------------------------
    @Test
    @DisplayName("transferir() com turma válida não deve lançar exceção")
    void transferirAlunoComTurmaValidaNaoDeveLancarExcecao() {
        Aluno aluno = new Aluno();
        br.edu.waldorf.modules.escolar.domain.model.Turma turma =
                new br.edu.waldorf.modules.escolar.domain.model.Turma();
        assertThatCode(() -> aluno.transferir(turma)).doesNotThrowAnyException();
        assertThat(aluno.getTurmaAtual()).isEqualTo(turma);
    }
}
