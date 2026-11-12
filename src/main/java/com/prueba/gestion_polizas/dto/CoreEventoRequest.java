package com.prueba.gestion_polizas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CoreEventoRequest(
    @NotBlank(message = "El evento es obligatorio")
    String evento,

    @NotNull(message = "El ID de la póliza es obligatorio")
    Long polizaId
) {}