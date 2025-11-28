package ufpi.poo.spring.bar.views;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ufpi.poo.spring.bar.dao.CardapioRepository;
import ufpi.poo.spring.bar.dao.MesaRepository;
import ufpi.poo.spring.bar.dao.PagamentoRepository;
import ufpi.poo.spring.bar.dto.CardapioDto;
import ufpi.poo.spring.bar.dto.MesaDto;
import ufpi.poo.spring.bar.dto.TiposCardapioDto;
import ufpi.poo.spring.bar.misc.MesaEstados;
import ufpi.poo.spring.bar.model.Mesa;
import ufpi.poo.spring.bar.service.BarService;
import ufpi.poo.spring.bar.service.DadosService;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class IndexController {
    @Autowired
    CardapioRepository cardapioDao;
    @Autowired
    MesaRepository mesaDao;
    @Autowired
    private DadosService dadosService;
    @Autowired
    private BarService barService;
    @Autowired
    private PagamentoRepository pagamentoRepository;

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
                // Usa o serviço do seu amigo para converter
                mesaDto.add(MesaDto.fromMesa(m));
        }
        model.addAttribute("mesas", mesaDto);
        return "mesas";
    }

    @GetMapping("/mesas/{id}")
    public String getDetalhesMesa(Model model, @PathVariable Integer id) {
        Optional<Mesa> mesa = mesaDao.findById(id);
        if (mesa.isPresent()) {
            // Usa o DTO atualizado
            model.addAttribute("mesa", MesaDto.fromMesa(mesa.get()));
            return "mesa-detalhe";
        }
        return "error/404";
    }

    @GetMapping("/mesas/{id}/adicionar")
    public String adicionarItem(Model model, @PathVariable Integer id) {
        Optional<Mesa> mesa = mesaDao.findById(id);
        // Verifica se existe, está ativa e OCUPADA (usando o Enum correto)
        if (mesa.isPresent() && mesa.get().getAtivado() && mesa.get().getEstado() == MesaEstados.OCUPADA.getLabel()) {
            model.addAttribute("mesa", MesaDto.fromMesa(mesa.get()));
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

    @GetMapping("/admin/cardapio")
    public String gerenciarCardapio(Model model) {
        // 1. Busque todos os tipos
        List<TiposCardapioDto> tipos = dadosService.getTiposCardapioAll();

        // 2. Busque todos os itens
        List<CardapioDto> itens = dadosService.getCardapioAll();

        // 3. Agrupe-os em um Map para facilitar a visualização
        Map<TiposCardapioDto, List<CardapioDto>> map = new LinkedHashMap<>();

        for (TiposCardapioDto tipo : tipos) {
            List<CardapioDto> itensDoTipo = itens.stream()
                    .filter(i -> i.getTipoId().equals(tipo.getId()))
                    .collect(Collectors.toList());

            map.put(tipo, itensDoTipo);
        }

        model.addAttribute("cardapioMap", map);

        return "admin-cardapio";
    }

    // --- REMOVIDO: @GetMapping("/admin/mesas") ---
    // Este método foi removido porque agora quem cuida dessa rota
    // é o MesaAdminController que criamos.
}