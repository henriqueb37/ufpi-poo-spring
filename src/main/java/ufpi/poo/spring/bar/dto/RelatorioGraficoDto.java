package ufpi.poo.spring.bar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RelatorioGraficoDto {
    private String label;  // Nome do item
    private Number value;  // Quantidade ou Valor
}