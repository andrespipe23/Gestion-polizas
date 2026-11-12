package com.prueba.gestion_polizas.dto;

import jakarta.validation.constraints.NotBlank;

public record CrearRiesgoRequest(
    @NotBlank(message = "El inmueble es obligatorio")
    String inmueble,

    @NotBlank(message = "El arrendatario es obligatorio")
    String arrendatario,

    @NotBlank(message = "El arrendador es obligatorio")
    String arrendador
) {

}
