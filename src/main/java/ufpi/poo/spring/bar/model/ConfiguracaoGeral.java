package ufpi.poo.spring.bar.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
// CORREÇÃO 1: Adicionado o schema explicitamente para evitar erro de tabela não encontrada
@Table(name = "configuracoes", schema = "barspring")
public class ConfiguracaoGeral {

    @Id
    @Column(name = "id", nullable = false)
    // CORREÇÃO 2: Removido 'final' e a inicialização direta.
    // O valor 1 será garantido pelo banco ou pelo serviço.
    private Integer id;

    @Column(name = "valor_couvert", nullable = false)
    private Double valorCouvert = 0.0;

    @Column(name = "perc_gorjeta_comida", nullable = false)
    private Double percGorjetaComida = 15.0;

    @Column(name = "perc_gorjeta_bebida", nullable = false)
    private Double percGorjetaBebida = 10.0;

    public ConfiguracaoGeral() {
        // Define o ID padrão no construtor
        this.id = 1;
    }
}