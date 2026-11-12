package com.prueba.gestion_polizas.service;

import com.prueba.gestion_polizas.model.Poliza;
import com.prueba.gestion_polizas.model.EstadoPoliza;
import com.prueba.gestion_polizas.repository.PolizaRepository;
import com.prueba.gestion_polizas.repository.RiesgoRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class PolizaServiceTest {

    @Mock
    private PolizaRepository polizaRepository;

    @Mock
    private RiesgoRepository riesgoRepository;

    @InjectMocks
    private PolizaService polizaService;

    @Test
    void noDebeRenovarUnaPolizaCancelada() {

        // Arrange: preparar una póliza cancelada
        Long polizaId = 1L;

        Poliza poliza = new Poliza();
        poliza.setId(polizaId);
        poliza.setEstado(EstadoPoliza.CANCELADA);

        when(polizaRepository.findById(polizaId))
                .thenReturn(Optional.of(poliza));

        // Act: intentar renovar la póliza
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> polizaService.renovarPoliza(
            polizaId,
            new BigDecimal("5")
        ));

        // Assert: verificar que se produjo un HTTP 400
        assertEquals(400, exception.getStatusCode().value());

        // Verificar que nunca se guardó la póliza
        verify(polizaRepository, never()).save(poliza);
    }
}