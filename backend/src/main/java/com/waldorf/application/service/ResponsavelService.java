package com.waldorf.application.service;

import com.waldorf.application.dto.responsavel.ResponsavelRequestDTO;
import com.waldorf.application.dto.responsavel.ResponsavelResponseDTO;
import com.waldorf.domain.entity.Responsavel;
import com.waldorf.infrastructure.repository.ResponsavelRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResponsavelService {

    private final ResponsavelRepository repository;

    public Page<ResponsavelResponseDTO> listar(String nome, Pageable pageable) {
        return repository.findByNomeContainingIgnoreCaseOrNomeIsNull(nome, pageable)
                .map(this::toDTO);
    }

    public ResponsavelResponseDTO buscarPorId(Long id) {
        return toDTO(findOrThrow(id));
    }

    @Transactional
    public ResponsavelResponseDTO criar(ResponsavelRequestDTO dto) {
        Responsavel r = new Responsavel();
        aplicarDTO(r, dto);
        return toDTO(repository.save(r));
    }

    @Transactional
    public ResponsavelResponseDTO atualizar(Long id, ResponsavelRequestDTO dto) {
        Responsavel r = findOrThrow(id);
        aplicarDTO(r, dto);
        return toDTO(repository.save(r));
    }

    private Responsavel findOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Responsável não encontrado: " + id));
    }

    private void aplicarDTO(Responsavel r, ResponsavelRequestDTO dto) {
        r.setNome(dto.nome());
        r.setDataNascimento(dto.dataNascimento());
        r.setGenero(dto.genero());
        r.setEmail(dto.email());
        r.setTelefone(dto.telefone());
        r.setCpf(dto.cpf());
        r.setProfissao(dto.profissao());
        r.setEmpresa(dto.empresa());
        r.setAutorizado(dto.autorizado());
    }

    private ResponsavelResponseDTO toDTO(Responsavel r) {
        return new ResponsavelResponseDTO(
                r.getId(), r.getNome(), r.getDataNascimento(), r.getGenero(),
                r.getEmail(), r.getTelefone(), r.getProfissao(), r.getEmpresa(),
                r.isAutorizado(), List.of(), r.getCreatedAt()
        );
    }
}
