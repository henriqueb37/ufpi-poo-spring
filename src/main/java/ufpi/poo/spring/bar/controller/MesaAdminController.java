package ufpi.poo.spring.bar.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ufpi.poo.spring.bar.dao.CardapioRepository;
import ufpi.poo.spring.bar.dao.MesaRepository; // Mantenha se for usar validações extras
import ufpi.poo.spring.bar.dto.MesaDto;
import ufpi.poo.spring.bar.model.Cardapio;
import ufpi.poo.spring.bar.model.Mesa;
import ufpi.poo.spring.bar.service.BarService;
import ufpi.poo.spring.bar.service.DadosService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/mesas") // Rota exclusiva do Admin
@PreAuthorize("hasRole('ADMIN')")
public class MesaAdminController {

    @Autowired private BarService barService;
    @Autowired private CardapioRepository cardapioRepository; // Necessário para buscar o preço do ingresso
    @Autowired
    private DadosService dadosService;

    // --- MÉTODOS DE VISUALIZAÇÃO E CRIAÇÃO ---

    // GET: Carrega a tela de gerenciamento com a lista de mesas
    @GetMapping
    public String gerenciarMesas(Model model) {
        List<MesaDto> mesas = barService.listarTodasMesas().stream()
                .map(m -> dadosService.getMesa(m))
                .collect(Collectors.toList());

        model.addAttribute("mesas", mesas);

        // Manda um objeto vazio para o formulário de criação preencher
        model.addAttribute("novaMesa", new Mesa());

        return "admin-mesas"; // Renderiza o template admin-mesas.html
    }

    // POST: Rota para CRIAR MESA
    @PostMapping("/criar")
    public String criarMesa(
            @RequestParam("idMesa") Integer idMesa,
            @RequestParam("capacidade") Integer capacidade,
            RedirectAttributes ra) {
        try {
            barService.criarNovaMesa(idMesa, capacidade);
            ra.addFlashAttribute("sucesso", "Mesa " + idMesa + " cadastrada com sucesso!");
        } catch (Exception e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/admin/mesas";
    }
}