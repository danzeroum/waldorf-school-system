package com.waldorf.application.service;

import com.waldorf.application.dto.epoca.EpocaRequestDTO;
import com.waldorf.application.dto.epoca.EpocaResponseDTO;
import com.waldorf.domain.entity.EpocaPedagogica;
import com.waldorf.infrastructure.repository.EpocaRepository;
import com.waldorf.infrastructure.repository.TurmaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EpocaService {

    private final EpocaRepository epocaRepository;
    private final TurmaRepository turmaRepository;

    public List<EpocaResponseDTO> listar(Long turmaId, String status) {
        List<EpocaPedagogica> epocas = turmaId != null
                ? epocaRepository.findByTurmaId(turmaId)
                : epocaRepository.findAll();
        if (status != null) {
            epocas = epocas.stream().filter(e -> resolverStatus(e).equals(status)).toList();
        }
        return epocas.stream().map(this::toDTO).toList();
    }

    public EpocaResponseDTO buscarPorId(Long id) {
        return toDTO(findOrThrow(id));
    }

    @Transactional
    public EpocaResponseDTO criar(EpocaRequestDTO dto) {
        EpocaPedagogica e = new EpocaPedagogica();
        aplicarDTO(e, dto);
        return toDTO(epocaRepository.save(e));
    }

    @Transactional
    public EpocaResponseDTO atualizar(Long id, EpocaRequestDTO dto) {
        EpocaPedagogica e = findOrThrow(id);
        aplicarDTO(e, dto);
        return toDTO(epocaRepository.save(e));
    }

    @Transactional
    public EpocaResponseDTO encerrar(Long id) {
        EpocaPedagogica e = findOrThrow(id);
        e.setDataFim(LocalDate.now());
        return toDTO(epocaRepository.save(e));
    }

    private EpocaPedagogica findOrThrow(Long id) {
        return epocaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("\u00c9poca n\u00e3o encontrada: " + id));
    }

    private void aplicarDTO(EpocaPedagogica e, EpocaRequestDTO dto) {
        e.setTurma(turmaRepository.findById(dto.turmaId())
                .orElseThrow(() -> new EntityNotFoundException("Turma n\u00e3o encontrada")));
        e.setTitulo(dto.titulo());
        e.setMateria(dto.materia());
        e.setAspecto(dto.aspecto());
        e.setDataInicio(dto.dataInicio());
        e.setDataFim(dto.dataFim());
        e.setDescricao(dto.descricao());
        e.setObjetivos(dto.objetivos());
    }

    private String resolverStatus(EpocaPedagogica e) {
        LocalDate hoje = LocalDate.now();
        LocalDate fim  = e.getDataFim();
        if (hoje.isBefore(e.getDataInicio()))      return "PLANEJADA";
        if (fim != null && hoje.isAfter(fim))       return "CONCLUIDA";
        return "EM_ANDAMENTO";
    }

    private EpocaResponseDTO toDTO(EpocaPedagogica e) {
        return new EpocaResponseDTO(
                e.getId(),
                e.getTurma().getId(), e.getTurma().getNome(),
                e.getTitulo(), e.getMateria(), e.getAspecto(),
                e.getDataInicio(), e.getDataFim(),
                e.getDescricao(), e.getObjetivos(),
                resolverStatus(e), e.getCreatedAt()
        );
    }
}
