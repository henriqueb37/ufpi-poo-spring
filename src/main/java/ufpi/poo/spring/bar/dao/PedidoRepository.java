package ufpi.poo.spring.bar.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ufpi.poo.spring.bar.model.Pedido;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Integer> {

    // 1. Buscar todos os pedidos de uma mesa específica
    // Isso substitui o antigo 'findByNumConta'
    List<Pedido> findByMesaId(Integer idMesa);

    // 2. CÁLCULO DO SUBTOTAL (Soma dos Itens)
    // Soma (Quantidade * Valor Atual do Item)
    // Retorna 0.0 se não houver pedidos (COALESCE)
    @Query("SELECT COALESCE(SUM(p.quant * c.valor), 0.0) " +
            "FROM Pedido p JOIN p.item c " +
            "WHERE p.mesa.id = :idMesa")
    Double calcularSubtotalMesa(@Param("idMesa") Integer idMesa);

    // 3. CÁLCULO DA GORJETA (Regra de Negócio)
    // Calcula a porcentagem baseada no tipo do item (c.tipo.percGorjeta)
    @Query("SELECT COALESCE(SUM(p.quant * c.valor * (t.percGorjeta / 100.0)), 0.0) " +
            "FROM Pedido p " +
            "JOIN p.item c " +
            "JOIN c.tipo t " +
            "WHERE p.mesa.id = :idMesa")
    Double calcularGorjetaMesa(@Param("idMesa") Integer idMesa);
}