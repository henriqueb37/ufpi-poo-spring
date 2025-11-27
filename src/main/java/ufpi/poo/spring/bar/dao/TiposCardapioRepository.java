package ufpi.poo.spring.bar.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ufpi.poo.spring.bar.model.TiposCardapio;
import java.util.List;

public interface TiposCardapioRepository extends JpaRepository<TiposCardapio, Integer> {

    // Lista tipos ativos ordenados por nome
    List<TiposCardapio> findByAtivadoTrueOrderByNomeAsc();
}