package br.edu.waldorf.modules.security.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CheckPermissionResponseDTO {
    private boolean allowed;
    private String  resource;
    private String  action;
    private Long    contextId;
    private String  reason;   // motivo quando allowed=false
}
