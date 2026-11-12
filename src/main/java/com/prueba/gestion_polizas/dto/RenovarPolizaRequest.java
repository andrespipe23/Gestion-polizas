package com.prueba.gestion_polizas.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public  record RenovarPolizaRequest(
    @NotNull(message = "El IPC es obligatorio")
    @DecimalMin(value = "0.0", message = "El IPC no puede ser negativo")
    BigDecimal ipcPorcentaje
) {}