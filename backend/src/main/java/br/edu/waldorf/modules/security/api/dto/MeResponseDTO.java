package br.edu.waldorf.modules.security.api.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class MeResponseDTO {
    private Long   id;
    private String username;
    private String email;
    private String nomeCompleto;
    private String primaryRole;
    private Set<String> roles;
    private Set<String> permissions;
}
