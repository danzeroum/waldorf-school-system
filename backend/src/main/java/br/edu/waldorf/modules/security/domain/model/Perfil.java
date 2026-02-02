package br.edu.waldorf.modules.security.domain.model;

import jakarta.persistence.*;
import lombok.*;


import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "perfis")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Perfil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Ex.: ADMIN, PROFESSOR, RESPONSAVEL, SECRETARIA, DIRETOR
    @Column(name = "nome", nullable = false, unique = true, length = 50)
    private String nome;

    @Column(name = "descricao", length = 255)
    private String descricao;

    @Column(name = "nivel_acesso", nullable = false)
    private Integer nivelAcesso;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = Boolean.TRUE;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "perfis_permissoes",
            joinColumns = @JoinColumn(name = "perfil_id"),
            inverseJoinColumns = @JoinColumn(name = "permissao_id")
    )
    @Builder.Default
    private Set<Permissao> permissoes = new HashSet<>();
}
