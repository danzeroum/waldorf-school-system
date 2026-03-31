package com.waldorf.domain.entity;

import com.waldorf.domain.enums.StatusConsentimento;
import com.waldorf.domain.enums.TipoConsentimento;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "consentimentos_lgpd")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ConsentimentoLgpd {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsavel_id", nullable = false)
    private Responsavel responsavel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoConsentimento tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusConsentimento status;

    @Column(name = "versao_termos", nullable = false)
    private String versaoTermos;

    @Column(name = "data_aceite")
    private LocalDate dataAceite;

    @Column(name = "data_revogacao")
    private LocalDate dataRevogacao;

    @Column(name = "ip_aceite")
    private String ipAceite;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() { createdAt = updatedAt = LocalDateTime.now(); }

    @PreUpdate
    protected void onUpdate() { updatedAt = LocalDateTime.now(); }
}
