package br.edu.waldorf.modules.pessoa.api.dto;

import br.edu.waldorf.modules.pessoa.domain.model.Responsavel;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO de resposta para Responsável
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponsavelResponseDTO {

    private Long id;
    private String nomeCompleto;
    private String email;
    private String telefonePrincipal;
    private String telefoneSecundario;
    private String fotoUrl;
    private Responsavel.TipoRelacao tipoRelacao;
    private String profissao;
    private String localTrabalho;
    private String telefoneTrabalho;
    private Boolean autorizadoBuscar;
    private Boolean contatoEmergencia;
    private Boolean guardaCompartilhada;
    private Integer prioridadeContato;
    private Responsavel.SituacaoResponsavel situacao;
    private LocalDateTime createdAt;
}
