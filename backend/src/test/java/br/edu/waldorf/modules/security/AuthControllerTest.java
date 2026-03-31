package br.edu.waldorf.modules.security;

import br.edu.waldorf.modules.security.api.dto.LoginRequestDTO;
import br.edu.waldorf.modules.security.api.dto.LoginResponseDTO;
import br.edu.waldorf.modules.security.api.dto.RefreshTokenRequestDTO;
import br.edu.waldorf.modules.security.application.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes unitários do AuthController.
 * Usa @WebMvcTest (sem subir contexto completo) com AuthService mockado.
 */
@WebMvcTest(br.edu.waldorf.modules.security.api.controller.AuthController.class)
@DisplayName("AuthController — testes unitários")
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    // ------------------------------------------------------------------
    // POST /api/v1/auth/login — credenciais válidas → 200 + tokens
    // ------------------------------------------------------------------
    @Test
    @DisplayName("Login com credenciais válidas deve retornar 200 e tokens JWT")
    void loginComCredenciaisValidasDeveRetornar200() throws Exception {
        LoginRequestDTO req = new LoginRequestDTO();
        req.setUsername("secretaria");
        req.setPassword("senha123");

        LoginResponseDTO resp = LoginResponseDTO.builder()
                .success(true)
                .accessToken("eyJ.access.token")
                .refreshToken("eyJ.refresh.token")
                .expiresIn(900L)
                .refreshExpiresIn(604800L)
                .build();

        when(authService.login(any(LoginRequestDTO.class))).thenReturn(resp);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.accessToken").value("eyJ.access.token"))
                .andExpect(jsonPath("$.refreshToken").value("eyJ.refresh.token"));
    }

    // ------------------------------------------------------------------
    // POST /api/v1/auth/login — credenciais inválidas → 401
    // ------------------------------------------------------------------
    @Test
    @DisplayName("Login com credenciais inválidas deve retornar 401")
    void loginComCredenciaisInvalidasDeveRetornar401() throws Exception {
        LoginRequestDTO req = new LoginRequestDTO();
        req.setUsername("usuario");
        req.setPassword("senhaErrada");

        when(authService.login(any(LoginRequestDTO.class)))
                .thenThrow(new BadCredentialsException("Credenciais inválidas"));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }

    // ------------------------------------------------------------------
    // POST /api/v1/auth/login — body vazio → 400
    // ------------------------------------------------------------------
    @Test
    @DisplayName("Login com body vazio deve retornar 400 Bad Request")
    void loginComBodyVazioDeveRetornar400() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    // ------------------------------------------------------------------
    // POST /api/v1/auth/refresh — refresh token válido → 200
    // ------------------------------------------------------------------
    @Test
    @DisplayName("Refresh com token válido deve retornar 200 e novos tokens")
    void refreshComTokenValidoDeveRetornar200() throws Exception {
        RefreshTokenRequestDTO req = new RefreshTokenRequestDTO();
        req.setRefreshToken("eyJ.valid.refresh");

        LoginResponseDTO resp = LoginResponseDTO.builder()
                .success(true)
                .accessToken("eyJ.new.access")
                .refreshToken("eyJ.new.refresh")
                .expiresIn(900L)
                .refreshExpiresIn(604800L)
                .build();

        when(authService.refreshToken("eyJ.valid.refresh")).thenReturn(resp);

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("eyJ.new.access"));
    }

    // ------------------------------------------------------------------
    // POST /api/v1/auth/refresh — refresh token inválido → 401
    // ------------------------------------------------------------------
    @Test
    @DisplayName("Refresh com token inválido deve retornar 401")
    void refreshComTokenInvalidoDeveRetornar401() throws Exception {
        RefreshTokenRequestDTO req = new RefreshTokenRequestDTO();
        req.setRefreshToken("token.invalido");

        when(authService.refreshToken("token.invalido"))
                .thenThrow(new BadCredentialsException("Refresh token inválido ou expirado"));

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }
}
