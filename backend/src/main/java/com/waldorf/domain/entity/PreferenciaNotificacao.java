package com.waldorf.domain.entity;

import com.waldorf.domain.enums.AgregacaoNotificacao;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "preferencias_notificacao")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PreferenciaNotificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    @Builder.Default
    @Column(nullable = false)
    private boolean email = true;

    @Builder.Default
    @Column(nullable = false)
    private boolean push = true;

    @Builder.Default
    @Column(nullable = false)
    private boolean sms = false;

    @Builder.Default
    @Column(name = "in_app", nullable = false)
    private boolean inApp = true;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AgregacaoNotificacao agregacao = AgregacaoNotificacao.IMEDIATO;

    @Column(name = "silencio_inicio")
    private LocalTime silencioInicio;

    @Column(name = "silencio_fim")
    private LocalTime silencioFim;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist @PreUpdate
    protected void onUpdate() { updatedAt = LocalDateTime.now(); }
}
