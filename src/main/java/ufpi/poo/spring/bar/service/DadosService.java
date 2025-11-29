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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DadosService {

    @Autowired
    private CardapioRepository cardapioDao;

    @Autowired
    private MesaRepository mesaRepository;

    @Autowired
    private TiposCardapioRepository tiposCardapioRepository;

    @Autowired
    private ufpi.poo.spring.bar.dao.ConfiguracaoRepository configuracaoRepository;

    public List<CardapioDto> getCardapio() {
        Iterable<Cardapio> cardapio = cardapioDao.findAll();
        List<CardapioDto> cardapioDto = new ArrayList<>();
        for (var c : cardapio) {
            if (c.getAtivado() && c.getTipo().getAtivado())
                cardapioDto.add(CardapioDto.fromCardapio(c));
        }
        return cardapioDto;
    }

    /**
     * Método principal para converter Mesa -> MesaDto.
     * Agora busca o preço do ingresso no banco para passar ao DTO.
     */
    public MesaDto getMesa(Mesa mesa) {
        // Busca o preço da Configuração, não do Item
        Double precoIngresso = configuracaoRepository.findById(1)
                .map(c -> c.getValorCouvert())
                .orElse(0.0);

        return MesaDto.fromMesa(mesa, precoIngresso);
    }

    public Optional<MesaDto> getMesa(Integer id) {
        Optional<Mesa> mesaOpt = mesaRepository.findById(id);
        if (mesaOpt.isEmpty()) {
            return Optional.empty();
        }
        // Reusa o método acima para garantir consistência
        return Optional.of(getMesa(mesaOpt.get()));
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