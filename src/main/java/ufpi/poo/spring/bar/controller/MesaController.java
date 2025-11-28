package ufpi.poo.spring.bar.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ufpi.poo.spring.bar.dao.MesaRepository;
import ufpi.poo.spring.bar.dto.MesaDto;
import ufpi.poo.spring.bar.model.Mesa;
import ufpi.poo.spring.bar.service.BarService;

@Controller
@RequestMapping("/mesas")
public class MesaController {

    @Autowired private BarService barService;
    @Autowired private MesaRepository mesaRepository;

    // OBS: Os métodos GET (listar e detalhes) foram removidos daqui
    // pois já estão no IndexController do seu amigo.

    // --- AÇÕES OPERACIONAIS (POST) ---

    // 1. Abrir Mesa
    @PostMapping("/{id}/abrir")
    @PreAuthorize("hasAnyRole('GARCOM', 'ADMIN')")
    public String abrirMesa(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            barService.abrirMesa(id);
            ra.addFlashAttribute("sucesso", "Mesa aberta com sucesso!");
        } catch (Exception e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/mesas/" + id;
    }

    // 2. Adicionar Item (Ação do Formulário)
    @PostMapping("/{id}/adicionar")
    @PreAuthorize("hasAnyRole('GARCOM', 'ADMIN')")
    public String adicionarPedido(@PathVariable Integer id,
                                  @RequestParam Integer idItem,
                                  @RequestParam Integer quantidade,
                                  RedirectAttributes ra) {
        try {
            barService.adicionarPedido(id, idItem, quantidade);
            ra.addFlashAttribute("sucesso", "Item adicionado!");
        } catch (Exception e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/mesas/" + id;
    }

    // 3. Fechar Conta (Muda estado para 2)
    @PostMapping("/{id}/fechar")
    @PreAuthorize("hasAnyRole('GARCOM', 'ADMIN')")
    public String fecharMesa(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            barService.fecharMesa(id);
            ra.addFlashAttribute("sucesso", "Conta fechada. Aguardando pagamento.");
        } catch (Exception e) {
            ra.addFlashAttribute("erro", "Erro ao fechar: " + e.getMessage());
        }
        return "redirect:/mesas/" + id;
    }

    // 4. Liberar Mesa (Muda estado para 0 - Se pago)
    @PostMapping("/{id}/liberar")
    @PreAuthorize("hasAnyRole('GARCOM', 'ADMIN')")
    public String liberarMesa(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            barService.liberarMesa(id);
            ra.addFlashAttribute("sucesso", "Mesa liberada!");
        } catch (Exception e) {
            ra.addFlashAttribute("erro", "Erro ao liberar: " + e.getMessage());
        }
        return "redirect:/mesas/" + id;
    }

    // 5. Toggle Entrada (Cobrar/Isentar)
    @PostMapping("/{id}/toggle-entrada")
    @PreAuthorize("hasAnyRole('GARCOM', 'ADMIN')")
    public String toggleEntrada(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            Mesa mesa = barService.buscarMesaPorId(id);
            mesa.setPagaEntrada(!mesa.getPagaEntrada());
            mesaRepository.save(mesa);
            ra.addFlashAttribute("sucesso", "Status de entrada atualizado.");
        } catch (Exception e) {
            ra.addFlashAttribute("erro", "Erro ao alterar entrada.");
        }
        return "redirect:/mesas/" + id;
    }

    // --- PAGAMENTO (GET e POST) ---
    // Este o seu amigo NÃO fez no IndexController, então mantemos aqui.

    @GetMapping("/{id}/pagamento")
    @PreAuthorize("hasAnyRole('GARCOM', 'ADMIN')")
    public String formPagamento(@PathVariable Integer id, Model model) {
        Mesa mesa = barService.buscarMesaPorId(id);
        // Usamos o DTO atualizado com os cálculos que acabamos de corrigir!
        model.addAttribute("mesa", MesaDto.fromMesa(mesa));
        return "pagamento-form"; // Precisa criar/verificar este HTML
    }

    @PostMapping("/{id}/pagamento")
    @PreAuthorize("hasAnyRole('GARCOM', 'ADMIN')")
    public String registrarPagamento(@PathVariable Integer id,
                                     @RequestParam Double valor,
                                     RedirectAttributes ra) {
        try {
            barService.registrarPagamento(id, valor);
            ra.addFlashAttribute("sucesso", "Pagamento de R$ " + valor + " registrado!");
        } catch (Exception e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/mesas/" + id;
    }
}