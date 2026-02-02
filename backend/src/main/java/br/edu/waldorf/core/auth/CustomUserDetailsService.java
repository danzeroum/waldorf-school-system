package br.edu.waldorf.core.auth;

import br.edu.waldorf.modules.security.domain.model.Usuario;
import br.edu.waldorf.modules.security.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));

        if (!usuario.isAtivoNaoBloqueado()) {
            log.warn("Usuário inativo ou bloqueado tentando autenticar: {}", username);
            throw new UsernameNotFoundException("Usuário inativo ou bloqueado");
        }

        return UserPrincipal.fromUsuario(usuario);
    }
}
