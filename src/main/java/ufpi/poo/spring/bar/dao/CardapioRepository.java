package ufpi.poo.spring.bar.dao;

import org.springframework.data.repository.CrudRepository;
import ufpi.poo.spring.bar.model.Cardapio;

import java.util.List;

public interface CardapioRepository extends CrudRepository<Cardapio, Integer> {
    List<Cardapio> findAllByAtivado(Boolean ativado);
}