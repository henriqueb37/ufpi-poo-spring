package ufpi.poo.spring.bar.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "pedidos", schema = "barspring", indexes = {
        @Index(name = "id_item", columnList = "id_item"),
        @Index(name = "id_mesa", columnList = "id_mesa")
})
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_item", nullable = false)
    private Cardapio item;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_mesa", nullable = false)
    private Mesa mesa;

    @Column(name = "quant", nullable = false)
    private Integer quant;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "hora")
    private Instant hora;

    @Column(name = "cancelamento", length = 500)
    private String cancelamento;

}