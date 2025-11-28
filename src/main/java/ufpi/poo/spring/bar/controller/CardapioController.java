package ufpi.poo.spring.bar.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ufpi.poo.spring.bar.dao.CardapioRepository;
import ufpi.poo.spring.bar.dao.TiposCardapioRepository;
import ufpi.poo.spring.bar.model.Cardapio;
import ufpi.poo.spring.bar.model.TiposCardapio;

import java.util.List;

@Controller
@RequestMapping("/admin/cardapio")
@PreAuthorize("hasRole('ADMIN')") // Apenas Admin pode mexer no cardápio
public class CardapioController {

    @Autowired private CardapioRepository cardapioRepository;
    @Autowired private TiposCardapioRepository tiposRepository;

    // 1. Tela de Cadastro (Novo Item)
    @GetMapping("/novo")
    public String formNovoItem(Model model) {
        // Precisamos enviar a lista de tipos (Bebida, Comida) para o <select>
        List<TiposCardapio> tipos = tiposRepository.findByAtivadoTrueOrderByNomeAsc();

        model.addAttribute("item", new Cardapio()); // Objeto vazio para o formulário
        model.addAttribute("tipos", tipos);
        model.addAttribute("titulo", "Novo Item");

        return "cardapio-form"; // Precisamos criar este HTML
    }

    // 2. Processar o Salvamento (Create/Update)
    @PostMapping("/salvar")
    public String salvarItem(@ModelAttribute Cardapio item, RedirectAttributes ra) {
        try {
            // Garante que o item está ativo ao criar
            if (item.getAtivado() == null) item.setAtivado(true);

            cardapioRepository.save(item);
            ra.addFlashAttribute("sucesso", "Item salvo com sucesso!");
        } catch (Exception e) {
            ra.addFlashAttribute("erro", "Erro ao salvar: " + e.getMessage());
        }
        return "redirect:/admin/cardapio"; // Volta para a listagem do seu amigo
    }

    // 3. Tela de Edição
    @GetMapping("/editar/{id}")
    public String formEditarItem(@PathVariable Integer id, Model model) {
        Cardapio item = cardapioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item inválido: " + id));

        List<TiposCardapio> tipos = tiposRepository.findByAtivadoTrueOrderByNomeAsc();

        model.addAttribute("item", item);
        model.addAttribute("tipos", tipos);
        model.addAttribute("titulo", "Editar Item");

        return "cardapio-form"; // Reusa o mesmo HTML
    }

    // 4. Excluir (Soft Delete)
    @PostMapping("/excluir/{id}")
    public String excluirItem(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            Cardapio item = cardapioRepository.findById(id).orElseThrow();
            // Apenas desativamos, não apagamos do banco!
            item.setAtivado(false);
            cardapioRepository.save(item);

            ra.addFlashAttribute("sucesso", "Item removido do cardápio.");
        } catch (Exception e) {
            ra.addFlashAttribute("erro", "Erro ao excluir.");
        }
        return "redirect:/admin/cardapio";
    }
}