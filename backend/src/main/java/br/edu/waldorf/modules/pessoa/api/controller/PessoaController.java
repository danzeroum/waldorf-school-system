package br.edu.waldorf.modules.pessoa.api.controller;

import br.edu.waldorf.modules.pessoa.api.dto.EnderecoDTO;
import br.edu.waldorf.modules.pessoa.api.dto.PessoaRequestDTO;
import br.edu.waldorf.modules.pessoa.api.dto.PessoaResponseDTO;
import br.edu.waldorf.modules.pessoa.api.mapper.PessoaMapper;
import br.edu.waldorf.modules.pessoa.domain.model.Endereco;
import br.edu.waldorf.modules.pessoa.domain.model.Pessoa;
import br.edu.waldorf.modules.pessoa.domain.service.PessoaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para gerência de Pessoas
 * 
 * @author Daniel Lau
 * @version 1.0.0
 * @since 2026-01-31
 */
@RestController
@RequestMapping("/api/v1/pessoas")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Pessoas", description = "API para gerência de pessoas do sistema")
public class PessoaController {

    private final PessoaService pessoaService;
    private final PessoaMapper pessoaMapper;

    // ===== OPERAÇÕES CRUD =====

    @PostMapping
    @Operation(summary = "Criar nova pessoa", description = "Cria uma nova pessoa no sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Pessoa criada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "409", description = "CPF ou email já cadastrado")
    })
    public ResponseEntity<PessoaResponseDTO> criar(
            @Valid @RequestBody PessoaRequestDTO dto) {
        
        log.info("Criando nova pessoa: {}", dto.getNomeCompleto());
        
        Pessoa pessoa = pessoaMapper.toEntity(dto);
        Pessoa pessoaSalva = pessoaService.create(pessoa);
        PessoaResponseDTO response = pessoaMapper.toResponseDTO(pessoaSalva);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pessoa por ID", description = "Retorna os dados de uma pessoa específica")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pessoa encontrada"),
        @ApiResponse(responseCode = "404", description = "Pessoa não encontrada")
    })
    public ResponseEntity<PessoaResponseDTO> buscarPorId(
            @Parameter(description = "ID da pessoa") @PathVariable Long id) {
        
        log.debug("Buscando pessoa por ID: {}", id);
        
        return pessoaService.findById(id)
            .map(pessoaMapper::toResponseDTO)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Listar todas as pessoas", description = "Lista pessoas com paginação")
    @ApiResponse(responseCode = "200", description = "Lista de pessoas")
    public ResponseEntity<Page<PessoaResponseDTO>> listarTodas(
            @PageableDefault(size = 20, sort = "nomeCompleto", direction = Sort.Direction.ASC) Pageable pageable) {
        
        log.debug("Listando todas as pessoas - Página: {}", pageable.getPageNumber());
        
        Page<Pessoa> pessoas = pessoaService.findAll(pageable);
        Page<PessoaResponseDTO> response = pessoas.map(pessoaMapper::toResponseDTO);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/ativas")
    @Operation(summary = "Listar pessoas ativas", description = "Lista apenas pessoas ativas com paginação")
    @ApiResponse(responseCode = "200", description = "Lista de pessoas ativas")
    public ResponseEntity<Page<PessoaResponseDTO>> listarAtivas(
            @PageableDefault(size = 20, sort = "nomeCompleto", direction = Sort.Direction.ASC) Pageable pageable) {
        
        log.debug("Listando pessoas ativas");
        
        Page<Pessoa> pessoas = pessoaService.findAtivas(pageable);
        Page<PessoaResponseDTO> response = pessoas.map(pessoaMapper::toResponseDTO);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/tipo/{tipo}")
    @Operation(summary = "Buscar por tipo", description = "Lista pessoas por tipo com paginação")
    @ApiResponse(responseCode = "200", description = "Lista de pessoas do tipo especificado")
    public ResponseEntity<Page<PessoaResponseDTO>> buscarPorTipo(
            @Parameter(description = "Tipo da pessoa") @PathVariable Pessoa.TipoPessoa tipo,
            @PageableDefault(size = 20, sort = "nomeCompleto", direction = Sort.Direction.ASC) Pageable pageable) {
        
        log.debug("Buscando pessoas por tipo: {}", tipo);
        
        Page<Pessoa> pessoas = pessoaService.findByTipo(tipo, pageable);
        Page<PessoaResponseDTO> response = pessoas.map(pessoaMapper::toResponseDTO);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/buscar")
    @Operation(summary = "Busca avançada", description = "Busca pessoas por nome, email ou CPF")
    @ApiResponse(responseCode = "200", description = "Resultados da busca")
    public ResponseEntity<Page<PessoaResponseDTO>> buscaAvancada(
            @Parameter(description = "Termo de busca") @RequestParam String termo,
            @PageableDefault(size = 20, sort = "nomeCompleto", direction = Sort.Direction.ASC) Pageable pageable) {
        
        log.debug("Busca avançada por termo: {}", termo);
        
        Page<Pessoa> pessoas = pessoaService.buscaAvancada(termo, pageable);
        Page<PessoaResponseDTO> response = pessoas.map(pessoaMapper::toResponseDTO);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/cpf/{cpf}")
    @Operation(summary = "Buscar por CPF", description = "Busca uma pessoa pelo CPF")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pessoa encontrada"),
        @ApiResponse(responseCode = "404", description = "Pessoa não encontrada")
    })
    public ResponseEntity<PessoaResponseDTO> buscarPorCpf(
            @Parameter(description = "CPF da pessoa") @PathVariable String cpf) {
        
        log.debug("Buscando pessoa por CPF: {}", cpf);
        
        return pessoaService.findByCpf(cpf)
            .map(pessoaMapper::toResponseDTO)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Buscar por email", description = "Busca uma pessoa pelo email")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pessoa encontrada"),
        @ApiResponse(responseCode = "404", description = "Pessoa não encontrada")
    })
    public ResponseEntity<PessoaResponseDTO> buscarPorEmail(
            @Parameter(description = "Email da pessoa") @PathVariable String email) {
        
        log.debug("Buscando pessoa por email: {}", email);
        
        return pessoaService.findByEmail(email)
            .map(pessoaMapper::toResponseDTO)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar pessoa", description = "Atualiza os dados de uma pessoa existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pessoa atualizada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Pessoa não encontrada"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<PessoaResponseDTO> atualizar(
            @Parameter(description = "ID da pessoa") @PathVariable Long id,
            @Valid @RequestBody PessoaRequestDTO dto) {
        
        log.info("Atualizando pessoa ID: {}", id);
        
        Pessoa pessoaAtualizada = pessoaMapper.toEntity(dto);
        Pessoa pessoaSalva = pessoaService.update(id, pessoaAtualizada);
        PessoaResponseDTO response = pessoaMapper.toResponseDTO(pessoaSalva);
        
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/inativar")
    @Operation(summary = "Inativar pessoa", description = "Inativa uma pessoa (soft delete)")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Pessoa inativada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Pessoa não encontrada")
    })
    public ResponseEntity<Void> inativar(
            @Parameter(description = "ID da pessoa") @PathVariable Long id) {
        
        log.info("Inativando pessoa ID: {}", id);
        
        pessoaService.inativar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reativar")
    @Operation(summary = "Reativar pessoa", description = "Reativa uma pessoa inativa")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Pessoa reativada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Pessoa não encontrada")
    })
    public ResponseEntity<Void> reativar(
            @Parameter(description = "ID da pessoa") @PathVariable Long id) {
        
        log.info("Reativando pessoa ID: {}", id);
        
        pessoaService.reativar(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar pessoa", description = "Deleta permanentemente uma pessoa (use com cuidado!)")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Pessoa deletada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Pessoa não encontrada")
    })
    public ResponseEntity<Void> deletar(
            @Parameter(description = "ID da pessoa") @PathVariable Long id) {
        
        log.warn("Deletando permanentemente pessoa ID: {}", id);
        
        pessoaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ===== OPERAÇÕES LGPD =====

    @PostMapping("/{id}/lgpd/consentimento")
    @Operation(summary = "Registrar consentimento LGPD", description = "Registra o consentimento LGPD de uma pessoa")
    @ApiResponse(responseCode = "204", description = "Consentimento registrado com sucesso")
    public ResponseEntity<Void> registrarConsentimentoLGPD(
            @Parameter(description = "ID da pessoa") @PathVariable Long id) {
        
        log.info("Registrando consentimento LGPD para pessoa ID: {}", id);
        
        pessoaService.registrarConsentimentoLGPD(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/lgpd/consentimento")
    @Operation(summary = "Revogar consentimento LGPD", description = "Revoga o consentimento LGPD de uma pessoa")
    @ApiResponse(responseCode = "204", description = "Consentimento revogado com sucesso")
    public ResponseEntity<Void> revogarConsentimentoLGPD(
            @Parameter(description = "ID da pessoa") @PathVariable Long id) {
        
        log.info("Revogando consentimento LGPD para pessoa ID: {}", id);
        
        pessoaService.revogarConsentimentoLGPD(id);
        return ResponseEntity.noContent().build();
    }

    // ===== OPERAÇÕES DE ENDEREÇO =====

    @PostMapping("/{id}/enderecos")
    @Operation(summary = "Adicionar endereço", description = "Adiciona um endereço a uma pessoa")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Endereço adicionado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Pessoa não encontrada")
    })
    public ResponseEntity<EnderecoDTO> adicionarEndereco(
            @Parameter(description = "ID da pessoa") @PathVariable Long id,
            @Valid @RequestBody EnderecoDTO enderecoDTO) {
        
        log.info("Adicionando endereço à pessoa ID: {}", id);
        
        Endereco endereco = pessoaMapper.toEnderecoEntity(enderecoDTO);
        Endereco enderecoSalvo = pessoaService.adicionarEndereco(id, endereco);
        EnderecoDTO response = pessoaMapper.toEnderecoDTO(enderecoSalvo);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}/enderecos")
    @Operation(summary = "Listar endereços", description = "Lista todos os endereços de uma pessoa")
    @ApiResponse(responseCode = "200", description = "Lista de endereços")
    public ResponseEntity<List<EnderecoDTO>> listarEnderecos(
            @Parameter(description = "ID da pessoa") @PathVariable Long id) {
        
        log.debug("Listando endereços da pessoa ID: {}", id);
        
        List<Endereco> enderecos = pessoaService.listarEnderecos(id);
        List<EnderecoDTO> response = pessoaMapper.toEnderecoDTOList(enderecos);
        
        return ResponseEntity.ok(response);
    }

    // ===== ESTATÍSTICAS =====

    @GetMapping("/estatisticas/tipo/{tipo}")
    @Operation(summary = "Contar por tipo", description = "Retorna a quantidade de pessoas de um tipo")
    @ApiResponse(responseCode = "200", description = "Contagem de pessoas")
    public ResponseEntity<Long> contarPorTipo(
            @Parameter(description = "Tipo da pessoa") @PathVariable Pessoa.TipoPessoa tipo) {
        
        log.debug("Contando pessoas por tipo: {}", tipo);
        
        long count = pessoaService.contarPorTipo(tipo);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/estatisticas/ativas/tipo/{tipo}")
    @Operation(summary = "Contar ativas por tipo", description = "Retorna a quantidade de pessoas ativas de um tipo")
    @ApiResponse(responseCode = "200", description = "Contagem de pessoas ativas")
    public ResponseEntity<Long> contarAtivasPorTipo(
            @Parameter(description = "Tipo da pessoa") @PathVariable Pessoa.TipoPessoa tipo) {
        
        log.debug("Contando pessoas ativas por tipo: {}", tipo);
        
        long count = pessoaService.contarAtivasPorTipo(tipo);
        return ResponseEntity.ok(count);
    }
}
