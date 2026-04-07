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
                .map(this::toListResponseDTO)
                .collect(Collectors.toList());
    }

    public UsuarioListResponseDTO buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario nao encontrado com ID: " + id));
        return toListResponseDTO(usuario);
    }

    @Transactional
    public UsuarioListResponseDTO criar(UsuarioRequestDTO dto) {
        if (usuarioRepository.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("Ja existe um usuario com este e-mail: " + dto.email());
        }
        Set<Perfil> perfis = resolverPerfis(dto.perfis());
        if (perfis.isEmpty()) {
            throw new IllegalArgumentException("Pelo menos um perfil valido deve ser informado");
        }
        String senhaHash = dto.senha() != null
                ? passwordEncoder.encode(dto.senha())
                : passwordEncoder.encode("waldorf2024");
        Usuario usuario = Usuario.builder()
                .nome(dto.nome())
                .email(dto.email())
                .senha(senhaHash)
                .ativo(dto.ativo() != null ? dto.ativo() : true)
                .perfis(perfis)
                .build();
        usuario = usuarioRepository.save(usuario);
        log.info("Usuario criado: {} ({})", usuario.getNome(), usuario.getEmail());
        return toListResponseDTO(usuario);
    }

    @Transactional
    public UsuarioListResponseDTO atualizar(Long id, UsuarioRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario nao encontrado com ID: " + id));
        if (!usuario.getEmail().equals(dto.email()) && usuarioRepository.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("Ja existe um usuario com este e-mail: " + dto.email());
        }
        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        if (dto.ativo() != null) {
            usuario.setAtivo(dto.ativo());
        }
        if (dto.perfis() != null && !dto.perfis().isEmpty()) {
            Set<Perfil> perfis = resolverPerfis(dto.perfis());
            if (!perfis.isEmpty()) {
                usuario.setPerfis(perfis);
            }
        }
        usuario = usuarioRepository.save(usuario);
        log.info("Usuario atualizado: {} ({})", usuario.getNome(), usuario.getEmail());
        return toListResponseDTO(usuario);
    }

    @Transactional
    public UsuarioListResponseDTO toggleAtivo(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario nao encontrado com ID: " + id));
        usuario.setAtivo(!usuario.isAtivo());
        usuario = usuarioRepository.save(usuario);
        log.info("Usuario {} {}: {}", usuario.isAtivo() ? "ativado" : "desativado", usuario.getNome(), usuario.getEmail());
        return toListResponseDTO(usuario);
    }

    @Transactional
    public void alterarSenha(AlterarSenhaRequestDTO dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = usuarioRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new EntityNotFoundException("Usuario nao encontrado"));
        if (!passwordEncoder.matches(dto.senhaAtual(), usuario.getSenha())) {
            throw new IllegalArgumentException("Senha atual incorreta");
        }
        usuario.setSenha(passwordEncoder.encode(dto.novaSenha()));
        usuarioRepository.save(usuario);
        log.info("Senha alterada para: {}", usuario.getEmail());
    }

    @Transactional
    public void resetarSenha(Long id, String novaSenha) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario nao encontrado com ID: " + id));
        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);
        log.info("Senha resetada pelo admin para: {}", usuario.getEmail());
    }

    @Transactional
    public void deletar(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario nao encontrado com ID: " + id));
        usuarioRepository.delete(usuario);
        log.info("Usuario deletado: {} ({})", usuario.getNome(), usuario.getEmail());
    }

    public List<String> listarPerfisDisponiveis() {
        return perfilRepository.findAll().stream()
                .map(Perfil::getNome)
                .sorted()
                .collect(Collectors.toList());
    }

    private Set<Perfil> resolverPerfis(Set<String> nomesPerfis) {
        Set<Perfil> perfis = new HashSet<>();
        for (String nome : nomesPerfis) {
            String nomeNormalizado = nome.trim().toUpperCase();
            perfilRepository.findByNome(nomeNormalizado)
                    .ifPresentOrElse(
                            perfis::add,
                            () -> log.warn("Perfil nao encontrado e sera ignorado: {}", nomeNormalizado)
                    );
        }
        return perfis;
    }

    private UsuarioListResponseDTO toListResponseDTO(Usuario usuario) {
        return new UsuarioListResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.isAtivo(),
                usuario.getPerfis().stream()
                        .map(Perfil::getNome)
                        .collect(Collectors.toSet()),
                usuario.getCreatedAt()
        );
    }
}
