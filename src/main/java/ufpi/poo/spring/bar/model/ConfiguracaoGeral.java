package ufpi.poo.spring.bar.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "configuracoes")
public class ConfiguracaoGeral {

    // Usamos um ID fixo (1) para garantir que sempre haverá apenas uma linha de configuração
    @Id
    @Column(name = "id", nullable = false)
    private final Integer id = 1;

    // Valor do Couvert (Preço de Entrada)
    @Column(name = "valor_couvert", nullable = false)
    private Double valorCouvert = 0.0;

    // Percentual de Gorjeta para Comida (15% padrão)
    @Column(name = "perc_gorjeta_comida", nullable = false)
    private Double percGorjetaComida = 15.0;

    // Percentual de Gorjeta para Bebida (10% padrão)
    @Column(name = "perc_gorjeta_bebida", nullable = false)
    private Double percGorjetaBebida = 10.0;

    // Construtor padrão necessário para JPA
    public ConfiguracaoGeral() {}
}