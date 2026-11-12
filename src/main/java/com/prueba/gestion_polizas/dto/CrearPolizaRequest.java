package com.prueba.gestion_polizas.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.prueba.gestion_polizas.model.TipoPoliza;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CrearPolizaRequest(
    @NotNull(message = "El tipo de póliza es obligatorio")
    TipoPoliza tipo,

    @NotNull(message = "La fecha de inicio es obligatoria")
    LocalDate fechaInicio,

    @NotNull(message = "La fecha final es obligatoria")
    LocalDate fechaFin,

    @NotNull(message = "La vigencia inicial es obligatoria")
    @Positive(message = "La vigencia debe ser mayor que cero")
    Integer vigenciaInicialMeses,

    @NotNull(message = "El valor de canon mensual es obligatorio")
    @DecimalMin(value = "0.01", message = "El canon debe ser mayor que cero")
    BigDecimal canonMensual
) {

}