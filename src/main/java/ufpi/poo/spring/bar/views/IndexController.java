package ufpi.poo.spring.bar.views;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ufpi.poo.spring.bar.dao.CardapioRepository;
import ufpi.poo.spring.bar.dto.CardapioDto;
import ufpi.poo.spring.bar.model.Cardapio;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

    @GetMapping("/login-funcionario")
    public String loginFuncionario() {
        return "login";
    }

    @GetMapping("/login")
    public String loginFuncionario2() {
        return "login";
    }

//    @GetMapping("/admin/registrar-funcionario")
//    public String cadastrarFuncionario() {
//        return "cadastro";
//    }

    @GetMapping("/fragments/item_cardapio")
    public String getItensCardapio(Model model) {
        Iterable<Cardapio> cardapio = cardapioDao.findAll();
        List<CardapioDto> cardapioDto = new ArrayList<>();
        for (var c : cardapio) {
            if (c.getAtivado() && c.getTipo().getAtivado())
                cardapioDto.add(CardapioDto.fromCardapio(c));
        }
        model.addAttribute("cardapio", cardapioDto);
        return "fragments/item_cardapio :: lista-cardapio";
    }
}
