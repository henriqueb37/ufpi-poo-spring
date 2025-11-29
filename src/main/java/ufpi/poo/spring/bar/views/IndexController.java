package ufpi.poo.spring.bar.views;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ufpi.poo.spring.bar.dao.CardapioRepository;
import ufpi.poo.spring.bar.dao.ConfiguracaoRepository; // Import adicionado
import ufpi.poo.spring.bar.dao.MesaRepository;
import ufpi.poo.spring.bar.dao.PagamentoRepository;
import ufpi.poo.spring.bar.dto.CardapioDto;
import ufpi.poo.spring.bar.dto.MesaDto;
import ufpi.poo.spring.bar.dto.TiposCardapioDto;
import ufpi.poo.spring.bar.misc.MesaEstados;
import ufpi.poo.spring.bar.model.Cardapio;
import ufpi.poo.spring.bar.model.Mesa;
import ufpi.poo.spring.bar.model.TiposCardapio;
import ufpi.poo.spring.bar.service.BarService;
import ufpi.poo.spring.bar.service.ConfiguracaoService;
import ufpi.poo.spring.bar.service.DadosService;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class IndexController {

    @Autowired
    private CardapioRepository cardapioDao;

    @Autowired
    private MesaRepository mesaDao;

    @Autowired
    private DadosService dadosService;

    @Autowired
    private BarService barService;

    @Autowired
    private PagamentoRepository pagamentoRepository;

    // Injeção nova para buscar o valor do couvert
    @Autowired
    private ConfiguracaoRepository configuracaoRepository;
    @Autowired
    private ConfiguracaoService configuracaoService;

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

    @GetMapping("/mesas")
    public String getMesas(Model model) {
        Iterable<Mesa> mesas = mesaDao.findAll();
        List<MesaDto> mesaDto = new ArrayList<>();
        for (var m : mesas) {
            if (m.getAtivado())
                mesaDto.add(dadosService.getMesa(m));
        }
        model.addAttribute("mesas", mesaDto);
        return "mesas";
    }

    @GetMapping("/mesas/{id}")
    public String getDetalhesMesa(Model model, @PathVariable Integer id) {
        Optional<Mesa> mesa = mesaDao.findById(id);
        if (mesa.isPresent()) {
            model.addAttribute("mesa", dadosService.getMesa(mesa.get()));
            return "mesa-detalhe";
        }
        return "error/404";
    }

    @GetMapping("/mesas/{id}/adicionar")
    public String adicionarItem(Model model, @PathVariable Integer id) {
        Optional<Mesa> mesa = mesaDao.findById(id);
        if (mesa.isPresent() && mesa.get().getAtivado() && mesa.get().getEstado() == MesaEstados.OCUPADA.getLabel()) {
            model.addAttribute("mesa", dadosService.getMesa(mesa.get()));
            model.addAttribute("cardapio", dadosService.getCardapio());
            return "adicionar-pedido";
        }
        return "error/404";
    }

    @GetMapping("/fragments/item_cardapio")
    public String getItensCardapio(Model model) {
        model.addAttribute("cardapio", dadosService.getCardapio());
        return "fragments/item_cardapio :: lista-cardapio";
    }

    @GetMapping("/admin/dashboard")
    public String dashboardAdmin(Model model) {
        Instant hoje = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant amanha = LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        model.addAttribute("faturamentoDia", pagamentoRepository.calcularFaturamentoPorPeriodo(hoje, amanha));
        return "admin-dashboard";
    }
}