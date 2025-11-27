package ufpi.poo.spring.bar.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ufpi.poo.spring.bar.dao.CardapioRepository;
import ufpi.poo.spring.bar.dto.CardapioDto;
import ufpi.poo.spring.bar.model.Cardapio;

import java.util.ArrayList;
import java.util.List;

@Service
public class DadosService {
    @Autowired
    CardapioRepository cardapioDao;

    public List<CardapioDto> getCardapio() {
        Iterable<Cardapio> cardapio = cardapioDao.findAll();
        List<CardapioDto> cardapioDto = new ArrayList<>();
        for (var c : cardapio) {
            if (c.getAtivado() && c.getTipo().getAtivado())
                cardapioDto.add(CardapioDto.fromCardapio(c));
        }
        return cardapioDto;
    }
}
