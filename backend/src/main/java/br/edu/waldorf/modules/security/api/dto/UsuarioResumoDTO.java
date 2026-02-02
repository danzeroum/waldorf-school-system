package br.edu.waldorf.modules.security.api.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
@Builder
public class UsuarioResumoDTO {

    private Long id;
    private String username;
    private String email;
    private String nomeCompleto;
    private String primaryRole;
    private Set<String> roles;
}
