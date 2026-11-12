package com.prueba.gestion_polizas.controller;

import com.prueba.gestion_polizas.dto.CoreEventoRequest;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/core-mock")
public class CoreMockController {

    private static final Logger logger = LoggerFactory.getLogger(CoreMockController.class);

    @PostMapping("/evento")
    public ResponseEntity<Map<String, Object>> recibirEvento(@Valid @RequestBody CoreEventoRequest request) {
        logger.info("CORE MOCK: Evento recibido");
        logger.info("Evento: {}", request.evento());
        logger.info("Poliza ID: {}", request.polizaId());

        return ResponseEntity.ok(Map.of(
            "mensaje", "Evento recibido correctamente por el CORE",
            "evento", request.evento(),
            "polizaId", request.polizaId()
        ));
    }
}