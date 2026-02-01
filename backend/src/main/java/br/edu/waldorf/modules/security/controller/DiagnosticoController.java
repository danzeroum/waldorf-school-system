package br.edu.waldorf.modules.security.controller;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller de Diagnóstico para testar autenticação
 * REMOVER EM PRODUÇÃO!
 */
@RestController
@RequestMapping("/api/diagnostico")
public class DiagnosticoController {

    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    public DiagnosticoController(PasswordEncoder passwordEncoder, JdbcTemplate jdbcTemplate) {
        this.passwordEncoder = passwordEncoder;
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/testar-senha")
    public Map<String, Object> testarSenha(
            @RequestParam String username,
            @RequestParam String senha
    ) {
        Map<String, Object> resultado = new HashMap<>();
        
        try {
            // Buscar hash do banco
            String hashDoBanco = jdbcTemplate.queryForObject(
                "SELECT password_hash FROM usuarios WHERE username = ?",
                String.class,
                username
            );
            
            // Gerar novo hash da senha fornecida
            String novoHash = passwordEncoder.encode(senha);
            
            // Testar se a senha bate com o hash do banco
            boolean senhaCorreta = passwordEncoder.matches(senha, hashDoBanco);
            
            resultado.put("username", username);
            resultado.put("hashDoBanco", hashDoBanco);
            resultado.put("hashDoBancoLength", hashDoBanco.length());
            resultado.put("hashDoBancoBytes", hashDoBanco.getBytes().length);
            resultado.put("novoHashGerado", novoHash);
            resultado.put("senhaCorreta", senhaCorreta);
            resultado.put("passwordEncoderClass", passwordEncoder.getClass().getName());
            
            // Testar se o novo hash valida
            boolean novoHashValida = passwordEncoder.matches(senha, novoHash);
            resultado.put("novoHashValida", novoHashValida);
            
            // Comparar caractere por caractere os primeiros 30 chars
            if (hashDoBanco.length() >= 30) {
                resultado.put("primeiros30Chars", hashDoBanco.substring(0, 30));
            }
            
        } catch (Exception e) {
            resultado.put("erro", e.getMessage());
            resultado.put("stackTrace", e.getClass().getName());
        }
        
        return resultado;
    }
    
    @GetMapping("/gerar-hash")
    public Map<String, String> gerarHash(@RequestParam String senha) {
        Map<String, String> resultado = new HashMap<>();
        
        String hash = passwordEncoder.encode(senha);
        resultado.put("senha", senha);
        resultado.put("hash", hash);
        resultado.put("length", String.valueOf(hash.length()));
        resultado.put("valida", String.valueOf(passwordEncoder.matches(senha, hash)));
        
        return resultado;
    }
}
