package ufpi.poo.spring.bar.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ufpi.poo.spring.bar.dao.CardapioRepository;
import ufpi.poo.spring.bar.dao.ConfiguracaoRepository;
import ufpi.poo.spring.bar.dao.MesaRepository;
import ufpi.poo.spring.bar.dao.TiposCardapioRepository;
import ufpi.poo.spring.bar.dto.CardapioDto;
import ufpi.poo.spring.bar.dto.MesaDto;
import ufpi.poo.spring.bar.dto.TiposCardapioDto;
import ufpi.poo.spring.bar.misc.TotaisMesa;
import ufpi.poo.spring.bar.model.Cardapio;
import ufpi.poo.spring.bar.model.Mesa;
import ufpi.poo.spring.bar.model.TiposCardapio;

import java.util.*;

@Service
public class DadosService {

    @Autowired
    private CardapioRepository cardapioDao;
    @Autowired
    private MesaRepository mesaRepository;
    @Autowired
    private TiposCardapioRepository tiposCardapioRepository;
    @Autowired
    private ConfiguracaoService configuracaoService;

    public List<CardapioDto> getCardapio() {
        Iterable<Cardapio> cardapio = cardapioDao.findAll();
        List<CardapioDto> cardapioDto = new ArrayList<>();
        for (var c : cardapio) {
            if (c.getAtivado() && c.getTipo().getAtivado())
                cardapioDto.add(CardapioDto.fromCardapio(c));
        }
        return cardapioDto;
    }

    public TotaisMesa calcularTotaisMesa(Mesa mesa) {
        Double precoIngresso = configuracaoService.getConfiguracaoAtual().getValorCouvert();

        if (mesa == null) return null;

        Set<MesaDto.PagamentoDto> pagamentosDto = new HashSet<>();
        double totalPagoCalculado = 0.0;
        if (mesa.getPagamentos() != null) {
            for (var p : mesa.getPagamentos()) {
                if (mesa.getHoraAberta() != null && p.getHora().isAfter(mesa.getHoraAberta())) {
                    pagamentosDto.add(new MesaDto.PagamentoDto(p.getId(), p.getValor(), p.getHora()));
                    totalPagoCalculado += p.getValor();
                }
            }
        }

        Set<MesaDto.PedidoDto> pedidosDto = new HashSet<>();
        double subtotalCalculado = 0.0;
        double gorjetaCalculada = 0.0;

        if (mesa.getPedidos() != null) {
            for (var p : mesa.getPedidos()) {
                if (mesa.getHoraAberta() != null && p.getHora().isAfter(mesa.getHoraAberta()) && p.getCancelamento() == null) {
                    Double valorEfetivo = (p.getValorFechado() != null) ? p.getValorFechado() : p.getItem().getValor();
                    Double percGorjeta = (p.getItem().getTipo().getPercGorjeta() != null) ? p.getItem().getTipo().getPercGorjeta() : 0.0;

                    double valorItemTotal = valorEfetivo * p.getQuant();
                    subtotalCalculado += valorItemTotal;
                    gorjetaCalculada += valorItemTotal * (percGorjeta / 100.0);

                    pedidosDto.add(new MesaDto.PedidoDto(
                            p.getId(), p.getItem().getId(), p.getItem().getNome(), valorEfetivo,
                            p.getItem().getTipo().getId(), p.getItem().getTipo().getNome(), percGorjeta,
                            p.getQuant(), p.getHora()
                    ));
                }
            }
        }

        double entradaCalculada = 0.0;
        if (mesa.getPagaEntrada() != null && mesa.getPagaEntrada()) {
            double valorUnitario = (precoIngresso != null) ? precoIngresso : 0.0;
            entradaCalculada = valorUnitario * (mesa.getNPessoas() != null ? mesa.getNPessoas() : 1);
        }

        // Trunca para 2 casas decimais
        subtotalCalculado = Math.floor(subtotalCalculado * 100) / 100;
        gorjetaCalculada = Math.floor(gorjetaCalculada * 100) / 100;
        entradaCalculada = Math.floor(entradaCalculada * 100) / 100;
        totalPagoCalculado = Math.floor(totalPagoCalculado * 100) / 100;

        double totalCalculado = subtotalCalculado + gorjetaCalculada + entradaCalculada - totalPagoCalculado;

        totalCalculado = Math.floor(totalCalculado * 100) / 100;

        return new TotaisMesa(
                subtotalCalculado,
                gorjetaCalculada,
                entradaCalculada,
                totalPagoCalculado,
                totalCalculado,
                pagamentosDto,
                pedidosDto
        );
    }

    /**
     * Método principal para converter Mesa -> MesaDto.
     * Agora busca o preço do ingresso no banco para passar ao DTO.
     */
    public MesaDto getMesa(Mesa mesa) {
        // Busca o preço da Configuração, não do Item
        TotaisMesa totais = calcularTotaisMesa(mesa);

        return new MesaDto(
                mesa.getId(),
                mesa.getAtivado(),
                mesa.getEstado(),
                mesa.getPagaEntrada(),
                mesa.getNPessoas(),
                mesa.getCapacidade(),
                mesa.getHoraAberta(),
                totais.pagamentosDtos(),
                totais.pedidosDtos(),
                totais.subtotal(),
                totais.gorjeta(),
                totais.entrada(),
                totais.totalPago(),
                totais.total()
        );
    }

    public Optional<MesaDto> getMesa(Integer id) {
        Optional<Mesa> mesaOpt = mesaRepository.findById(id);
        return mesaOpt.map(this::getMesa);
    }

    public List<Cardapio> getCardapioAll() {
        return cardapioDao.findAll();
    }

    public List<TiposCardapio> getTiposCardapioAll() {
        return tiposCardapioRepository.findAll();
    }
}