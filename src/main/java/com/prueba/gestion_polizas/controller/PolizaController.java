package com.prueba.gestion_polizas.controller;

import com.prueba.gestion_polizas.dto.CrearPolizaRequest;
import com.prueba.gestion_polizas.dto.CrearRiesgoRequest;
import com.prueba.gestion_polizas.dto.PolizaResponse;
import com.prueba.gestion_polizas.dto.RiesgoResponse;
import com.prueba.gestion_polizas.model.TipoPoliza;
import com.prueba.gestion_polizas.model.EstadoPoliza;
import com.prueba.gestion_polizas.service.PolizaService;
import com.prueba.gestion_polizas.dto.RenovarPolizaRequest;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/polizas")
public class PolizaController {
    private final PolizaService polizaService;

    public PolizaController(PolizaService polizaService) {
        this.polizaService = polizaService;
    }

    // GET /polizas
    @GetMapping
    public List<PolizaResponse> listarPolizas(
        @RequestParam(required = false) TipoPoliza tipo,
        @RequestParam(required = false) EstadoPoliza estado
    ) {
        return polizaService.listarPolizas(tipo, estado);
    }

    // GET /polizas/{id}/riesgos
    @GetMapping("/{id}/riesgos")
    public List<RiesgoResponse> listarRiesgos(
        @PathVariable Long id
    ) {
        return polizaService.listarRiesgos(id);
    }  

    // POST /polizas/{id}
    @PostMapping
    public ResponseEntity<PolizaResponse> agregarPoliza(
        @Valid @RequestBody CrearPolizaRequest request
    ) {
        PolizaResponse respuesta = polizaService.agregarPoliza(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }
    
    // POST /polizas/{id}/renovar
    @PostMapping("/{id}/renovar")
    public ResponseEntity<PolizaResponse> renovarPoliza(
        @PathVariable Long id,
        @Valid @RequestBody RenovarPolizaRequest request
    ) {
        PolizaResponse respuesta = polizaService.renovarPoliza(
            id,
            request.ipcPorcentaje()
        );

        return ResponseEntity.ok(respuesta);
    }

    // POST /polizas/{id}/cancelar
    @PostMapping("/{id}/cancelar")
    public ResponseEntity<PolizaResponse> cancelarPoliza(
        @PathVariable Long id
    ) {
        PolizaResponse respuesta = polizaService.cancelarPoliza(id);
        return ResponseEntity.ok(respuesta);
    }

    // POST /polizas/{id}/riesgos
    @PostMapping("/{id}/riesgos")
    public ResponseEntity<RiesgoResponse> agregarRiesgo(
        @PathVariable Long id,
        @Valid @RequestBody CrearRiesgoRequest request
    ) {
        RiesgoResponse respuesta = polizaService.agregarRiesgo(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }
}
