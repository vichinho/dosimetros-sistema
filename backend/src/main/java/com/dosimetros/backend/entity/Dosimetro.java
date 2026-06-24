package com.dosimetros.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "dosimetro")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dosimetro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Número físico grabado — PUEDE REPETIR entre distintos dosímetros
    @Column(nullable = false)
    private Integer numero;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tipo_dosimetro_id", nullable = false)
    private TipoDosimetro tipoDosimetro;

    // NULL cuando el dosímetro está "sin armar"
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_porta_id")
    private TipoPorta tipoPorta;

    // NULL para dosímetros OSL
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tarea_id")
    private Tarea tarea;

    // NULL para OSL
    @Column(name = "numero_bandeja")
    private Integer numeroBandeja;

    // NULL para OSL
    @Column(name = "slot_bandeja")
    private Integer slotBandeja;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    @Builder.Default
    private EstadoDosimetro estado = EstadoDosimetro.disponible;

    @Column(length = 500)
    private String observacion;
}
