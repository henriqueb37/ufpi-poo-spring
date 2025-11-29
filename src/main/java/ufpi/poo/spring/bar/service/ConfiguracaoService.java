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

    private double defaultCovert = 10;

    /**
     * Garante que a linha de configuração exista no banco.
     * Se não existir, cria com valores padrão.
     */
    private ConfiguracaoGeral getOrCreateConfig() {
        return repository.findById(1)
                .orElseGet(() -> {
                    ConfiguracaoGeral novaConfig = new ConfiguracaoGeral();
                    novaConfig.setValorCouvert(defaultCovert);
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
}