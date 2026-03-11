package com.waldorf.domain.entity;

import com.waldorf.domain.enums.SituacaoContrato;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "contratos")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Contrato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    @Column(name = "ano_letivo", nullable = false)
    private int anoLetivo;

    @Column(name = "valor_mensalidade", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorMensalidade;

    @Column(name = "desconto", precision = 10, scale = 2)
    private BigDecimal desconto;

    @Column(name = "valor_matricula", precision = 10, scale = 2)
    private BigDecimal valorMatricula;

    @Column(name = "total_parcelas")
    private int totalParcelas;

    @Column(name = "dia_vencimento")
    private int diaVencimento;

    @Column(name = "data_inicio")
    private LocalDate dataInicio;

    @Column(name = "data_fim")
    private LocalDate dataFim;

    @Enumerated(EnumType.STRING)
    private SituacaoContrato situacao = SituacaoContrato.ATIVO;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() { createdAt = updatedAt = LocalDateTime.now(); }

    @PreUpdate
    protected void onUpdate() { updatedAt = LocalDateTime.now(); }
}
