package ufpi.poo.spring.bar.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "pagamentos", schema = "barspring", indexes = {
        @Index(name = "id_mesa", columnList = "id_mesa")
})
public class Pagamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_mesa", nullable = false)
    private Mesa idMesa;

    @Column(name = "valor", nullable = false)
    private Double valor;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "hora")
    private Instant hora;

}