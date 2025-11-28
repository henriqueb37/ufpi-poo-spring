package ufpi.poo.spring.bar.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ufpi.poo.spring.bar.dao.*;
import ufpi.poo.spring.bar.dto.ItemRelatorioDto;
import ufpi.poo.spring.bar.dto.RelatorioGraficoDto;
import ufpi.poo.spring.bar.misc.MesaEstados;
import ufpi.poo.spring.bar.model.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
     * ATUALIZADO: Adiciona um item.
     * Regra: Só aceita se estado for 1 (OCUPADA).
     */
    @Transactional
    public void adicionarPedido(Integer idMesa, Integer idItem, Integer quantidade) {
        Mesa mesa = buscarMesaPorId(idMesa);

        // Se estiver LIVRE (0) ou EM_PAGAMENTO (2), bloqueia.
        if (mesa.getEstado() != MesaEstados.OCUPADA.getLabel()) {
            throw new RuntimeException("Não é possível fazer pedidos. A mesa " + idMesa +
                    " está no estado: " + MesaEstados.fromId(mesa.getEstado()));
        }

        // ... (resto da lógica de buscar item e salvar pedido igual ao anterior) ...
        Cardapio item = cardapioRepository.findById(idItem).orElseThrow();
        Pedido novoPedido = new Pedido();
        novoPedido.setMesa(mesa);
        novoPedido.setItem(item);
        novoPedido.setQuant(quantidade);
        novoPedido.setHora(Instant.now());
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
     * MUDANÇA DE LÓGICA: Fechar Mesa (Encerrar Consumo).
     * Transição: 1 (Ocupada) -> 2 (Em Pagamento).
     * Não exige pagamento total agora. Apenas bloqueia novos pedidos.
     */
    @Transactional
    public void fecharMesa(Integer idMesa) {
        Mesa mesa = buscarMesaPorId(idMesa);

        // Só pode fechar se estiver Ocupada
        if (mesa.getEstado() != MesaEstados.OCUPADA.getLabel()) {
            throw new RuntimeException("A mesa não está ocupada para ser fechada.");
        }

        // Muda estado para 2 (Bloqueia pedidos, libera pagamento final)
        mesa.setEstado(MesaEstados.EM_PAGAMENTO.getLabel());
        mesaRepository.save(mesa);
    }


    /**
     * NOVO MÉTODO: Liberar Mesa (Finalizar Atendimento).
     * Transição: 2 (Em Pagamento) -> 0 (Livre).
     * Regra: Só libera se o saldo for ZERO.
     */
    @Transactional
    public void liberarMesa(Integer idMesa) {
        Mesa mesa = buscarMesaPorId(idMesa);

        // Validação: Só libera se estiver em fase de pagamento
        if (mesa.getEstado() != MesaEstados.EM_PAGAMENTO.getLabel()) {
            throw new RuntimeException("A mesa precisa ser fechada (estado 'Em Pagamento') antes de ser liberada.");
        }

        // Validação Financeira (A regra rígida moveu-se para cá)
        double totalConta = calcularTotalGeral(idMesa);
        double totalPago = pagamentoRepository.calcularTotalPagoMesa(idMesa);
        double saldoPend = totalConta - totalPago;

        if (saldoPend > 0.009) {
            throw new RuntimeException("Mesa não pode ser liberada. Saldo pendente: R$ " + String.format("%.2f", saldoPend));
        }

        // Tudo pago! Libera a mesa para o próximo cliente.
        mesa.setEstado(MesaEstados.LIVRE.getLabel());

        // Opcional: Aqui você pode "arquivar" os pedidos/pagamentos se quiser limpar a mesa visualmente
        // Mas como seu banco guarda histórico, apenas mudar o estado já basta.

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


    // --- RELATÓRIOS ADMINISTRATIVOS (Atualizados) ---

    /**
     * Retorna o valor total faturado (soma dos pagamentos) no período.
     * (Este não muda, pois retorna Double simples)
     */
    public Double gerarRelatorioFaturamento(Instant inicio, Instant fim) {
        return pagamentoRepository.calcularFaturamentoPorPeriodo(inicio, fim);
    }

    /**
     * Retorna lista de itens mais vendidos (Convertida para Gráfico).
     */
    public List<RelatorioGraficoDto> gerarRelatorioMaisVendidos(Instant inicio, Instant fim) {
        // 1. Busca dados crus do banco (Interface)
        List<ItemRelatorioDto> dadosCrus = pedidoRepository.findItensMaisVendidos(inicio, fim);

        // 2. Converte para DTO Simples (POJO) para o JSON funcionar no HTML
        return dadosCrus.stream()
                .map(item -> new RelatorioGraficoDto(
                        item.getNomeItem(),      // Label (Nome)
                        item.getQuantidadeTotal() // Value (Qtd)
                ))
                .collect(Collectors.toList());
    }

    /**
     * Retorna lista de maior receita (Convertida para Gráfico).
     */
    public List<RelatorioGraficoDto> gerarRelatorioMelhoresItens(Instant inicio, Instant fim) {
        // 1. Busca dados crus do banco
        List<ItemRelatorioDto> dadosCrus = pedidoRepository.findItensMaiorFaturamento(inicio, fim);

        // 2. Converte para DTO Simples
        return dadosCrus.stream()
                .map(item -> new RelatorioGraficoDto(
                        item.getNomeItem(),  // Label (Nome)
                        item.getValorTotal() // Value (R$)
                ))
                .collect(Collectors.toList());
    }
}