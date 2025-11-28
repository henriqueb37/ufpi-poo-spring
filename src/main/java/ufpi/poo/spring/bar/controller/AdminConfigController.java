package ufpi.poo.spring.bar.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ufpi.poo.spring.bar.service.ConfiguracaoService;
import ufpi.poo.spring.bar.dao.TiposCardapioRepository;
import ufpi.poo.spring.bar.model.TiposCardapio;

@Controller
@RequestMapping("/admin/config") // Rota base para todas as configurações
@PreAuthorize("hasRole('ADMIN')") // Segurança: Apenas ADMIN pode editar
public class AdminConfigController {

    @Autowired private ConfiguracaoService configService;
    @Autowired private TiposCardapioRepository tiposRepository;

    // --- 1. POST: SALVAR VALOR DA ENTRADA (COUVERT) ---
    // Atende à rota do formulário de entrada no admin-cardapio.html
    @PostMapping("/salvar-entrada")
    public String salvarCouvert(@RequestParam Double valorEntrada, RedirectAttributes ra) {
        try {
            configService.atualizarCouvert(valorEntrada);
            ra.addFlashAttribute("sucesso", "Valor da Entrada (Couvert) atualizado com sucesso!");
        } catch (Exception e) {
            ra.addFlashAttribute("erro", "Erro ao atualizar Couvert: " + e.getMessage());
        }
        return "redirect:/admin/cardapio"; // Redireciona de volta para a tela de listagem
    }

    // --- 2. POST: SALVAR TAXA DE GORJETA (POR CATEGORIA) ---
    // Atende à rota do formulário de taxa (dentro do loop de Bebidas/Comidas)
    @PostMapping("/salvar-taxa")
    public String salvarTaxaGorjeta(
            @RequestParam Integer id,
            @RequestParam Double percGorjeta,
            RedirectAttributes ra) {
        try {
            // Buscamos a entidade do Tipo (Comida, Bebida) para atualizar apenas o percentual
            TiposCardapio tipo = tiposRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Tipo de cardápio não encontrado."));

            if (percGorjeta == null || percGorjeta < 0) {
                throw new IllegalArgumentException("Percentual inválido.");
            }

            tipo.setPercGorjeta(percGorjeta);
            tiposRepository.save(tipo);

            ra.addFlashAttribute("sucesso", "Taxa de gorjeta para " + tipo.getNome() + " atualizada!");
        } catch (Exception e) {
            ra.addFlashAttribute("erro", "Erro ao salvar taxa: " + e.getMessage());
        }
        return "redirect:/admin/cardapio";
    }
}