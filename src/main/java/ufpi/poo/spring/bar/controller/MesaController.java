package ufpi.poo.spring.bar.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ufpi.poo.spring.bar.dao.MesaRepository;
import ufpi.poo.spring.bar.dto.MesaDto;
import ufpi.poo.spring.bar.model.Mesa;
import ufpi.poo.spring.bar.service.DadosService;

import java.util.Optional;

@RestController
@RequestMapping("/api/mesa")
public class MesaController {
    @Autowired
    private MesaRepository mesaDao;
    @Autowired
    private DadosService dadosService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getMesaData(@PathVariable Integer id) {
        Optional<Mesa> mesa = mesaDao.findById(id);
        if (mesa.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dadosService.getMesa(mesa.get()));
    }
}
