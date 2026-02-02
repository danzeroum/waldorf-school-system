package br.edu.waldorf.modules.security.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "permissoes")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Permissao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Ex.: "pedagogia.observacoes.create"
    @Column(name = "nome", nullable = false, unique = true, length = 120)
    private String nome;

    @Column(name = "descricao", length = 255)
    private String descricao;

    // Ex.: PESSOAS, ALUNOS, OBSERVACOES
    @Column(name = "recurso", nullable = false, length = 80)
    private String recurso;

    // Ex.: READ, CREATE, UPDATE, DELETE, APPROVE
    @Column(name = "acao", nullable = false, length = 40)
    private String acao;

    // Ex.: GLOBAL, TURMA, PROPRIO
    @Column(name = "escopo", nullable = false, length = 40)
    private String escopo;
}
