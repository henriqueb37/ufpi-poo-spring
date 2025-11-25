package ufpi.poo.spring.bar.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.security.core.userdetails.UserDetails;
import ufpi.poo.spring.bar.misc.CargoTipos;
import ufpi.poo.spring.bar.model.Usuario;

public interface UsuarioRepository extends CrudRepository<Usuario, Integer> {
    int countByCargo(CargoTipos cargo);

    boolean existsByEmail(String email);

    UserDetails findByEmail(String email);
}