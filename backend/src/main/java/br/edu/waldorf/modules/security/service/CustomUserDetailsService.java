package br.edu.waldorf.modules.security.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * @version 1.0.2
 * @since 2026-01-31
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);
    private final JdbcTemplate jdbcTemplate;

    public CustomUserDetailsService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("🔍 Tentando carregar usuário: {}", username);
        
        String sql = "SELECT u.username, u.password_hash, u.ativo, u.bloqueado " +
                     "FROM usuarios u " +
                     "WHERE u.username = ?";
        
        try {
            UserDetails user = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                String user1 = rs.getString("username");
                String password = rs.getString("password_hash");
                boolean ativo = rs.getBoolean("ativo");
                boolean bloqueado = rs.getBoolean("bloqueado");
                
                log.info("✅ Usuário encontrado: {} | Ativo: {} | Bloqueado: {}", user1, ativo, bloqueado);
                log.info("🔑 Hash do banco (primeiros 20 chars): {}", password.substring(0, Math.min(20, password.length())));
                
                // Buscar perfis do usuário
                List<GrantedAuthority> authorities = getAuthorities(user1);
                
                // Se não tiver perfis, adiciona perfil USER padrão
                if (authorities.isEmpty()) {
                    log.warn("⚠️ Usuário {} sem perfis! Adicionando ROLE_USER padrão", user1);
                    authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                } else {
                    log.info("🛡️ Perfis carregados: {}", authorities);
                }
                
                return User.builder()
                    .username(user1)
                    .password(password)
                    .disabled(!ativo)
                    .accountLocked(bloqueado)
                    .accountExpired(false)
                    .credentialsExpired(false)
                    .authorities(authorities)
                    .build();
            }, username);
            
            log.info("✅ UserDetails criado com sucesso para: {}", username);
            return user;
            
        } catch (Exception e) {
            log.error("❌ Erro ao carregar usuário {}: {}", username, e.getMessage());
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
        
        try {
            jdbcTemplate.query(sql, (rs) -> {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + rs.getString("nome")));
            }, username);
        } catch (Exception e) {
            log.debug("⚠️ Erro ao buscar perfis para {}: {}", username, e.getMessage());
        }
        
        return authorities;
    }
}
