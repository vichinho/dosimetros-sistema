package com.dosimetros.backend.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "dosimetro")
public class Dosimetro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer numero;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tipo_dosimetro_id", nullable = false)
    private TipoDosimetro tipoDosimetro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_porta_id")
    private TipoPorta tipoPorta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tarea_id")
    private Tarea tarea;

    @Column(name = "numero_bandeja")
    private Integer numeroBandeja;

    @Column(name = "slot_bandeja")
    private Integer slotBandeja;

    @Column(nullable = false, length = 50)
    private String estado = "disponible";

    @Column(length = 500)
    private String observacion;

    // Fecha en que el dosímetro entró al inventario. Permite consultar el
    // stock histórico (cuántos existían a una fecha pasada). Los importados
    // heredan la fecha de creación de su tarea.
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDate fechaCreacion;

    public Dosimetro() {
    }

    @PrePersist
    private void asignarFechaCreacionSiFalta() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDate.now();
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public TipoDosimetro getTipoDosimetro() {
        return tipoDosimetro;
    }

    public void setTipoDosimetro(TipoDosimetro tipoDosimetro) {
        this.tipoDosimetro = tipoDosimetro;
    }

    public TipoPorta getTipoPorta() {
        return tipoPorta;
    }

    public void setTipoPorta(TipoPorta tipoPorta) {
        this.tipoPorta = tipoPorta;
    }

    public Tarea getTarea() {
        return tarea;
    }

    public void setTarea(Tarea tarea) {
        this.tarea = tarea;
    }

    public Integer getNumeroBandeja() {
        return numeroBandeja;
    }

    public void setNumeroBandeja(Integer numeroBandeja) {
        this.numeroBandeja = numeroBandeja;
    }

    public Integer getSlotBandeja() {
        return slotBandeja;
    }

    public void setSlotBandeja(Integer slotBandeja) {
        this.slotBandeja = slotBandeja;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}