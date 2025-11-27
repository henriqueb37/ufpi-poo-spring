package ufpi.poo.spring.bar.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ufpi.poo.spring.bar.model.Mesa;
import java.util.List;


public interface MesaRepository extends JpaRepository<Mesa, Integer> {

    List<Mesa> findByAtivadoTrue();

    List<Mesa> findByEstado(Integer estado);
}