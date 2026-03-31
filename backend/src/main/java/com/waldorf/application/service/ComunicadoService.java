package com.waldorf.application.service;

import com.waldorf.application.dto.ComunicadoDTO;
import com.waldorf.application.dto.CreateComunicadoRequest;
import com.waldorf.domain.entity.Comunicado;
import com.waldorf.domain.entity.Turma;
import com.waldorf.domain.entity.Usuario;
import com.waldorf.domain.enums.DestinatarioComunicado;
import com.waldorf.infrastructure.repository.ComunicadoRepository;
import com.waldorf.infrastructure.repository.TurmaRepository;
import com.waldorf.infrastructure.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ComunicadoService {

    private final ComunicadoRepository comunicadoRepository;
    private final TurmaRepository turmaRepository;
    private final UsuarioRepository usuarioRepository;

    public List<ComunicadoDTO> listar() {
        return comunicadoRepository.findByOrderByDataEnvioDesc()
                .stream().map(this::toDTO).toList();
    }

    @Transactional
    public ComunicadoDTO criar(CreateComunicadoRequest req, Long autorId) {
        Usuario autor = usuarioRepository.findById(autorId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + autorId));
        Turma turma = req.turmaId() != null
                ? turmaRepository.findById(req.turmaId()).orElse(null)
                : null;
        Comunicado comunicado = Comunicado.builder()
                .assunto(req.assunto())
                .corpo(req.corpo())
                .destinatarios(DestinatarioComunicado.valueOf(req.destinatarios()))
                .turma(turma)
                .autor(autor)
                .totalLidos(0)
                .build();
        return toDTO(comunicadoRepository.save(comunicado));
    }

    private ComunicadoDTO toDTO(Comunicado c) {
        return new ComunicadoDTO(
                c.getId(),
                c.getAssunto(),
                c.getCorpo(),
                c.getDestinatarios().name(),
                c.getTurma() != null ? c.getTurma().getId() : null,
                c.getTurma() != null ? c.getTurma().getNome() : null,
                c.getAutor().getNomeCompleto(),
                c.getDataEnvio() != null ? c.getDataEnvio().toString() : null,
                c.getTotalDestinatarios(),
                c.getTotalLidos()
        );
    }
}
