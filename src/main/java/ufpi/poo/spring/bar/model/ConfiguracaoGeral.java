package ufpi.poo.spring.bar.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "configuracoes", schema = "barspring")
public class ConfiguracaoGeral {

    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "valor_couvert", nullable = false)
    private Double valorCouvert = 0.0;

    public ConfiguracaoGeral() {
        // Define o ID padrão no construtor
        this.id = 1;
    }
}