package com.waldorf.application.dto.professor;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
public record ProfessorRequestDTO(
    @NotBlank(message = "Nome é obrigatório") String nome,
    @NotBlank(message = "E-mail é obrigatório") @Email(message = "E-mail inválido") String email,
    String especialidade
) {}
