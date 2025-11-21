package ufpi.poo.spring.bar.dao;

import org.springframework.data.repository.CrudRepository;
import ufpi.poo.spring.bar.model.Cardapio;

public interface CardapioRepository extends CrudRepository<Cardapio, Integer> {
}