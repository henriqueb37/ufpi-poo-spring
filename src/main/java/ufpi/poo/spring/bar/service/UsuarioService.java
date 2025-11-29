package ufpi.poo.spring.bar.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ufpi.poo.spring.bar.dao.UsuarioRepository;
import ufpi.poo.spring.bar.model.Usuario;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    // Listar todos
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    // Buscar por ID
    public Usuario buscarPorId(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
    }

    // Salvar (Cadastrar ou Editar)
    public void salvarUsuario(Usuario usuario) {
        if (usuario.getId() == null) {
            // Verifica duplicidade de email
            if (usuarioRepository.existsByEmail(usuario.getEmail())) {
                throw new RuntimeException("Já existe um usuário com este e-mail.");
            }
            String senhaCodificada = passwordEncoder.encode(usuario.getSenha());
            usuario.setSenha(senhaCodificada);
        } else {
            // Se o campo senha vier vazio do form, não muda
            // Se vier preenchido, criptografa a nova
            Usuario antigo = buscarPorId(usuario.getId());
            if (usuario.getSenha() == null || usuario.getSenha().isEmpty()) {
                usuario.setSenha(antigo.getSenha());
            } else {
                usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
            }
        }

        usuarioRepository.save(usuario);
    }

    // Excluir
    public void excluirUsuario(Integer id) {
        if (usuarioRepository.count() <= 1) {
            throw new RuntimeException("Não é possível excluir o último usuário do sistema.");
        }
        usuarioRepository.deleteById(id);
    }
}