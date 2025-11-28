package ufpi.poo.spring.bar.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ufpi.poo.spring.bar.model.ConfiguracaoGeral;

public interface ConfiguracaoRepository extends JpaRepository<ConfiguracaoGeral, Integer> {
    // Não precisamos de métodos customizados, pois sempre buscaremos pelo ID fixo '1'.
}