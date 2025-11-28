package ufpi.poo.spring.bar.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ufpi.poo.spring.bar.dao.ConfiguracaoRepository;
import ufpi.poo.spring.bar.model.ConfiguracaoGeral;

@Service
public class ConfiguracaoService {

    @Autowired
    private ConfiguracaoRepository repository;

    /**
     * Garante que a linha de configuração exista no banco.
     * Se não existir, cria com valores padrão.
     */
    private ConfiguracaoGeral getOrCreateConfig() {
        // Tentamos buscar a linha com ID 1
        return repository.findById(1)
                .orElseGet(() -> {
                    // Se não encontrar, cria uma nova e salva
                    ConfiguracaoGeral novaConfig = new ConfiguracaoGeral();
                    return repository.save(novaConfig);
                });
    }

    /**
     * Método público para obter a configuração atual (Valor do Couvert, Taxas).
     */
    public ConfiguracaoGeral getConfiguracaoAtual() {
        return getOrCreateConfig();
    }

    /**
     * Atualiza o valor do Couvert (Preço de Entrada).
     */
    @Transactional
    public void atualizarCouvert(Double valor) {
        if (valor == null || valor < 0) {
            throw new IllegalArgumentException("Valor de couvert inválido.");
        }
        ConfiguracaoGeral config = getOrCreateConfig();
        config.setValorCouvert(valor);
        repository.save(config);
    }

    /**
     * Atualiza as taxas de gorjeta.
     */
    @Transactional
    public void atualizarTaxasGorjeta(Double percComida, Double percBebida) {
        if (percComida == null || percBebida == null || percComida < 0 || percBebida < 0) {
            throw new IllegalArgumentException("Percentuais de gorjeta inválidos.");
        }
        ConfiguracaoGeral config = getOrCreateConfig();
        config.setPercGorjetaComida(percComida);
        config.setPercGorjetaBebida(percBebida);
        repository.save(config);
    }
}