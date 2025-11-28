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
import ufpi.poo.spring.bar.dto.MesaDto;
import ufpi.poo.spring.bar.service.BarService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/mesas")
@PreAuthorize("hasRole('ADMIN')") // Apenas Admin pode criar/deletar mesas
public class MesaAdminController {

    @Autowired private BarService barService;

    // Rota POST que o seu HTML está tentando chamar (para CRIAR MESA)
    // A rota exata deve ser '/admin/mesas/criar' ou '/admin/mesas' dependendo do HTML
    // Vou assumir que o formulário envia para a URL base /admin/mesas

    @PostMapping("/criar")
    public String criarMesa(
            @RequestParam("idMesa") Integer idMesa, // Recebe o "Número da Mesa"
            @RequestParam("capacidade") Integer capacidade, // Capacidade da mesa
            RedirectAttributes ra) {
        try {
            barService.criarNovaMesa(idMesa, capacidade);
            ra.addFlashAttribute("sucesso", "Mesa " + idMesa + " cadastrada com sucesso!");
        } catch (Exception e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/admin/mesas"; // Retorna para a tela de gerenciamento
    }

    // ... imports (adicione MesaDto e Collectors se faltar)

    // GET: Carrega a tela de gerenciamento com a lista
    @GetMapping
    public String gerenciarMesas(Model model) {
        // Reusa a lógica de listar todas as mesas
        // (Pode usar o Service ou o Repository direto aqui se for só leitura simples)
        List<MesaDto> mesas = barService.listarTodasMesas().stream()
                .map(MesaDto::fromMesa)
                .collect(Collectors.toList());

        model.addAttribute("mesas", mesas);
        return "admin-mesas";
    }
}