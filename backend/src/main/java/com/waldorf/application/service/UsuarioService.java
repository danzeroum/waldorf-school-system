package com.waldorf.application.service;

import com.waldorf.application.dto.usuario.AlterarSenhaRequestDTO;
import com.waldorf.application.dto.usuario.UsuarioListResponseDTO;
import com.waldorf.application.dto.usuario.UsuarioRequestDTO;
import com.waldorf.domain.entity.Perfil;
import com.waldorf.domain.entity.Usuario;
import com.waldorf.infrastructure.repository.PerfilRepository;
import com.waldorf.infrastructure.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PerfilRepository perfilRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UsuarioListResponseDTO> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(this::toListResponseDTO).collect(Collectors.toList());
    }

    public UsuarioListResponseDTO buscarPorId(Long id) {
        Usuario u = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario nao encontrado: " + id));
        return toListResponseDTO(u);
    }

    @Transactional
    public UsuarioListResponseDTO criar(UsuarioRequestDTO dto) {
        if (usuarioRepository.existsByEmail(dto.email()))
            throw new IllegalArgumentException("E-mail ja existe: " + dto.email());
        Set<Perfil> perfis = resolverPerfis(dto.perfis());
        if (perfis.isEmpty())
            throw new IllegalArgumentException("Pelo menos um perfil valido deve ser informado");
        String hash = dto.senha() != null ? passwordEncoder.encode(dto.senha()) : passwordEncoder.encode("waldorf2024");
        Usuario u = Usuario.builder().nome(dto.nome()).email(dto.email()).senha(hash)
                .ativo(dto.ativo() != null ? dto.ativo() : true).perfis(perfis).build();
        u = usuarioRepository.save(u);
        log.info("Usuario criado: {} ({})", u.getNome(), u.getEmail());
        return toListResponseDTO(u);
    }

    @Transactional
    public UsuarioListResponseDTO atualizar(Long id, UsuarioRequestDTO dto) {
        Usuario u = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario nao encontrado: " + id));
        if (!u.getEmail().equals(dto.email()) && usuarioRepository.existsByEmail(dto.email()))
            throw new IllegalArgumentException("E-mail ja existe: " + dto.email());
        u.setNome(dto.nome());
        u.setEmail(dto.email());
        if (dto.ativo() != null) u.setAtivo(dto.ativo());
        if (dto.perfis() != null && !dto.perfis().isEmpty()) {
            Set<Perfil> perfis = resolverPerfis(dto.perfis());
            if (!perfis.isEmpty()) u.setPerfis(perfis);
        }
        u = usuarioRepository.save(u);
        log.info("Usuario atualizado: {} ({})", u.getNome(), u.getEmail());
        return toListResponseDTO(u);
    }

    @Transactional
    public UsuarioListResponseDTO toggleAtivo(Long id) {
        Usuario u = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario nao encontrado: " + id));
        u.setAtivo(!u.isAtivo());
        u = usuarioRepository.save(u);
        log.info("Usuario {} {}: {}", u.isAtivo() ? "ativado" : "desativado", u.getNome(), u.getEmail());
        return toListResponseDTO(u);
    }

    @Transactional
    public void alterarSenha(AlterarSenhaRequestDTO dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Usuario u = usuarioRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new EntityNotFoundException("Usuario nao encontrado"));
        if (!passwordEncoder.matches(dto.senhaAtual(), u.getSenha()))
            throw new IllegalArgumentException("Senha atual incorreta");
        u.setSenha(passwordEncoder.encode(dto.novaSenha()));
        usuarioRepository.save(u);
    }

    @Transactional
    public void resetarSenha(Long id, String novaSenha) {
        Usuario u = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario nao encontrado: " + id));
        u.setSenha(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(u);
    }

    @Transactional
    public void deletar(Long id) {
        Usuario u = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario nao encontrado: " + id));
        usuarioRepository.delete(u);
    }

    public List<String> listarPerfisDisponiveis() {
        return perfilRepository.findAll().stream().map(Perfil::getNome).sorted().collect(Collectors.toList());
    }

    private Set<Perfil> resolverPerfis(Set<String> nomes) {
        Set<Perfil> perfis = new HashSet<>();
        for (String nome : nomes) {
            perfilRepository.findByNome(nome.trim().toUpperCase())
                    .ifPresentOrElse(perfis::add, () -> log.warn("Perfil ignorado: {}", nome));
        }
        return perfis;
    }

    private UsuarioListResponseDTO toListResponseDTO(Usuario u) {
        return new UsuarioListResponseDTO(u.getId(), u.getNome(), u.getEmail(), u.isAtivo(),
                u.getPerfis().stream().map(Perfil::getNome).collect(Collectors.toSet()), u.getCreatedAt());
    }
}
