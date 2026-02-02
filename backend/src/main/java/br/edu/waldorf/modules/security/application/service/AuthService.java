package br.edu.waldorf.modules.security.application.service;

import br.edu.waldorf.core.auth.JwtTokenProvider;
import br.edu.waldorf.core.auth.UserPrincipal;
import br.edu.waldorf.modules.security.api.dto.LoginRequestDTO;
import br.edu.waldorf.modules.security.api.dto.LoginResponseDTO;
import br.edu.waldorf.modules.security.api.dto.UsuarioResumoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public LoginResponseDTO login(LoginRequestDTO request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        String accessToken = tokenProvider.generateAccessToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(principal.getId());

        UsuarioResumoDTO userResumo = UsuarioResumoDTO.builder()
                .id(principal.getId())
                .username(principal.getUsername())
                .email(principal.getEmail())
                .nomeCompleto(principal.getUsername()) // pode ser ajustado para Pessoa.nomeCompleto
                .primaryRole(principal.getAuthorities().stream()
                        .map(a -> a.getAuthority())
                        .findFirst()
                        .orElse(null))
                .roles(principal.getAuthorities().stream()
                        .map(a -> a.getAuthority())
                        .collect(Collectors.toSet()))
                .build();

        return LoginResponseDTO.builder()
                .success(true)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(900L)
                .refreshExpiresIn(604800L)
                .user(userResumo)
                .build();
    }
}
