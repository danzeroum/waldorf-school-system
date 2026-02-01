package br.edu.waldorf.modules.security.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Serviço de autenticação customizado que busca usuários do banco de dados
 * 
 * @author Daniel Lau
 * @version 1.0.0
 * @since 2026-01-31
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final JdbcTemplate jdbcTemplate;

    public CustomUserDetailsService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String sql = "SELECT u.username, u.password_hash, u.ativo " +
                     "FROM usuarios u " +
                     "WHERE u.username = ? AND u.bloqueado = false";
        
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                String user = rs.getString("username");
                String password = rs.getString("password_hash");
                boolean enabled = rs.getBoolean("ativo");
                
                // Buscar perfis do usuário
                List<GrantedAuthority> authorities = getAuthorities(user);
                
                return User.builder()
                    .username(user)
                    .password(password)
                    .disabled(!enabled)
                    .authorities(authorities)
                    .build();
            }, username);
        } catch (Exception e) {
            throw new UsernameNotFoundException("Usuário não encontrado: " + username, e);
        }
    }
    
    private List<GrantedAuthority> getAuthorities(String username) {
        String sql = "SELECT p.nome " +
                     "FROM usuarios u " +
                     "JOIN usuarios_perfis up ON u.id = up.usuario_id " +
                     "JOIN perfis p ON up.perfil_id = p.id " +
                     "WHERE u.username = ?";
        
        List<GrantedAuthority> authorities = new ArrayList<>();
        
        jdbcTemplate.query(sql, (rs) -> {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + rs.getString("nome")));
        }, username);
        
        return authorities;
    }
}
