package br.edu.waldorf.modules.security.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenRequestDTO {

    @NotBlank(message = "refreshToken é obrigatório")
    private String refreshToken;
}
