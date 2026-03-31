package com.waldorf.presentation.controller;

import com.waldorf.application.dto.ConsentimentoDTO;
import com.waldorf.application.dto.ResumoLgpdDTO;
import com.waldorf.application.dto.ResponderSolicitacaoRequest;
import com.waldorf.application.dto.SolicitacaoDTO;
import com.waldorf.application.service.LgpdService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/lgpd")
@RequiredArgsConstructor
@Tag(name = "LGPD", description = "Consentimentos e solicitações LGPD")
public class LgpdController {

    private final LgpdService lgpdService;

    @GetMapping("/consentimentos")
    @PreAuthorize("hasAnyRole('ADMIN','COORDENADOR')")
    @Operation(summary = "Lista consentimentos")
    public ResponseEntity<List<ConsentimentoDTO>> listarConsentimentos(
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(lgpdService.listarConsentimentos(status));
    }

    @GetMapping("/solicitacoes")
    @PreAuthorize("hasAnyRole('ADMIN','COORDENADOR')")
    @Operation(summary = "Lista solicitações de titulares")
    public ResponseEntity<List<SolicitacaoDTO>> listarSolicitacoes(
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(lgpdService.listarSolicitacoes(status));
    }

    @PutMapping("/solicitacoes/{id}/responder")
    @PreAuthorize("hasAnyRole('ADMIN','COORDENADOR')")
    @Operation(summary = "Responde uma solicitação de titular")
    public ResponseEntity<SolicitacaoDTO> responder(
            @PathVariable Long id,
            @RequestBody ResponderSolicitacaoRequest req) {
        return ResponseEntity.ok(lgpdService.responder(id, req));
    }

    @GetMapping("/resumo")
    @PreAuthorize("hasAnyRole('ADMIN','COORDENADOR')")
    @Operation(summary = "Resumo de conformidade LGPD")
    public ResponseEntity<ResumoLgpdDTO> resumo() {
        return ResponseEntity.ok(lgpdService.resumo());
    }
}
