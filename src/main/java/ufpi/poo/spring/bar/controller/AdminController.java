package ufpi.poo.spring.bar.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ufpi.poo.spring.bar.dto.ItemRelatorioDto;
import ufpi.poo.spring.bar.service.BarService;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')") // Segurança: Apenas ADMIN acessa
public class AdminController {

    @Autowired
    private BarService barService;

    @GetMapping("/relatorios")
    public String painelRelatorios(
            Model model,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim
    ) {
        // 1. Define datas padrão (Últimos 30 dias) se não forem passadas
        if (inicio == null) inicio = LocalDate.now().minus(30, ChronoUnit.DAYS);
        if (fim == null) fim = LocalDate.now();

        // 2. Converte LocalDate (dia) para Instant (momento exato) para o Banco
        // Inicio do dia: 00:00:00
        var dataInicio = inicio.atStartOfDay(ZoneId.systemDefault()).toInstant();
        // Fim do dia: 23:59:59 (ajustamos pegando o dia seguinte às 00:00)
        var dataFim = fim.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();

        // 3. Busca os dados usando o Serviço
        Double faturamentoTotal = barService.gerarRelatorioFaturamento(dataInicio, dataFim);
        List<ItemRelatorioDto> maisVendidos = barService.gerarRelatorioMaisVendidos(dataInicio, dataFim);
        List<ItemRelatorioDto> maiorFaturamento = barService.gerarRelatorioMelhoresItens(dataInicio, dataFim);

        // 4. Manda para a tela
        model.addAttribute("inicio", inicio);
        model.addAttribute("fim", fim);
        model.addAttribute("faturamentoTotal", faturamentoTotal);

        // Manda as listas para serem usadas pelo JavaScript do gráfico
        model.addAttribute("maisVendidos", maisVendidos);
        model.addAttribute("maiorFaturamento", maiorFaturamento);

        return "admin-relatorios"; // Nome do HTML que faremos no próximo passo
    }
}