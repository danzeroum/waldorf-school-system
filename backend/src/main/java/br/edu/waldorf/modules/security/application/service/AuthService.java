package br.edu.waldorf.modules.security.application.service;

import br.edu.waldorf.core.auth.JwtTokenProvider;
import br.edu.waldorf.core.auth.UserPrincipal;
import br.edu.waldorf.modules.security.api.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Serviço de aplicação para autenticação e autorização JWT.
 * Cobre login, refresh de token, logout e verificação de permissão contextual.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider      tokenProvider;

    // -------------------------------------------------------------------
    // Login
    // -------------------------------------------------------------------
    public LoginResponseDTO login(LoginRequestDTO request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        String accessToken  = tokenProvider.generateAccessToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(principal.getId());

        log.info("Login bem-sucedido: usuario={}", principal.getUsername());

        return LoginResponseDTO.builder()
                .success(true)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(900L)
                .refreshExpiresIn(604800L)
                .user(buildUsuarioResumo(principal))
                .build();
    }

    // -------------------------------------------------------------------
    // Refresh Token
    // -------------------------------------------------------------------
    public LoginResponseDTO refreshToken(String refreshToken) {
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new BadCredentialsException("Refresh token inválido ou expirado");
        }

        Long userId = tokenProvider.getUserIdFromToken(refreshToken);
        String newAccessToken  = tokenProvider.generateAccessTokenFromUserId(userId);
        String newRefreshToken = tokenProvider.generateRefreshToken(userId);

        log.info("Token renovado: userId={}", userId);

        return LoginResponseDTO.builder()
                .success(true)
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .expiresIn(900L)
                .refreshExpiresIn(604800L)
                .build();
    }

    // -------------------------------------------------------------------
    // Logout (stateless — apenas loga; blacklist pode ser adicionada via Redis)
    // -------------------------------------------------------------------
    public void logout(Long userId, String token) {
        // Em arquitetura stateless pura, o logout é responsabilidade do cliente
        // (descartar tokens). Aqui registramos o evento para auditoria.
        // TODO: adicionar token ao Redis blacklist quando Redis estiver configurado.
        log.info("Logout registrado: userId={}", userId);
    }

    // -------------------------------------------------------------------
    // GET /me — perfil do usuário autenticado
    // -------------------------------------------------------------------
    public MeResponseDTO getMe(UserPrincipal user) {
        Set<String> roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        return MeResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .nomeCompleto(user.getUsername())
                .primaryRole(roles.stream().findFirst().orElse(null))
                .roles(roles)
                .permissions(Set.of()) // TODO: popular via RbacService
                .build();
    }

    // -------------------------------------------------------------------
    // Verificação de permissão contextual
    // -------------------------------------------------------------------
    public CheckPermissionResponseDTO checkPermission(
            CheckPermissionRequestDTO request, UserPrincipal user) {

        // Admin sempre tem permissão
        boolean isAdmin = user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            return CheckPermissionResponseDTO.builder()
                    .allowed(true)
                    .resource(request.getResource())
                    .action(request.getAction())
                    .contextId(request.getContextId())
                    .build();
        }

        // TODO: integrar com RbacService para verificação granular por contexto
        // Por ora, permite qualquer usuário autenticado
        boolean allowed = user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().startsWith("ROLE_"));

        return CheckPermissionResponseDTO.builder()
                .allowed(allowed)
                .resource(request.getResource())
                .action(request.getAction())
                .contextId(request.getContextId())
                .reason(allowed ? null : "Permissão insuficiente para este recurso/contexto")
                .build();
    }

    // -------------------------------------------------------------------
    // Helpers privados
    // -------------------------------------------------------------------
    private UsuarioResumoDTO buildUsuarioResumo(UserPrincipal principal) {
        Set<String> roles = principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        return UsuarioResumoDTO.builder()
                .id(principal.getId())
                .username(principal.getUsername())
                .email(principal.getEmail())
                .nomeCompleto(principal.getUsername())
                .primaryRole(roles.stream().findFirst().orElse(null))
                .roles(roles)
                .build();
    }
}
