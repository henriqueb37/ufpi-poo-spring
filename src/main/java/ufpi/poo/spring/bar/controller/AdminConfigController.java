package ufpi.poo.spring.bar.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ufpi.poo.spring.bar.dao.TiposCardapioRepository;
import ufpi.poo.spring.bar.model.TiposCardapio;
import ufpi.poo.spring.bar.service.ConfiguracaoService;

@Controller
@RequestMapping("/admin/config") // ATENÇÃO: A rota base é essa
@PreAuthorize("hasRole('ADMIN')")
public class AdminConfigController {

    @Autowired private ConfiguracaoService configService;
    @Autowired private TiposCardapioRepository tiposRepository;

    // 1. SALVAR VALOR DO COUVERT
    @PostMapping("/salvar-entrada")
    public String salvarCouvert(@RequestParam Double valorEntrada, RedirectAttributes ra) {
        try {
            configService.atualizarCouvert(valorEntrada);
            ra.addFlashAttribute("sucesso", "Valor do Couvert atualizado para R$ " + valorEntrada);
        } catch (Exception e) {
            ra.addFlashAttribute("erro", "Erro ao atualizar: " + e.getMessage());
        }
        return "redirect:/admin/cardapio";
    }

    // 2. SALVAR TAXA DE GORJETA
    @PostMapping("/salvar-taxa")
    public String salvarTaxa(@RequestParam Integer id, @RequestParam Double percGorjeta, RedirectAttributes ra) {
        try {
            TiposCardapio tipo = tiposRepository.findById(id).orElseThrow();
            tipo.setPercGorjeta(percGorjeta);
            tiposRepository.save(tipo);
            ra.addFlashAttribute("sucesso", "Taxa atualizada!");
        } catch (Exception e) {
            ra.addFlashAttribute("erro", "Erro: " + e.getMessage());
        }
        return "redirect:/admin/cardapio";
    }
}