package com.waldorf.application.service;

import com.waldorf.application.dto.auth.LoginRequestDTO;
import com.waldorf.application.dto.auth.LoginResponseDTO;
import com.waldorf.application.dto.auth.UsuarioResponseDTO;
import com.waldorf.domain.entity.Usuario;
import com.waldorf.infrastructure.repository.UsuarioRepository;
import com.waldorf.infrastructure.security.JwtService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authManager;
    private final UsuarioRepository     usuarioRepository;
    private final JwtService            jwtService;

    public LoginResponseDTO login(LoginRequestDTO dto) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.email(), dto.password()));
        var usuario = usuarioRepository.findByEmail(dto.email())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
        if (!usuario.isAtivo()) throw new IllegalStateException("Usuário inativo");
        return buildResponse(usuario);
    }

    public LoginResponseDTO refresh(String refreshToken) {
        String email = jwtService.extractUsername(refreshToken);
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
        if (!jwtService.isTokenValid(refreshToken, usuario)) {
            throw new IllegalArgumentException("Refresh token inválido ou expirado");
        }
        return buildResponse(usuario);
    }

    /**
     * Invalida o token do usuário autenticado.
     * Se o JwtService suportar blacklist, o token é adicionado a ela.
     * Caso contrário, o logout é stateless (cliente descarta o token).
     */
    public void logout(String token, String email) {
        try {
            if (jwtService.isTokenValid(token,
                    usuarioRepository.findByEmail(email)
                            .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado")))) {
                jwtService.invalidarToken(token);
            }
        } catch (Exception ex) {
            log.warn("Logout: não foi possível invalidar token para {}: {}", email, ex.getMessage());
        }
    }

    private LoginResponseDTO buildResponse(Usuario usuario) {
        String accessToken  = jwtService.gerarToken(usuario);
        String refreshToken = jwtService.gerarRefreshToken(usuario);
        var dto = new UsuarioResponseDTO(
                usuario.getId(), usuario.getNome(), usuario.getEmail(),
                usuario.getPerfis().stream()
                        .map(p -> p.getNome())
                        .collect(Collectors.toSet()));
        return new LoginResponseDTO(accessToken, refreshToken, dto);
    }
}
