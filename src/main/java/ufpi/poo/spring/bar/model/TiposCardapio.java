package ufpi.poo.spring.bar.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "tipos_cardapio", schema = "barspring")
public class TiposCardapio {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Column(name = "perc_gorjeta", nullable = false)
    private Double percGorjeta;

    @OneToMany(mappedBy = "tipo")
    private Set<Cardapio> cardapios = new LinkedHashSet<>();

    @ColumnDefault("1")
    @Column(name = "ativado")
    private Boolean ativado;

}