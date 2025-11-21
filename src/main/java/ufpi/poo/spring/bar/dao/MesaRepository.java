package ufpi.poo.spring.bar.dao;

import org.springframework.data.repository.CrudRepository;
import ufpi.poo.spring.bar.model.Mesa;

public interface MesaRepository extends CrudRepository<Mesa, Integer> {
}