package br.edu.waldorf.modules.security.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CheckPermissionRequestDTO {

    @NotBlank(message = "resource é obrigatório")
    private String resource;   // ex: "observations"

    @NotBlank(message = "action é obrigatório")
    private String action;     // ex: "create"

    private Long contextId;    // ex: alunoId, turmaId
}
