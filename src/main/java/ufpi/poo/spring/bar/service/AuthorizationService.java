package ufpi.poo.spring.bar.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ufpi.poo.spring.bar.dao.UsuarioRepository;

@Service
public class AuthorizationService implements UserDetailsService {

    @Autowired
    UsuarioRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Como na sua entidade Usuario você definiu que getUsername() retorna o email,
        // aqui nós buscamos pelo email.
        UserDetails user = repository.findByEmail(username);

        if (user == null) {
            throw new UsernameNotFoundException("Usuário não encontrado!");
        }

        return user;
    }
}
