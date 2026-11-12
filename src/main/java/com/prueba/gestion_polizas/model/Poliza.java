package com.prueba.gestion_polizas.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "polizas")
public class Poliza {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPoliza tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPoliza estado;

    private LocalDate fechaInicio;

    private LocalDate fechaFin;

    private Integer vigenciaInicialMeses;

    @Column(precision = 15, scale = 2)
    private BigDecimal canonMensual;

    @Column(precision = 15, scale = 2)
    private BigDecimal prima;

    @Column(precision = 8, scale = 4)
    private BigDecimal ipcAplicado;

    @OneToMany(mappedBy = "poliza", fetch = FetchType.LAZY)
    private List<Riesgo> riesgos = new ArrayList<>();

    public Poliza() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoPoliza getTipo() {
        return tipo;
    }

    public void setTipo(TipoPoliza tipo) {
        this.tipo = tipo;
    }

    public EstadoPoliza getEstado() {
        return estado;
    }

    public void setEstado(EstadoPoliza estado) {
        this.estado = estado;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Integer getVigenciaInicialMeses() {
        return vigenciaInicialMeses;
    }

    public void setVigenciaInicialMeses(Integer vigenciaInicialMeses) {
        this.vigenciaInicialMeses = vigenciaInicialMeses;
    }

    public BigDecimal getCanonMensual() {
        return canonMensual;
    }

    public void setCanonMensual(BigDecimal canonMensual) {
        this.canonMensual = canonMensual;
    }

    public BigDecimal getPrima() {
        return prima;
    }

    public void setPrima(BigDecimal prima) {
        this.prima = prima;
    }

    public BigDecimal getIpcAplicado() {
        return ipcAplicado;
    }

    public void setIpcAplicado(BigDecimal ipcAplicado) {
        this.ipcAplicado = ipcAplicado;
    }

    public List<Riesgo> getRiesgos() {
        return riesgos;
    }

    public void setRiesgos(List<Riesgo> riesgos) {
        this.riesgos = riesgos;
    }
}
