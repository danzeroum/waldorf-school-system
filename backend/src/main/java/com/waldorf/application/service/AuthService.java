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
     * Logout stateless: o servidor retorna 204 e o cliente descarta o token.
     * Para blacklist futura, adicionar invalidarToken() no JwtService.
     */
    public void logout(String token, String email) {
        log.info("Logout solicitado para: {}", email);
        // Stateless — nenhuma ação server-side necessária.
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
