package com.prueba.gestion_polizas.config;

import com.prueba.gestion_polizas.model.*;
import com.prueba.gestion_polizas.repository.*;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner cargarDatos(
        PolizaRepository polizaRepository,
        RiesgoRepository riesgoRepository
    ) {
        return args -> {
            if (polizaRepository.count() > 0) {
                return;
            }

            // Póliza individual
            Poliza individual = new Poliza();
            individual.setTipo(TipoPoliza.INDIVIDUAL);
            individual.setEstado(EstadoPoliza.ACTIVA);
            individual.setFechaInicio(LocalDate.now());
            individual.setFechaFin(LocalDate.now().plusMonths(12));
            individual.setVigenciaInicialMeses(12);
            individual.setCanonMensual(new BigDecimal("1500000"));
            individual.setPrima(new BigDecimal("18000000"));
            individual.setIpcAplicado(BigDecimal.ZERO);
            polizaRepository.save(individual);

            // Riesgo de póliza individual
            Riesgo riesgo1 = new Riesgo();
            riesgo1.setPoliza(individual);
            riesgo1.setEstado(EstadoRiesgo.ACTIVO);
            riesgo1.setInmueble("Apartamento 101");
            riesgo1.setArrendatario("Carlos");
            riesgo1.setArrendador("Pedro");
            riesgoRepository.save(riesgo1);

            // Póliza colectiva
            Poliza colectiva = new Poliza();
            colectiva.setTipo(TipoPoliza.COLECTIVA);
            colectiva.setEstado(EstadoPoliza.ACTIVA);
            colectiva.setFechaInicio(LocalDate.now());
            colectiva.setFechaFin(LocalDate.now().plusMonths(12));
            colectiva.setVigenciaInicialMeses(12);
            colectiva.setCanonMensual(new BigDecimal("2000000"));
            colectiva.setPrima(new BigDecimal("24000000"));
            colectiva.setIpcAplicado(BigDecimal.ZERO);
            polizaRepository.save(colectiva);

            // Primer riesgo colectivo
            Riesgo riesgo2 = new Riesgo();
            riesgo2.setPoliza(colectiva);
            riesgo2.setEstado(EstadoRiesgo.ACTIVO);
            riesgo2.setInmueble("Apartamento 202");
            riesgo2.setArrendatario("Andrés");
            riesgo2.setArrendador("María");
            riesgoRepository.save(riesgo2);

            // Segundo riesgo colectivo
            Riesgo riesgo3 = new Riesgo();
            riesgo3.setPoliza(colectiva);
            riesgo3.setEstado(EstadoRiesgo.ACTIVO);
            riesgo3.setInmueble("Apartamento 303");
            riesgo3.setArrendatario("Laura");
            riesgo3.setArrendador("María");
            riesgoRepository.save(riesgo3);
        };
    }
}