package ufpi.poo.spring.bar.misc;

import lombok.Getter;

@Getter
public enum MesaEstados {
    DESOCUPADA(0),
    ABERTA(1),
    FECHADA(2);

    private final int label;
    MesaEstados(int label) {
        this.label = label;
    }

}
