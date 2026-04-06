package com.waldorf.application.dto.professor;
import com.waldorf.domain.entity.Professor;
import java.time.LocalDateTime;
public record ProfessorResponseDTO(Long id, String nome, String email, String especialidade, boolean ativo, LocalDateTime createdAt) {
    public static ProfessorResponseDTO from(Professor p) { return new ProfessorResponseDTO(p.getId(),p.getNome(),p.getEmail(),p.getEspecialidade(),p.isAtivo(),p.getCreatedAt()); }
}
