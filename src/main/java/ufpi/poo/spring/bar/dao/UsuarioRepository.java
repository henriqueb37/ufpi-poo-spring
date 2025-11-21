package ufpi.poo.spring.bar.dao;

import org.springframework.data.repository.CrudRepository;
import ufpi.poo.spring.bar.model.Usuario;

public interface UsuarioRepository extends CrudRepository<Usuario, Integer> {
}