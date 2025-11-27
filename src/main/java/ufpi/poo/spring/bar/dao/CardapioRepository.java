package ufpi.poo.spring.bar.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ufpi.poo.spring.bar.model.Cardapio;
import java.util.List;

public interface CardapioRepository extends JpaRepository<Cardapio, Integer> {

    // Lista apenas os itens que estão marcados como ATIVADOS (não deletados)
    // SQL gerado: SELECT * FROM cardapio WHERE ativado = true
    List<Cardapio> findByAtivadoTrue();

    // Ordena por nome para ficar bonito na tela
    List<Cardapio> findByAtivadoTrueOrderByNomeAsc();
}