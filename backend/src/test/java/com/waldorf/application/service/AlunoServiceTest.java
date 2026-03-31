package com.waldorf.application.service;

import com.waldorf.application.dto.aluno.AlunoRequestDTO;
import com.waldorf.application.dto.aluno.AlunoResponseDTO;
import com.waldorf.domain.entity.Aluno;
import com.waldorf.domain.entity.Turma;
import com.waldorf.domain.enums.Genero;
import com.waldorf.infrastructure.repository.AlunoRepository;
import com.waldorf.infrastructure.repository.ResponsavelRepository;
import com.waldorf.infrastructure.repository.TurmaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AlunoService — testes unitários")
class AlunoServiceTest {

    @Mock AlunoRepository       alunoRepository;
    @Mock TurmaRepository       turmaRepository;
    @Mock ResponsavelRepository responsavelRepository;

    @InjectMocks AlunoService alunoService;

    private Aluno aluno;

    @BeforeEach
    void setUp() {
        aluno = new Aluno();
        aluno.setId(1L);
        aluno.setNome("Pedro Santos");
        aluno.setMatricula("202600001");
        aluno.setDataNascimento(LocalDate.of(2015, 5, 10));
        aluno.setGenero(Genero.MASCULINO);
        aluno.setAnoIngresso(2026);
        aluno.setAtivo(true);
        aluno.setCreatedAt(LocalDateTime.now());
        aluno.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("buscarPorId deve retornar DTO quando aluno existe")
    void buscarPorIdExistente() {
        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        AlunoResponseDTO dto = alunoService.buscarPorId(1L);
        assertThat(dto.nome()).isEqualTo("Pedro Santos");
        assertThat(dto.ativo()).isTrue();
    }

    @Test
    @DisplayName("buscarPorId deve lançar EntityNotFoundException quando não existe")
    void buscarPorIdInexistente() {
        when(alunoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> alunoService.buscarPorId(99L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("criar deve gerar matrícula e salvar aluno")
    void criarAluno() {
        var dto = new AlunoRequestDTO(
                "Ana Clara", LocalDate.of(2016, 3, 20), Genero.FEMININO,
                "ana@mail.com", null, null, 2026, null,
                null, null, null, null, null, null, null);

        // 1º saveAndFlush: simula INSERT e retorna aluno com ID gerado
        // 2º saveAndFlush: salva matrícula definitiva
        when(alunoRepository.saveAndFlush(any(Aluno.class))).thenAnswer(inv -> {
            Aluno a = inv.getArgument(0);
            if (a.getId() == null) {
                a.setId(2L);
                a.setCreatedAt(LocalDateTime.now());
                a.setUpdatedAt(LocalDateTime.now());
            }
            return a;
        });

        AlunoResponseDTO resp = alunoService.criar(dto);
        assertThat(resp.nome()).isEqualTo("Ana Clara");
        assertThat(resp.matricula()).startsWith("2026");
        // service chama saveAndFlush 2 vezes
        verify(alunoRepository, times(2)).saveAndFlush(any(Aluno.class));
    }

    @Test
    @DisplayName("inativar deve setar ativo=false")
    void inativarAluno() {
        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(alunoRepository.save(any())).thenReturn(aluno);

        alunoService.inativar(1L);

        assertThat(aluno.isAtivo()).isFalse();
        verify(alunoRepository).save(aluno);
    }

    @Test
    @DisplayName("listar deve aplicar filtros e retornar página")
    void listarComFiltros() {
        var page = new PageImpl<>(List.of(aluno));
        when(alunoRepository.findWithFilters(
                eq("Pedro"), isNull(), eq(true), any(Pageable.class)))
                .thenReturn(page);

        var resultado = alunoService.listar("Pedro", null, true, Pageable.unpaged());
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).nome()).isEqualTo("Pedro Santos");
    }
}
