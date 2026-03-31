package com.waldorf.application.service;

import com.waldorf.application.dto.AvisoDTO;
import com.waldorf.application.dto.CreateAvisoRequest;
import com.waldorf.domain.entity.Aviso;
import com.waldorf.domain.entity.Turma;
import com.waldorf.domain.entity.Usuario;
import com.waldorf.domain.enums.TipoAviso;
import com.waldorf.infrastructure.repository.AvisoRepository;
import com.waldorf.infrastructure.repository.TurmaRepository;
import com.waldorf.infrastructure.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AvisoService {

    private final AvisoRepository avisoRepository;
    private final TurmaRepository turmaRepository;
    private final UsuarioRepository usuarioRepository;

    public List<AvisoDTO> listar(Long turmaId) {
        List<Aviso> avisos = turmaId != null
                ? avisoRepository.findByTurmaIdOrderByDataPublicacaoDesc(turmaId)
                : avisoRepository.findByOrderByFixadoDescDataPublicacaoDesc();
        return avisos.stream().map(this::toDTO).toList();
    }

    public List<AvisoDTO> listarAtivos() {
        return avisoRepository.findAtivos().stream().map(this::toDTO).toList();
    }

    @Transactional
    public AvisoDTO criar(CreateAvisoRequest req, Long autorId) {
        Usuario autor = usuarioRepository.findById(autorId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + autorId));
        Turma turma = req.turmaId() != null
                ? turmaRepository.findById(req.turmaId()).orElse(null)
                : null;
        Aviso aviso = Aviso.builder()
                .titulo(req.titulo())
                .conteudo(req.conteudo())
                .tipo(TipoAviso.valueOf(req.tipo()))
                .turma(turma)
                .autor(autor)
                .fixado(req.fixado())
                .dataPublicacao(LocalDate.now())
                .dataExpiracao(req.dataExpiracao() != null ? LocalDate.parse(req.dataExpiracao()) : null)
                .build();
        return toDTO(avisoRepository.save(aviso));
    }

    @Transactional
    public void excluir(Long id) {
        avisoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Aviso não encontrado: " + id));
        avisoRepository.deleteById(id);
    }

    private AvisoDTO toDTO(Aviso a) {
        return new AvisoDTO(
                a.getId(),
                a.getTitulo(),
                a.getConteudo(),
                a.getTipo().name(),
                a.getTurma() != null ? a.getTurma().getId() : null,
                a.getTurma() != null ? a.getTurma().getNome() : null,
                // FIX: Usuario.java tem campo 'nome', não 'nomeCompleto'
                a.getAutor().getNome(),
                a.isFixado(),
                a.getDataPublicacao().toString(),
                a.getDataExpiracao() != null ? a.getDataExpiracao().toString() : null
        );
    }
}
