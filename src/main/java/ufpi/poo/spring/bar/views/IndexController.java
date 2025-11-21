package ufpi.poo.spring.bar.views;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ufpi.poo.spring.bar.dao.CardapioRepository;
import ufpi.poo.spring.bar.dto.CardapioDto;

import java.util.HashSet;
import java.util.Set;

@Controller
public class IndexController {
    @Autowired
    CardapioRepository cardapioDao;

    @GetMapping("/")
    public String paginaInicial() {
        return "index";
    }

    @GetMapping("/cardapio")
    public String paginaCardapio(Model model) {
        Set<CardapioDto> cardapioDtoSet = new HashSet<>();
        for (var item : cardapioDao.findAll()) {
            cardapioDtoSet.add(CardapioDto.fromCardapio(item));
        }
        model.addAttribute("cardapio", cardapioDtoSet);

        return "cardapio";
    }
}
