package br.edu.waldorf.modules.comunidade.domain.service;

import br.edu.waldorf.modules.comunidade.domain.model.CanalComunicacao;
import br.edu.waldorf.modules.comunidade.domain.model.MensagemCanal;
import br.edu.waldorf.modules.comunidade.domain.repository.CanalComunicacaoRepository;
import br.edu.waldorf.modules.comunidade.domain.repository.MensagemCanalRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service de dominio para Canal de Comunicacao
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CanalComunicacaoService {

    private final CanalComunicacaoRepository canalRepository;
    private final MensagemCanalRepository mensagemRepository;

    @Transactional(readOnly = true)
    public List<CanalComunicacao> listarAtivos() {
        return canalRepository.findByAtivoTrueOrderByNome();
    }

    @Transactional(readOnly = true)
    public CanalComunicacao buscarPorId(Long id) {
        return canalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Canal nao encontrado: id=" + id));
    }

    @Transactional(readOnly = true)
    public Page<MensagemCanal> listarMensagens(Long canalId, Pageable pageable) {
        buscarPorId(canalId); // valida existencia
        return mensagemRepository.findByCanalIdOrderByCreatedAtDesc(canalId, pageable);
    }

    @Transactional(readOnly = true)
    public List<MensagemCanal> listarFixadas(Long canalId) {
        return mensagemRepository.findFixadasByCanal(canalId);
    }

    @Transactional
    public CanalComunicacao criar(CanalComunicacao canal) {
        CanalComunicacao salvo = canalRepository.save(canal);
        log.info("Canal criado: id={}, nome={}, tipo={}", salvo.getId(), salvo.getNome(), salvo.getTipo());
        return salvo;
    }

    @Transactional
    public MensagemCanal enviarMensagem(MensagemCanal mensagem) {
        buscarPorId(mensagem.getCanal().getId()); // valida canal
        MensagemCanal salva = mensagemRepository.save(mensagem);
        log.info("Mensagem enviada: canal={}, autor={}, tipo={}",
                mensagem.getCanal().getId(), mensagem.getAutor().getId(), mensagem.getTipo());
        return salva;
    }

    @Transactional
    public MensagemCanal editarMensagem(Long mensagemId, String novoConteudo) {
        MensagemCanal mensagem = mensagemRepository.findById(mensagemId)
                .orElseThrow(() -> new EntityNotFoundException("Mensagem nao encontrada: id=" + mensagemId));
        mensagem.editar(novoConteudo);
        return mensagemRepository.save(mensagem);
    }

    @Transactional
    public void fixarMensagem(Long mensagemId, boolean fixar) {
        MensagemCanal mensagem = mensagemRepository.findById(mensagemId)
                .orElseThrow(() -> new EntityNotFoundException("Mensagem nao encontrada: id=" + mensagemId));
        if (fixar) mensagem.fixar(); else mensagem.desafixar();
        mensagemRepository.save(mensagem);
    }

    @Transactional
    public void excluirMensagem(Long mensagemId) {
        mensagemRepository.deleteById(mensagemId);
        log.info("Mensagem excluida: id={}", mensagemId);
    }
}
