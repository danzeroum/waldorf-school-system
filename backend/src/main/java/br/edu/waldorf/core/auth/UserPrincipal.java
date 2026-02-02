package br.edu.waldorf.core.auth;

import br.edu.waldorf.modules.security.domain.model.Perfil;
import br.edu.waldorf.modules.security.domain.model.Usuario;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class UserPrincipal implements UserDetails {

    private final Long id;
    private final String username;
    private final String email;
    private final String password;
    private final boolean ativo;
    private final boolean bloqueado;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(Usuario usuario, Collection<? extends GrantedAuthority> authorities) {
        this.id = usuario.getId();
        this.username = usuario.getUsername();
        this.email = usuario.getEmail();
        this.password = usuario.getPasswordHash();
        this.ativo = Boolean.TRUE.equals(usuario.getAtivo());
        this.bloqueado = Boolean.TRUE.equals(usuario.getBloqueado());
        this.authorities = authorities;
    }

    public static UserPrincipal fromUsuario(Usuario usuario) {
        Set<GrantedAuthority> roles = usuario.getPerfis().stream()
                .map(Perfil::getNome)
                .map(roleName -> (GrantedAuthority) () -> "ROLE_" + roleName)
                .collect(Collectors.toSet());

        return new UserPrincipal(usuario, roles);
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !bloqueado;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return ativo && !bloqueado;
    }
}
