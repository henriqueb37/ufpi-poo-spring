package ufpi.poo.spring.bar.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ufpi.poo.spring.bar.model.Pagamento;

import java.util.List;

public interface PagamentoRepository extends JpaRepository<Pagamento, Integer> {

    // 1. Buscar histórico de pagamentos da mesa (para o extrato detalhado)
    List<Pagamento> findByIdMesaId(Integer idMesa);

    // 2. CÁLCULO DO TOTAL PAGO
    // Soma todos os valores pagos para aquela mesa
    // COALESCE garante que retorne 0.0 se não houver pagamentos (evita NullPointerException)
    @Query("SELECT COALESCE(SUM(p.valor), 0.0) " +
            "FROM Pagamento p " +
            "WHERE p.idMesa.id = :idMesa")
    Double calcularTotalPagoMesa(@Param("idMesa") Integer idMesa);
}