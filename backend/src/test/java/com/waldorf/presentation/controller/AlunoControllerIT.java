package com.waldorf.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.waldorf.application.dto.aluno.AlunoRequestDTO;
import com.waldorf.domain.enums.Genero;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("AlunoController — testes de integração")
class AlunoControllerIT {

    @Autowired MockMvc      mockMvc;
    @Autowired ObjectMapper mapper;

    @Test
    @DisplayName("GET /api/v1/alunos sem autenticação deve retornar 401")
    void listarSemAuth() throws Exception {
        mockMvc.perform(get("/api/v1/alunos"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "SECRETARIA")
    @DisplayName("GET /api/v1/alunos autenticado deve retornar 200")
    void listarAutenticado() throws Exception {
        mockMvc.perform(get("/api/v1/alunos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(roles = "SECRETARIA")
    @DisplayName("POST /api/v1/alunos com dados válidos deve retornar 201")
    void criarAlunoValido() throws Exception {
        var dto = new AlunoRequestDTO(
                "Maria Oliveira", LocalDate.of(2015, 8, 15), Genero.FEMININO,
                "maria@mail.com", null, null, 2026, null,
                null, null, null, null, null, null, null);

        mockMvc.perform(post("/api/v1/alunos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Maria Oliveira"))
                .andExpect(jsonPath("$.matricula").exists());
    }

    @Test
    @WithMockUser(roles = "SECRETARIA")
    @DisplayName("POST /api/v1/alunos com nome vazio deve retornar 400")
    void criarAlunoInvalido() throws Exception {
        var dto = new AlunoRequestDTO(
                "", LocalDate.of(2015, 8, 15), Genero.FEMININO,
                null, null, null, 2026, null,
                null, null, null, null, null, null, null);

        mockMvc.perform(post("/api/v1/alunos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "PAIS")
    @DisplayName("POST /api/v1/alunos com perfil PAIS deve retornar 403")
    void criarAlunoSemPermissao() throws Exception {
        var dto = new AlunoRequestDTO(
                "Aluno Teste", LocalDate.of(2015, 8, 15), Genero.MASCULINO,
                null, null, null, 2026, null,
                null, null, null, null, null, null, null);

        mockMvc.perform(post("/api/v1/alunos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }
}
