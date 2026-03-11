package com.waldorf.application.service;

import com.waldorf.application.dto.auth.LoginRequestDTO;
import com.waldorf.domain.entity.Perfil;
import com.waldorf.domain.entity.Usuario;
import com.waldorf.infrastructure.repository.UsuarioRepository;
import com.waldorf.infrastructure.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService — testes unitários")
class AuthServiceTest {

    @Mock AuthenticationManager authManager;
    @Mock UsuarioRepository     usuarioRepository;
    @Mock JwtService            jwtService;

    @InjectMocks AuthService authService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Admin Waldorf");
        usuario.setEmail("admin@waldorf.edu.br");
        usuario.setAtivo(true);
        Perfil perfil = new Perfil();
        perfil.setNome("ADMIN");
        usuario.setPerfis(Set.of(perfil));
    }

    @Test
    @DisplayName("login com credenciais válidas deve retornar tokens")
    void loginValido() {
        Authentication auth = mock(Authentication.class);
        when(authManager.authenticate(any())).thenReturn(auth);
        when(usuarioRepository.findByEmail("admin@waldorf.edu.br")).thenReturn(Optional.of(usuario));
        when(jwtService.gerarToken(usuario)).thenReturn("access.token.jwt");
        when(jwtService.gerarRefreshToken(usuario)).thenReturn("refresh.token.jwt");

        var dto = new LoginRequestDTO("admin@waldorf.edu.br", "senha123");
        var resp = authService.login(dto);

        assertThat(resp.accessToken()).isEqualTo("access.token.jwt");
        assertThat(resp.refreshToken()).isEqualTo("refresh.token.jwt");
        assertThat(resp.usuario().email()).isEqualTo("admin@waldorf.edu.br");
        verify(jwtService).gerarToken(usuario);
    }

    @Test
    @DisplayName("login com credenciais inválidas deve lançar BadCredentialsException")
    void loginInvalido() {
        when(authManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Credenciais inválidas"));

        assertThatThrownBy(() -> authService.login(new LoginRequestDTO("x@x.com", "errada")))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    @DisplayName("login com usuário inativo deve lançar exceção")
    void loginUsuarioInativo() {
        usuario.setAtivo(false);
        Authentication auth = mock(Authentication.class);
        when(authManager.authenticate(any())).thenReturn(auth);
        when(usuarioRepository.findByEmail(any())).thenReturn(Optional.of(usuario));

        assertThatThrownBy(() -> authService.login(new LoginRequestDTO("admin@waldorf.edu.br", "senha")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("inativo");
    }
}
