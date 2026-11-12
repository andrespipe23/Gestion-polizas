package com.prueba.gestion_polizas.dto;

import com.prueba.gestion_polizas.model.TipoPoliza;
import com.prueba.gestion_polizas.model.EstadoPoliza;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PolizaResponse(
    Long id,
    TipoPoliza tipo,
    EstadoPoliza estado,
    LocalDate fechaInicio,
    LocalDate fechaFin,
    Integer vigenciaInicialMeses,
    BigDecimal canonMensual,
    BigDecimal prima,
    BigDecimal ipcAplicado
) {}