package com.waldorf.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "perfis")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Perfil {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String nome;
}
