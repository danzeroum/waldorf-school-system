package br.edu.waldorf.modules.pedagogia.api;

import br.edu.waldorf.modules.pedagogia.domain.model.PortfolioItem;
import br.edu.waldorf.modules.pedagogia.domain.repository.PortfolioItemRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

/**
 * Controller REST para Portfólio Digital
 * Base path: /api/v1/portfolio
 */
@RestController
@RequestMapping("/api/v1/portfolio")
@RequiredArgsConstructor
@Tag(name = "Portfólio", description = "Portfólio digital dos alunos")
public class PortfolioController {

    private final PortfolioItemRepository portfolioRepository;

    @GetMapping("/student/{alunoId}")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR','DIRECAO','SECRETARIA')")
    @Operation(summary = "Listar portfólio de um aluno")
    public ResponseEntity<Page<PortfolioItem>> listarPorAluno(
            @PathVariable Long alunoId,
            @PageableDefault(size = 20, sort = "dataCriacao", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(portfolioRepository.findByAlunoIdOrderByDataCriacaoDesc(alunoId, pageable));
    }

    @GetMapping("/student/{alunoId}/parents")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR','DIRECAO','RESPONSAVEL')")
    @Operation(summary = "Listar portfólio visível para os pais")
    public ResponseEntity<List<PortfolioItem>> listarVisivelPais(@PathVariable Long alunoId) {
        return ResponseEntity.ok(portfolioRepository.findVisivelPaisByAluno(alunoId));
    }

    @GetMapping("/gallery")
    @Operation(summary = "Galeria pública de portfólios")
    public ResponseEntity<Page<PortfolioItem>> galeriaPublica(
            @PageableDefault(size = 24) Pageable pageable
    ) {
        return ResponseEntity.ok(portfolioRepository.findGaleriaPublica(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR','DIRECAO')")
    @Operation(summary = "Buscar item de portfólio por ID")
    public ResponseEntity<PortfolioItem> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(
                portfolioRepository.findById(id)
                        .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Item de portfólio não encontrado: " + id))
        );
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR')")
    @Operation(summary = "Adicionar item ao portfólio")
    public ResponseEntity<PortfolioItem> criar(
            @RequestBody PortfolioItem item,
            UriComponentsBuilder ucb
    ) {
        PortfolioItem salvo = portfolioRepository.save(item);
        var uri = ucb.path("/api/v1/portfolio/{id}").buildAndExpand(salvo.getId()).toUri();
        return ResponseEntity.created(uri).body(salvo);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR')")
    @Operation(summary = "Remover item do portfólio")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        portfolioRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
