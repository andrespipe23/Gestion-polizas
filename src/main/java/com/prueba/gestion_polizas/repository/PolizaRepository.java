package com.prueba.gestion_polizas.repository;

import com.prueba.gestion_polizas.model.Poliza;
import com.prueba.gestion_polizas.model.TipoPoliza;
import com.prueba.gestion_polizas.model.EstadoPoliza;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;

public interface PolizaRepository extends JpaRepository<Poliza, Long> {
    @Query("""
        SELECT p FROM Poliza p
        WHERE (:tipo IS NULL OR p.tipo = :tipo)
        AND (:estado IS NULL OR p.estado = :estado)
    """)
    List<Poliza> buscarConFiltros(
            @Param("tipo") TipoPoliza tipo,
            @Param("estado") EstadoPoliza estado
    );
}
