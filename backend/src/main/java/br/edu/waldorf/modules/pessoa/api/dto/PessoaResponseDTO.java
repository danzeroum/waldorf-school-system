package br.edu.waldorf.modules.pessoa.api.dto;

import br.edu.waldorf.modules.pessoa.domain.model.Pessoa;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO para resposta de consulta de Pessoa
 * Inclui campos adicionais como timestamps e informações LGPD
 * 
 * @author Daniel Lau
 * @version 1.0.0
 * @since 2026-01-31
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PessoaResponseDTO implements Serializable {

    private Long id;
    private Pessoa.TipoPessoa tipo;
    private String nomeCompleto;
    private String cpf;
    private String rg;
    private LocalDate dataNascimento;
    private String email;
    private String telefonePrincipal;
    private String telefoneSecundario;
    private String fotoUrl;
    private Boolean ativo;

    // Campos LGPD (apenas informações básicas)
    private Boolean lgpdConsentimentoGeral;
    private LocalDateTime lgpdDataConsentimento;

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Endereços
    @Builder.Default
    private List<EnderecoDTO> enderecos = new ArrayList<>();
}
