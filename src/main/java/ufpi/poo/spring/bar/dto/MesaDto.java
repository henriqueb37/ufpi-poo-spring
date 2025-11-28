package ufpi.poo.spring.bar.dto;

import lombok.Value;

import java.io.Serializable;
import java.time.Instant;
import java.util.Set;

/**
 * DTO for {@link ufpi.poo.spring.bar.model.Mesa}
 */
@Value
public class MesaDto implements Serializable {
    Integer id;
    Integer estado;
    Boolean pagaEntrada;
    Integer nPessoas;
    Instant horaAberta;
    Set<PagamentoDto> pagamentos;
    Set<PedidoDto> pedidos;
    Double subtotal;
    Double gorjeta;
    Double entrada;
    Double totalPago;
    Double total;

    /**
     * DTO for {@link ufpi.poo.spring.bar.model.Pagamento}
     */
    @Value
    public static class PagamentoDto implements Serializable {
        Integer id;
        Double valor;
        Instant hora;
    }

    /**
     * DTO for {@link ufpi.poo.spring.bar.model.Pedido}
     */
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