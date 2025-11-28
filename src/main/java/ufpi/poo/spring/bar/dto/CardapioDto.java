package ufpi.poo.spring.bar.dto;

import lombok.NonNull;
import lombok.Value;
import ufpi.poo.spring.bar.model.Cardapio;

import java.io.Serializable;

/**
 * DTO for {@link ufpi.poo.spring.bar.model.Cardapio}
 */
@Value
public class CardapioDto implements Serializable {
    Integer id;
    Boolean ativado;
    String nome;
    Double valor;
    Integer tipoId;
    String tipoNome;
    Double tipoPercGorjeta;

    public static CardapioDto fromCardapio(@NonNull Cardapio item) {
        return new CardapioDto(
                item.getId(),
                item.getAtivado(),
                item.getNome(),
                item.getValor(),
                item.getTipo().getId(),
                item.getTipo().getNome(),
                item.getTipo().getPercGorjeta()
        );
    }
}