package ufpi.poo.spring.bar.dto;

import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link ufpi.poo.spring.bar.model.TiposCardapio}
 */
@Value
public class TiposCardapioDto implements Serializable {
    Integer id;
    String nome;
    Double percGorjeta;
    Boolean ativado;
}