package com.prueba.gestion_polizas.dto;

import com.prueba.gestion_polizas.model.EstadoRiesgo;

public record RiesgoResponse(
        Long id,
        EstadoRiesgo estado,
        String inmueble,
        String arrendatario,
        String arrendador
) {}