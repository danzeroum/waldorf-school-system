package br.edu.waldorf.modules.financeiro.api;

import br.edu.waldorf.modules.financeiro.domain.model.PlanoMensalidade;
import br.edu.waldorf.modules.financeiro.domain.repository.PlanoMensalidadeRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

/**
 * Controller REST para Planos de Mensalidade
 * Base path: /api/v1/payment-plans
 */
@RestController
@RequestMapping("/api/v1/payment-plans")
@RequiredArgsConstructor
@Tag(name = "Planos de Mensalidade", description = "Planos financeiros por ano de vigência")
public class PlanoMensalidadeController {

    private final PlanoMensalidadeRepository planoRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA','DIRECAO')")
    @Operation(summary = "Listar planos ativos")
    public ResponseEntity<List<PlanoMensalidade>> listar(
            @RequestParam(required = false) Integer anoVigencia
    ) {
        List<PlanoMensalidade> lista = anoVigencia != null
                ? planoRepository.findByAnoVigenciaAndAtivoTrue(anoVigencia)
                : planoRepository.findByAtivoTrueOrderByNome();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA','DIRECAO')")
    @Operation(summary = "Buscar plano por ID")
    public ResponseEntity<PlanoMensalidade> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(
                planoRepository.findById(id)
                        .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Plano não encontrado: " + id))
        );
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','DIRECAO')")
    @Operation(summary = "Criar plano de mensalidade")
    public ResponseEntity<PlanoMensalidade> criar(
            @Valid @RequestBody PlanoMensalidade plano,
            UriComponentsBuilder ucb
    ) {
        PlanoMensalidade salvo = planoRepository.save(plano);
        var uri = ucb.path("/api/v1/payment-plans/{id}").buildAndExpand(salvo.getId()).toUri();
        return ResponseEntity.created(uri).body(salvo);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','DIRECAO')")
    @Operation(summary = "Atualizar plano de mensalidade")
    public ResponseEntity<PlanoMensalidade> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody PlanoMensalidade dados
    ) {
        PlanoMensalidade existente = planoRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Plano não encontrado: " + id));
        existente.setNome(dados.getNome());
        existente.setDescricao(dados.getDescricao());
        existente.setValorBase(dados.getValorBase());
        existente.setNumeroParcelas(dados.getNumeroParcelas());
        existente.setDescontoAnualPercentual(dados.getDescontoAnualPercentual());
        existente.setDescontoIrmaoPercentual(dados.getDescontoIrmaoPercentual());
        existente.setTaxaMatricula(dados.getTaxaMatricula());
        existente.setAtivo(dados.getAtivo());
        return ResponseEntity.ok(planoRepository.save(existente));
    }
}
