package ufpi.poo.spring.bar.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ufpi.poo.spring.bar.model.Usuario;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    // Método mágico que o Spring Data implementa sozinho.
    // Ele vai gerar: SELECT * FROM usuarios WHERE email = ?
    Optional<Usuario> findByEmail(String email);

    // Verifica se já existe (útil para não criar usuários duplicados)
    boolean existsByEmail(String email);

    // Útil para o setup inicial (contar quantos admins existem)
    long countByCargo(ufpi.poo.spring.bar.misc.CargoTipos cargo);
}