package ufpi.poo.spring.bar.dto;

import lombok.NonNull;
import lombok.Value;
import ufpi.poo.spring.bar.model.Mesa;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
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

    public static MesaDto fromMesa(@NonNull Mesa mesa) {
        Set<MesaDto.PagamentoDto> pagamentos = new HashSet<>();
        for (var p : mesa.getPagamentos()) {
            pagamentos.add(new MesaDto.PagamentoDto(p.getId(), p.getValor(), p.getHora()));
        }
        Set<MesaDto.PedidoDto> pedidos = new HashSet<>();
        for (var p : mesa.getPedidos()) {
            pedidos.add(new MesaDto.PedidoDto(
                    p.getId(),
                    p.getItem().getId(),
                    p.getItem().getNome(),
                    p.getItem().getValor(),
                    p.getItem().getTipo().getId(),
                    p.getItem().getTipo().getNome(),
                    p.getItem().getTipo().getPercGorjeta(),
                    p.getQuant(),
                    p.getHora()
            ));
        }
        return new MesaDto(
                mesa.getId(),
                mesa.getEstado(),
                mesa.getPagaEntrada(),
                mesa.getNPessoas(),
                mesa.getHoraAberta(),
                pagamentos,
                pedidos
        );
    }
}