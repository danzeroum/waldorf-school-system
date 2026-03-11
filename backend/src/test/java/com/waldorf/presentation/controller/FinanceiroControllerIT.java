package com.waldorf.presentation.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("FinanceiroController — testes de integração")
class FinanceiroControllerIT {

    @Autowired MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "SECRETARIA")
    @DisplayName("GET /api/v1/finance/contracts deve retornar 200")
    void listarContratos() throws Exception {
        mockMvc.perform(get("/api/v1/finance/contracts"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "SECRETARIA")
    @DisplayName("GET /api/v1/finance/invoices deve retornar 200")
    void listarParcelas() throws Exception {
        mockMvc.perform(get("/api/v1/finance/invoices"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/v1/finance/contracts sem auth deve retornar 401")
    void listarSemAuth() throws Exception {
        mockMvc.perform(get("/api/v1/finance/contracts"))
                .andExpect(status().isUnauthorized());
    }
}
