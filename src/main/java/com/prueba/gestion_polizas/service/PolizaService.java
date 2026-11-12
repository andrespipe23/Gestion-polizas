package com.prueba.gestion_polizas.service;

import com.prueba.gestion_polizas.dto.CrearPolizaRequest;
import com.prueba.gestion_polizas.dto.CrearRiesgoRequest;
import com.prueba.gestion_polizas.dto.PolizaResponse;
import com.prueba.gestion_polizas.dto.RiesgoResponse;

import com.prueba.gestion_polizas.model.Poliza;
import com.prueba.gestion_polizas.model.Riesgo;
import com.prueba.gestion_polizas.model.TipoPoliza;
import com.prueba.gestion_polizas.model.EstadoPoliza;
import com.prueba.gestion_polizas.model.EstadoRiesgo;
import com.prueba.gestion_polizas.repository.PolizaRepository;
import com.prueba.gestion_polizas.repository.RiesgoRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
public class PolizaService {
    private final PolizaRepository polizaRepository;
    private final RiesgoRepository riesgoRepository;

    public PolizaService(
        PolizaRepository polizaRepository,
        RiesgoRepository riesgoRepository
    ) {
        this.polizaRepository = polizaRepository;
        this.riesgoRepository = riesgoRepository;
    }

    public List<PolizaResponse> listarPolizas(
        TipoPoliza tipo,
        EstadoPoliza estado
    ) {
        return polizaRepository.buscarConFiltros(tipo, estado).stream().map(this::convertirPoliza).toList();
    }

