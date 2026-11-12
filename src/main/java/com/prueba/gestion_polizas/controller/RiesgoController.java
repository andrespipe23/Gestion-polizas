package com.prueba.gestion_polizas.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prueba.gestion_polizas.dto.RiesgoResponse;
import com.prueba.gestion_polizas.service.PolizaService;

@RestController
@RequestMapping("/riesgos")
public class RiesgoController {
    private final PolizaService polizaService;

    public RiesgoController(PolizaService polizaService) {
        this.polizaService = polizaService;
    }

    // POST /riesgos/{id}/cancelar
    @PostMapping("/{id}/cancelar")
    public ResponseEntity<RiesgoResponse> cancelarRiesgo(
        @PathVariable Long id
    ) {
        RiesgoResponse respuesta = polizaService.cancelarRiesgo(id);
        return ResponseEntity.ok(respuesta);
    }
}
