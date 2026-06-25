package com.dosimetros.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tipo_porta")
public class TipoPorta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tipo_dosimetro_id", nullable = false)
    private TipoDosimetro tipoDosimetro;

    public TipoPorta() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public TipoDosimetro getTipoDosimetro() {
        return tipoDosimetro;
    }

    public void setTipoDosimetro(TipoDosimetro tipoDosimetro) {
        this.tipoDosimetro = tipoDosimetro;
    }
}