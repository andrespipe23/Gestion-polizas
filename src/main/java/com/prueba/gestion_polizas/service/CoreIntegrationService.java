package com.prueba.gestion_polizas.service;

import com.prueba.gestion_polizas.dto.CoreEventoRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class CoreIntegrationService {

    private static final Logger logger = LoggerFactory.getLogger(CoreIntegrationService.class);

    private final RestClient restClient;

    public CoreIntegrationService(RestClient.Builder builder) {
        this.restClient = builder.baseUrl("http://localhost:8080").build();
    }

    public void enviarEvento(Long polizaId, String evento) {
        CoreEventoRequest request = new CoreEventoRequest(evento, polizaId);
        
        try {
            logger.info("Intentando enviar evento al CORE. Póliza: {}, Evento: {}", polizaId, evento);
            restClient.post().uri("/core-mock/evento").body(request).retrieve().toBodilessEntity();
            logger.info("Evento enviado correctamente al CORE. Póliza: {}", polizaId);
        } catch (Exception ex) {
            logger.error("Error enviando evento al CORE. Póliza: {}, Error: {}", polizaId, ex.getMessage());
        }
    }
}