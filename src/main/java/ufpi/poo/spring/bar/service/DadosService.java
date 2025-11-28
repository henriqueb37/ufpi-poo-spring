package ufpi.poo.spring.bar.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ufpi.poo.spring.bar.dao.CardapioRepository;
import ufpi.poo.spring.bar.dao.MesaRepository;
import ufpi.poo.spring.bar.dao.TiposCardapioRepository;
import ufpi.poo.spring.bar.dto.CardapioDto;
import ufpi.poo.spring.bar.dto.MesaDto;
import ufpi.poo.spring.bar.dto.TiposCardapioDto;
import ufpi.poo.spring.bar.model.Cardapio;
import ufpi.poo.spring.bar.model.Mesa;
import ufpi.poo.spring.bar.model.TiposCardapio;

import java.util.*;

@Service
public class DadosService {
    @Autowired
    CardapioRepository cardapioDao;
    @Autowired
    private MesaRepository mesaRepository;
    @Autowired
    private TiposCardapioRepository tiposCardapioRepository;

    public List<CardapioDto> getCardapio() {
        Iterable<Cardapio> cardapio = cardapioDao.findAll();
        List<CardapioDto> cardapioDto = new ArrayList<>();
        for (var c : cardapio) {
            if (c.getAtivado() && c.getTipo().getAtivado())
                cardapioDto.add(CardapioDto.fromCardapio(c));
        }
        return cardapioDto;
    }

    public MesaDto getMesa(Mesa mesa) {
        double subtotal = 0;
        double gorjeta = 0;
        double entrada = 0;
        double jaPago = 0;
        double total = 0;
        Set<MesaDto.PagamentoDto> pagamentos = new HashSet<>();
        for (var p : mesa.getPagamentos()) {
            if (mesa.getHoraAberta() != null && p.getHora().isAfter(mesa.getHoraAberta())) {
                pagamentos.add(new MesaDto.PagamentoDto(p.getId(), p.getValor(), p.getHora()));
                jaPago += p.getValor();
            }
        }
        Set<MesaDto.PedidoDto> pedidos = new HashSet<>();
        for (var p : mesa.getPedidos()) {
            if (p.getCancelamento() == null && mesa.getHoraAberta() != null && p.getHora().isAfter(mesa.getHoraAberta())) {
                MesaDto.PedidoDto pedidoDto = new MesaDto.PedidoDto(
                        p.getId(),
                        p.getItem().getId(),
                        p.getItem().getNome(),
                        p.getValorFechado() == null
                                ? p.getItem().getValor()
                                : p.getValorFechado(),
                        p.getItem().getTipo().getId(),
                        p.getItem().getTipo().getNome(),
                        p.getItem().getTipo().getPercGorjeta(),
                        p.getQuant(),
                        p.getHora()
                );
                pedidos.add(pedidoDto);
                subtotal += pedidoDto.getItemValor() * pedidoDto.getQuant();
                gorjeta += pedidoDto.getItemValor() * pedidoDto.getQuant() * pedidoDto.getItemTipoPercGorjeta();
            }
        }
        // FIXME: Pegar o valor da entrada de algum lugar do banco de dados, em vez de usar um valor fixo.
        entrada = mesa.getPagaEntrada() ? mesa.getNPessoas() * 50 : 0;
        total = subtotal + gorjeta + entrada - jaPago;
        return new MesaDto(
                mesa.getId(),
                mesa.getEstado(),
                mesa.getPagaEntrada(),
                mesa.getNPessoas(),
                mesa.getHoraAberta(),
                pagamentos,
                pedidos,
                subtotal,
                gorjeta,
                entrada,
                jaPago,
                total
        );
    }

    public Optional<MesaDto> getMesa(Integer id) {
        Optional<Mesa> mesaOpt = mesaRepository.findById(id);
        if (mesaOpt.isEmpty()) {
            return Optional.empty();
        }
        Mesa mesa = mesaOpt.get();
        return Optional.of(getMesa(mesa));
    }

    public List<CardapioDto> getCardapioAll() {
        List<Cardapio> cardapioList = cardapioDao.findAll();
        List<CardapioDto> cardapioDtos = new ArrayList<>();
        for (var item : cardapioList) {
            cardapioDtos.add(new CardapioDto(
                    item.getId(),
                    item.getAtivado(),
                    item.getNome(),
                    item.getValor(),
                    item.getTipo().getId(),
                    item.getTipo().getNome(),
                    item.getTipo().getPercGorjeta()
            ));
        }
        return cardapioDtos;
    }

    public List<TiposCardapioDto> getTiposCardapioAll() {
        List<TiposCardapio> tiposCardapios = tiposCardapioRepository.findAll();
        List<TiposCardapioDto> tiposCardapioDtos = new ArrayList<>();
        for (var tc : tiposCardapios) {
            tiposCardapioDtos.add(new TiposCardapioDto(tc.getId(), tc.getNome(), tc.getPercGorjeta(), tc.getAtivado()));
        }
        return tiposCardapioDtos;
    }
}
