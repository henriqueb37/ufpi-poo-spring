package ufpi.poo.spring.bar.views;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ufpi.poo.spring.bar.dao.CardapioRepository;
import ufpi.poo.spring.bar.dao.MesaRepository;
import ufpi.poo.spring.bar.dto.CardapioDto;
import ufpi.poo.spring.bar.dto.MesaDto;
import ufpi.poo.spring.bar.model.Cardapio;
import ufpi.poo.spring.bar.model.Mesa;

import java.util.*;

@Controller
public class IndexController {
    @Autowired
    CardapioRepository cardapioDao;

    @Autowired
    MesaRepository mesaDao;

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

    @GetMapping("/mesas")
    public String getMesas(Model model) {
        Iterable<Mesa> mesas = mesaDao.findAll();
        List<MesaDto> mesaDto = new ArrayList<>();
        for (var m : mesas) {
            if (m.getAtivado())
                mesaDto.add(MesaDto.fromMesa(m));
        }
        model.addAttribute("mesas", mesas);
        return "mesas";
    }

    @GetMapping("/mesas/{id}")
    public String getDetalhesMesa(Model model, @PathVariable Integer id) {
        Optional<Mesa> mesa = mesaDao.findById(id);
        if (mesa.isPresent()) {
            model.addAttribute("mesa", MesaDto.fromMesa(mesa.get()));
            return "mesa-detalhe";
        }
        return "error/404";
    }

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
