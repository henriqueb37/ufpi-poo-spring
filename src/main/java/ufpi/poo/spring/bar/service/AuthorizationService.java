package ufpi.poo.spring.bar.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ufpi.poo.spring.bar.dao.UsuarioRepository;
import ufpi.poo.spring.bar.model.Usuario;

import java.util.Optional;

@Service
public class AuthorizationService implements UserDetailsService {

    @Autowired
    UsuarioRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Como na sua entidade Usuario você definiu que getUsername() retorna o email,
        // aqui nós buscamos pelo email.
        Optional<Usuario> user = repository.findByEmail(username);

        if (user.isEmpty()) {
            throw new UsernameNotFoundException("Usuário não encontrado!");
        }

        return user.get();
    }
}
