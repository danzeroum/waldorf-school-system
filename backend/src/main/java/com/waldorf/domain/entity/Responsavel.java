package com.waldorf.domain.entity;

import com.waldorf.domain.enums.Genero;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "responsaveis")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Responsavel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @Enumerated(EnumType.STRING)
    private Genero genero;

    @Column(unique = true)
    private String email;

    private String telefone;
    private String cpf;
    private String parentesco;
    private String profissao;
    private String empresa;

    @Builder.Default
    @Column(nullable = false)
    private boolean autorizado = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() { createdAt = updatedAt = LocalDateTime.now(); }

    @PreUpdate
    protected void onUpdate() { updatedAt = LocalDateTime.now(); }
}
