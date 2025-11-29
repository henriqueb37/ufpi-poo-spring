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

    public static MesaDto fromMesa(Mesa mesa, Double precoIngresso) {
        if (mesa == null) return null;

        // ... (Mantenha a lógica de cálculo de pagamentos e pedidos IGUAL) ...
        // Vou resumir aqui para não ficar gigante, mas mantenha o código de loop que já existia

        Set<PagamentoDto> pagamentosDto = new HashSet<>();
        double totalPagoCalculado = 0.0;
        if (mesa.getPagamentos() != null) {
            for (var p : mesa.getPagamentos()) {
                pagamentosDto.add(new PagamentoDto(p.getId(), p.getValor(), p.getHora()));
                totalPagoCalculado += p.getValor();
            }
        }

        Set<PedidoDto> pedidosDto = new HashSet<>();
        double subtotalCalculado = 0.0;
        double gorjetaCalculada = 0.0;

        if (mesa.getPedidos() != null) {
            for (var p : mesa.getPedidos()) {
                if (p.getCancelamento() == null) {
                    Double valorEfetivo = (p.getValorFechado() != null) ? p.getValorFechado() : p.getItem().getValor();
                    Double percGorjeta = (p.getItem().getTipo().getPercGorjeta() != null) ? p.getItem().getTipo().getPercGorjeta() : 0.0;

                    double valorItemTotal = valorEfetivo * p.getQuant();
                    subtotalCalculado += valorItemTotal;
                    gorjetaCalculada += valorItemTotal * (percGorjeta / 100.0);

                    pedidosDto.add(new PedidoDto(
                            p.getId(), p.getItem().getId(), p.getItem().getNome(), valorEfetivo,
                            p.getItem().getTipo().getId(), p.getItem().getTipo().getNome(), percGorjeta,
                            p.getQuant(), p.getHora()
                    ));
                }
            }
        }

        // Calcula a entrada baseada no preço recebido e no número de pessoas
        double entradaCalculada = 0.0;
        if (mesa.getPagaEntrada() != null && mesa.getPagaEntrada()) {
            // Se o preço for nulo (não achou item), usa 0.0
            double valorUnitario = (precoIngresso != null) ? precoIngresso : 0.0;
            entradaCalculada = valorUnitario * (mesa.getNPessoas() != null ? mesa.getNPessoas() : 1);
        }

        double totalCalculado = subtotalCalculado + gorjetaCalculada + entradaCalculada;

        return new MesaDto(
                mesa.getId(),
                mesa.getEstado(),
                mesa.getPagaEntrada(),
                mesa.getNPessoas(),
                mesa.getCapacidade(),
                mesa.getHoraAberta(),
                pagamentosDto,
                pedidosDto,
                subtotalCalculado,
                gorjetaCalculada,
                entradaCalculada, // Agora vai com o valor certo!
                totalPagoCalculado,
                totalCalculado
        );
    }
}