package ufpi.poo.spring.bar.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ufpi.poo.spring.bar.dao.*;
import ufpi.poo.spring.bar.model.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class BarService {

    // Injeção de Dependências (Os "Braços")
    @Autowired private MesaRepository mesaRepository;
    @Autowired private PedidoRepository pedidoRepository;
    @Autowired private CardapioRepository cardapioRepository;
    @Autowired private PagamentoRepository pagamentoRepository;

    // --- MÉTODOS DE LEITURA (Para as Telas) ---

    public List<Mesa> listarTodasMesas() {
        return mesaRepository.findAll();
    }

    public Mesa buscarMesaPorId(Integer id) {
        return mesaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mesa não encontrada: " + id));
    }

    // --- MÉTODOS OPERACIONAIS (Ações do Garçom) ---

    /**
     * Abre uma mesa para iniciar o atendimento.
     */
    @Transactional
    public void abrirMesa(Integer idMesa) {
        Mesa mesa = buscarMesaPorId(idMesa);

        // Regra: Só pode abrir se estiver LIVRE (vamos assumir 0=Livre, 1=Ocupada)
        if (mesa.getEstado() == 1) {
            throw new RuntimeException("Mesa " + idMesa + " já está ocupada!");
        }

        mesa.setEstado(1); // Marca como Ocupada
        mesa.setHoraAberta(Instant.now());
        mesa.setAtivado(true);
        // Limpa pagamentos/pedidos antigos se necessário, ou assume que o banco está limpo

        mesaRepository.save(mesa);
    }

    /**
     * Adiciona um item (Pedido) à mesa.
     */
    @Transactional
    public void adicionarPedido(Integer idMesa, Integer idItem, Integer quantidade) {
        Mesa mesa = buscarMesaPorId(idMesa);

        // Regra: Mesa deve estar aberta
        if (mesa.getEstado() != 1) {
            throw new RuntimeException("A mesa " + idMesa + " está fechada. Abra a mesa primeiro.");
        }

        Cardapio item = cardapioRepository.findById(idItem)
                .orElseThrow(() -> new RuntimeException("Item do cardápio não encontrado: " + idItem));

        Pedido novoPedido = new Pedido();
        novoPedido.setMesa(mesa);
        novoPedido.setItem(item);
        novoPedido.setQuant(quantidade);
        novoPedido.setHora(Instant.now());
        novoPedido.setCancelamento(null); // Não está cancelado

        pedidoRepository.save(novoPedido);
    }

    /**
     * Cancela um item (Auditoria).
     * Em vez de deletar, salvamos o motivo no campo 'cancelamento'.
     */
    @Transactional
    public void cancelarPedido(Integer idPedido, String motivo) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        if (pedido.getCancelamento() != null) {
            throw new RuntimeException("Este pedido já foi cancelado anteriormente.");
        }

        pedido.setCancelamento(motivo); // Marca como cancelado e salva o motivo
        pedidoRepository.save(pedido);
    }

    /**
     * Registra um pagamento parcial ou total.
     */
    @Transactional
    public void registrarPagamento(Integer idMesa, Double valor) {
        Mesa mesa = buscarMesaPorId(idMesa);

        if (valor <= 0) {
            throw new RuntimeException("Valor do pagamento deve ser positivo.");
        }

        // Regra: Não pode pagar mais do que deve
        double totalConta = calcularTotalGeral(idMesa);
        double totalJaPago = pagamentoRepository.calcularTotalPagoMesa(idMesa);
        double saldoDevedor = totalConta - totalJaPago;

        // Pequena margem de erro para double (0.01)
        if (valor > (saldoDevedor + 0.01)) {
            throw new RuntimeException("Valor excede o saldo devedor de R$ " + String.format("%.2f", saldoDevedor));
        }

        Pagamento pagamento = new Pagamento();
        pagamento.setIdMesa(mesa);
        pagamento.setValor(valor);
        pagamento.setHora(Instant.now());

        pagamentoRepository.save(pagamento);
    }

    /**
     * Fecha a mesa (Encerra o atendimento).
     */
    @Transactional
    public void fecharMesa(Integer idMesa) {
        Mesa mesa = buscarMesaPorId(idMesa);

        // Validações
        if (mesa.getEstado() == 0) {
            throw new RuntimeException("Mesa já está livre.");
        }

        // Regra de Ouro: Só fecha se estiver pago
        double totalConta = calcularTotalGeral(idMesa);
        double totalPago = pagamentoRepository.calcularTotalPagoMesa(idMesa);
        double saldoPend = totalConta - totalPago;

        if (saldoPend > 0.009) { // Margem de segurança de 5 centavos
            throw new RuntimeException("Não é possível fechar. Ainda há saldo devedor de R$ " + String.format("%.2f", saldoPend));
        }

        // Resetar a mesa
        mesa.setEstado(0); // Livre
        mesa.setAtivado(true);
        // Opcional: Limpar dados ou arquivar
        mesaRepository.save(mesa);
    }

    // --- MÉTODOS DE CÁLCULO (Auxiliares) ---

    public Double calcularTotalGeral(Integer idMesa) {
        // Precisamos filtrar pedidos cancelados!
        // Como o Repository soma TUDO via SQL puro, o ideal é atualizar o SQL lá.
        // Mas por segurança, vou chamar a lógica que considera cancelamentos aqui.

        // Soma dos Itens (Excluindo cancelados)
        List<Pedido> pedidos = pedidoRepository.findByMesaId(idMesa);

        double totalItens = 0.0;
        double totalGorjeta = 0.0;

        for (Pedido p : pedidos) {
            // PULA se estiver cancelado
            if (p.getCancelamento() != null) continue;

            double valItem = p.getItem().getValor();
            double subtotalItem = valItem * p.getQuant();

            // Soma Item
            totalItens += subtotalItem;

            // Calcula Gorjeta Específica deste item
            Double percGorjeta = p.getItem().getTipo().getPercGorjeta(); // Ex: 10.0 ou 15.0
            if (percGorjeta != null && percGorjeta > 0) {
                totalGorjeta += subtotalItem * (percGorjeta / 100.0);
            }
        }

        return totalItens + totalGorjeta;
    }
}