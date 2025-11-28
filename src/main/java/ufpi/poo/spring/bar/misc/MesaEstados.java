package ufpi.poo.spring.bar.misc;

import lombok.Getter;

@Getter
public enum MesaEstados {
    LIVRE(0),       // Pronta para novo cliente
    OCUPADA(1),     // Cliente consumindo (aceita pedidos)
    EM_PAGAMENTO(2); // Conta encerrada (só aceita pagamentos)

    private final int label;

    MesaEstados(int label) {
        this.label = label;
    }

    // Método auxiliar para converter int do banco para Enum (opcional, mas útil)
    public static MesaEstados fromId(int id) {
        for (MesaEstados e : values()) {
            if (e.label == id) return e;
        }
        throw new IllegalArgumentException("Estado inválido: " + id);
    }
}