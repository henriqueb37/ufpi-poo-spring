package ufpi.poo.spring.bar.dao;

import org.springframework.data.repository.CrudRepository;
import ufpi.poo.spring.bar.model.Pedido;

public interface PedidoRepository extends CrudRepository<Pedido, Integer> {
}