package ufpi.poo.spring.bar.misc;

import ufpi.poo.spring.bar.dto.MesaDto;

import java.util.Set;

public record TotaisMesa(
        Double subtotal,
        Double gorjeta,
        Double entrada,
        Double totalPago,
        Double total,
        Set<MesaDto.PagamentoDto> pagamentosDtos,
        Set<MesaDto.PedidoDto> pedidosDtos
) {}
