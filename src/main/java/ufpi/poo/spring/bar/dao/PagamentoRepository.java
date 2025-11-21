package ufpi.poo.spring.bar.dao;

import org.springframework.data.repository.CrudRepository;
import ufpi.poo.spring.bar.model.Pagamento;

public interface PagamentoRepository extends CrudRepository<Pagamento, Integer> {
}