package br.edu.waldorf.modules.pessoa.api.mapper;

import br.edu.waldorf.modules.pessoa.api.dto.EnderecoDTO;
import br.edu.waldorf.modules.pessoa.api.dto.PessoaRequestDTO;
import br.edu.waldorf.modules.pessoa.api.dto.PessoaResponseDTO;
import br.edu.waldorf.modules.pessoa.domain.model.Endereco;
import br.edu.waldorf.modules.pessoa.domain.model.Pessoa;
import org.mapstruct.*;

import java.util.List;

/**
 * Mapper para conversão entre Entity e DTO usando MapStruct
 * 
 * @author Daniel Lau
 * @version 1.0.0
 * @since 2026-01-31
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface PessoaMapper {

    /**
     * Converte RequestDTO para Entity
     * 
     * @param dto Request DTO
     * @return Entity Pessoa
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "lgpdConsentimentoGeral", constant = "false")
    @Mapping(target = "lgpdDataConsentimento", ignore = true)
    @Mapping(target = "lgpdBaseLegal", ignore = true)
    @Mapping(target = "classificacaoDados", ignore = true)
    @Mapping(target = "dataExclusaoPrevista", ignore = true)
    Pessoa toEntity(PessoaRequestDTO dto);

    /**
     * Converte Entity para ResponseDTO
     * 
     * @param pessoa Entity Pessoa
     * @return Response DTO
     */
    PessoaResponseDTO toResponseDTO(Pessoa pessoa);

    /**
     * Converte lista de Entities para lista de ResponseDTOs
     * 
     * @param pessoas Lista de entities
     * @return Lista de DTOs
     */
    List<PessoaResponseDTO> toResponseDTOList(List<Pessoa> pessoas);

    /**
     * Atualiza uma Entity existente com dados do RequestDTO
     * Ignora campos null do DTO (update parcial)
     * 
     * @param dto Request DTO com novos dados
     * @param pessoa Entity existente a ser atualizada
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "lgpdConsentimentoGeral", ignore = true)
    @Mapping(target = "lgpdDataConsentimento", ignore = true)
    @Mapping(target = "lgpdBaseLegal", ignore = true)
    @Mapping(target = "classificacaoDados", ignore = true)
    @Mapping(target = "dataExclusaoPrevista", ignore = true)
    void updateEntityFromDTO(PessoaRequestDTO dto, @MappingTarget Pessoa pessoa);

    // === Mapeamento de Endereço ===

    /**
     * Converte EnderecoDTO para Entity
     * 
     * @param dto Endereco DTO
     * @return Entity Endereco
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "pessoa", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Endereco toEnderecoEntity(EnderecoDTO dto);

    /**
     * Converte Entity Endereco para DTO
     * 
     * @param endereco Entity Endereco
     * @return Endereco DTO
     */
    EnderecoDTO toEnderecoDTO(Endereco endereco);

    /**
     * Converte lista de Enderecos Entity para DTO
     * 
     * @param enderecos Lista de entities
     * @return Lista de DTOs
     */
    List<EnderecoDTO> toEnderecoDTOList(List<Endereco> enderecos);

    /**
     * Converte lista de Enderecos DTO para Entity
     * 
     * @param dtos Lista de DTOs
     * @return Lista de entities
     */
    List<Endereco> toEnderecoEntityList(List<EnderecoDTO> dtos);
}
