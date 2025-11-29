package ufpi.poo.spring.bar.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "mesas", schema = "barspring")
public class Mesa {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "estado", nullable = false)
    private Integer estado;

    @ColumnDefault("0")
    @Column(name = "paga_entrada")
    private Boolean pagaEntrada = false;

    @ColumnDefault("1")
    @Column(name = "n_pessoas")
    private Integer nPessoas = 1;

    @Column(name = "hora_aberta")
    private Instant horaAberta;

    @OneToMany(mappedBy = "idMesa")
    private Set<Pagamento> pagamentos = new LinkedHashSet<>();

    @OneToMany(mappedBy = "mesa")
    private Set<Pedido> pedidos = new LinkedHashSet<>();

    @ColumnDefault("1")
    @Column(name = "ativado")
    private Boolean ativado;

    @Column(name = "capacidade")
    private Integer capacidade;

}