package ufpi.poo.spring.bar.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "cardapio", schema = "barspring", indexes = {
        @Index(name = "tipo", columnList = "tipo")
})
public class Cardapio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    @Column(name = "valor", nullable = false)
    private Double valor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tipo", nullable = false)
    private TiposCardapio tipo;

    @OneToMany(mappedBy = "item")
    private Set<Pedido> pedidos = new LinkedHashSet<>();

}