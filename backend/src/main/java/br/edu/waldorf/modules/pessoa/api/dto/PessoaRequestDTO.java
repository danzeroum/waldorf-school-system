package br.edu.waldorf.modules.pessoa.api.dto;

import br.edu.waldorf.modules.pessoa.domain.model.Pessoa;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO para requisição de criação/atualização de Pessoa
 * 
 * @author Daniel Lau
 * @version 1.0.0
 * @since 2026-01-31
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PessoaRequestDTO implements Serializable {

    private Pessoa.TipoPessoa tipo;

    @NotBlank(message = "Nome completo é obrigatório")
    @Size(min = 3, max = 200, message = "Nome deve ter entre 3 e 200 caracteres")
    private String nomeCompleto;

    @Size(max = 14, message = "CPF deve ter no máximo 14 caracteres")
    private String cpf;

    @Size(max = 20, message = "RG deve ter no máximo 20 caracteres")
    private String rg;

    private LocalDate dataNascimento;

    @Email(message = "Email deve ser válido")
    @NotBlank(message = "Email é obrigatório")
    private String email;

    @Size(max = 20, message = "Telefone deve ter no máximo 20 caracteres")
    private String telefonePrincipal;

    @Size(max = 20, message = "Telefone secundário deve ter no máximo 20 caracteres")
    private String telefoneSecundario;

    private String fotoUrl;

    private Boolean ativo;

    @Builder.Default
    private List<EnderecoDTO> enderecos = new ArrayList<>();
}
