package ufpi.poo.spring.bar.dto;

import lombok.Value;
import ufpi.poo.spring.bar.model.Mesa;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Value
public class MesaDto implements Serializable {
    Integer id;
    Boolean ativado;
    Integer estado;
    Boolean pagaEntrada;
    Integer nPessoas;     // Pessoas sentadas agora
    Integer capacidade;   // <--- NOVO CAMPO (Fixo da mesa)
    Instant horaAberta;
    Set<PagamentoDto> pagamentos;
    Set<PedidoDto> pedidos;
    Double subtotal;
    Double gorjeta;
    Double entrada;
    Double totalPago;
    Double total;

    // ... (Mantenha as classes internas PagamentoDto e PedidoDto iguais) ...
    @Value
    public static class PagamentoDto implements Serializable {
        Integer id;
        Double valor;
        Instant hora;
    }

    @Value
    public static class PedidoDto implements Serializable {
        Integer id;
        Integer itemId;
        String itemNome;
        Double itemValor;
        Integer itemTipoId;
        String itemTipoNome;
        Double itemTipoPercGorjeta;
        Integer quant;
        Instant hora;
    }
}