    public List<RiesgoResponse> listarRiesgos(Long polizaId) {
        if (!polizaRepository.existsById(polizaId)) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "La póliza no existe"
            );
        }

        return riesgoRepository.findByPolizaId(polizaId).stream().map(this::convertirRiesgo).toList();
    }

    private PolizaResponse convertirPoliza(Poliza poliza) {
        return new PolizaResponse(
            poliza.getId(),
            poliza.getTipo(),
            poliza.getEstado(),
            poliza.getFechaInicio(),
            poliza.getFechaFin(),
            poliza.getVigenciaInicialMeses(),
            poliza.getCanonMensual(),
            poliza.getPrima(),
            poliza.getIpcAplicado()
        );
    }

    private RiesgoResponse convertirRiesgo(Riesgo riesgo) {
        return new RiesgoResponse(
            riesgo.getId(),
            riesgo.getEstado(),
            riesgo.getInmueble(),
            riesgo.getArrendatario(),
            riesgo.getArrendador()
        );
    }

    @Transactional
    public PolizaResponse agregarPoliza(CrearPolizaRequest request) {
        if (!request.fechaFin().isAfter(request.fechaInicio()) && !request.fechaFin().isEqual(request.fechaInicio())) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "La fecha final debe ser igual o posterior a la fecha inicial"
            );
        }

        BigDecimal prima = request.canonMensual().multiply(BigDecimal.valueOf(request.vigenciaInicialMeses())).setScale(2, RoundingMode.HALF_UP);

        Poliza poliza = new Poliza();
        poliza.setTipo(request.tipo());
        poliza.setEstado(EstadoPoliza.ACTIVA);
        poliza.setFechaInicio(request.fechaInicio());
        poliza.setFechaFin(request.fechaFin());
        poliza.setVigenciaInicialMeses(request.vigenciaInicialMeses());
        poliza.setCanonMensual(request.canonMensual());
        poliza.setPrima(prima);
        poliza.setIpcAplicado(BigDecimal.ZERO);
        Poliza polizaGuardada = polizaRepository.save(poliza);

        return convertirPoliza(polizaGuardada);
    }

    @Transactional
    public PolizaResponse renovarPoliza(Long id, BigDecimal ipcPorcentaje) {
        Poliza poliza = polizaRepository.findById(id).orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "La póliza no existe"
        ));

        if (poliza.getEstado() == EstadoPoliza.CANCELADA) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "No se puede renovar una póliza cancelada"
            );
        }

        if (poliza.getCanonMensual() == null ||
            poliza.getVigenciaInicialMeses() == null ||
            poliza.getFechaFin() == null ||
            poliza.getVigenciaInicialMeses() <= 0
        ) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "La póliza no tiene información suficiente para renovarse"
            );
        }

        BigDecimal factorIPC = BigDecimal.ONE.add(
            ipcPorcentaje.divide(
                new BigDecimal("100"),
                8,
                RoundingMode.HALF_UP
            )
        );

        BigDecimal nuevoCanon = poliza.getCanonMensual().multiply(factorIPC).setScale(2, RoundingMode.HALF_UP);
        BigDecimal nuevaPrima = nuevoCanon.multiply(BigDecimal.valueOf(poliza.getVigenciaInicialMeses())).setScale(2, RoundingMode.HALF_UP);
        LocalDate nuevaFechaInicio = poliza.getFechaFin().plusDays(1);
        LocalDate nuevaFechaFin = nuevaFechaInicio.plusMonths(poliza.getVigenciaInicialMeses()).minusDays(1);

        poliza.setFechaInicio(nuevaFechaInicio);
        poliza.setFechaFin(nuevaFechaFin);
        poliza.setCanonMensual(nuevoCanon);
        poliza.setPrima(nuevaPrima);
        poliza.setIpcAplicado(ipcPorcentaje);
        poliza.setEstado(EstadoPoliza.RENOVADA);
        Poliza polizaActualizada = polizaRepository.save(poliza);

        return convertirPoliza(polizaActualizada);
    }

    @Transactional
    public PolizaResponse cancelarPoliza(Long id) {
        Poliza poliza = polizaRepository.findById(id).orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "No existe la póliza con ID: " + id
        ));

        if (poliza.getEstado() == EstadoPoliza.CANCELADA) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "La póliza ya se encuentra cancelada"
            );
        }

        List<Riesgo> riesgos = riesgoRepository.findByPolizaId(id);
        for (Riesgo riesgo : riesgos) {
            riesgo.setEstado(EstadoRiesgo.CANCELADO);
        }
        riesgoRepository.saveAll(riesgos);

        poliza.setEstado(EstadoPoliza.CANCELADA);
        Poliza polizaActualizada = polizaRepository.save(poliza);

        return convertirPoliza(polizaActualizada);
    }

    @Transactional
    public RiesgoResponse agregarRiesgo(Long polizaId, CrearRiesgoRequest request) {
        Poliza poliza = polizaRepository.findById(polizaId).orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "No existe la póliza con ID: " + polizaId
        ));

        if (poliza.getEstado() == EstadoPoliza.CANCELADA) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "No se pueden agregar riesgos a una póliza cancelada"
            );
        }

        if (poliza.getTipo() == TipoPoliza.INDIVIDUAL) {
            List<Riesgo> riesgosExistentes =
                    riesgoRepository.findByPolizaId(polizaId);

            if (!riesgosExistentes.isEmpty()) {
                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Una póliza individual solamente puede tener un riesgo"
                );
            }
        }

        Riesgo riesgo = new Riesgo();
        riesgo.setPoliza(poliza);
        riesgo.setEstado(EstadoRiesgo.ACTIVO);
        riesgo.setInmueble(request.inmueble());
        riesgo.setArrendatario(request.arrendatario());
        riesgo.setArrendador(request.arrendador());
        Riesgo riesgoGuardado = riesgoRepository.save(riesgo);

        return new RiesgoResponse(
            riesgoGuardado.getId(),
            riesgoGuardado.getEstado(),
            riesgoGuardado.getInmueble(),
            riesgoGuardado.getArrendatario(),
            riesgoGuardado.getArrendador()
        );
    }

    @Transactional
    public RiesgoResponse cancelarRiesgo(Long riesgoId) {
        Riesgo riesgo = riesgoRepository.findById(riesgoId).orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "No existe el riesgo con ID: " + riesgoId
        ));

        if (riesgo.getEstado() == EstadoRiesgo.CANCELADO) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "El riesgo ya se encuentra cancelado"
            );
        }

        riesgo.setEstado(EstadoRiesgo.CANCELADO);
        Riesgo riesgoActualizado = riesgoRepository.save(riesgo);

        return new RiesgoResponse(
            riesgoActualizado.getId(),
            riesgoActualizado.getEstado(),
            riesgoActualizado.getInmueble(),
            riesgoActualizado.getArrendatario(),
            riesgoActualizado.getArrendador()
        );
    }
}