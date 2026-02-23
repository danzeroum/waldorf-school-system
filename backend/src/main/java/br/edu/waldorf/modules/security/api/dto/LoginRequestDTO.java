package br.edu.waldorf.modules.security.api.dto;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
public class LoginRequestDTO {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    private String deviceId;
    private String deviceType; // WEB, MOBILE, etc.
}
