package ufpi.poo.spring.bar.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ufpi.poo.spring.bar.misc.CargoTipos;
import ufpi.poo.spring.bar.model.Usuario;
import ufpi.poo.spring.bar.service.UsuarioService;

@Controller
@RequestMapping("/admin/usuarios")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUsuarioController {

    @Autowired private UsuarioService usuarioService;

    // 1. Listar Usuários
    @GetMapping
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioService.listarTodos());
        // Envia os tipos de cargo para o <select> do formulário
        model.addAttribute("cargos", CargoTipos.values());
        // Objeto vazio para o modal de cadastro
        model.addAttribute("novoUsuario", new Usuario());
        return "admin-usuarios";
    }

    // 2. Salvar (Criar/Editar)
    @PostMapping("/salvar")
    public String salvarUsuario(@ModelAttribute Usuario usuario, RedirectAttributes ra) {
        try {
            usuarioService.salvarUsuario(usuario);
            ra.addFlashAttribute("sucesso", "Funcionário salvo com sucesso!");
        } catch (Exception e) {
            ra.addFlashAttribute("erro", "Erro ao salvar: " + e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }

    // 3. Excluir
    @PostMapping("/excluir/{id}")
    public String excluirUsuario(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            usuarioService.excluirUsuario(id);
            ra.addFlashAttribute("sucesso", "Funcionário removido.");
        } catch (Exception e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }
}