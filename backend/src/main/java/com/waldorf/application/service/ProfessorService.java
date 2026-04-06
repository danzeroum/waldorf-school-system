package com.waldorf.application.service;
import com.waldorf.application.dto.professor.ProfessorRequestDTO;
import com.waldorf.application.dto.professor.ProfessorResponseDTO;
import com.waldorf.domain.entity.Professor;
import com.waldorf.infrastructure.repository.ProfessorRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
@Service @RequiredArgsConstructor
public class ProfessorService {
    private final ProfessorRepository repository;
    public Page<ProfessorResponseDTO> listar(Pageable p) { return repository.findAll(p).map(ProfessorResponseDTO::from); }
    public ProfessorResponseDTO buscarPorId(Long id) { return ProfessorResponseDTO.from(repository.findById(id).orElseThrow(()->new EntityNotFoundException("Professor não encontrado: "+id))); }
    @Transactional public ProfessorResponseDTO criar(ProfessorRequestDTO dto) {
        repository.findByEmail(dto.email()).ifPresent(e->{throw new IllegalArgumentException("E-mail já cadastrado: "+dto.email());});
        Professor prof=new Professor(); prof.setNome(dto.nome()); prof.setEmail(dto.email()); prof.setEspecialidade(dto.especialidade()); prof.setAtivo(true);
        return ProfessorResponseDTO.from(repository.save(prof));
    }
    @Transactional public ProfessorResponseDTO atualizar(Long id, ProfessorRequestDTO dto) {
        Professor prof=repository.findById(id).orElseThrow(()->new EntityNotFoundException("Professor não encontrado: "+id));
        repository.findByEmail(dto.email()).filter(e->!e.getId().equals(id)).ifPresent(e->{throw new IllegalArgumentException("E-mail já cadastrado: "+dto.email());});
        prof.setNome(dto.nome()); prof.setEmail(dto.email()); prof.setEspecialidade(dto.especialidade());
        return ProfessorResponseDTO.from(repository.save(prof));
    }
    @Transactional public void inativar(Long id) { Professor prof=repository.findById(id).orElseThrow(()->new EntityNotFoundException("Professor não encontrado")); prof.setAtivo(false); repository.save(prof); }
}
