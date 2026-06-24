package com.dosimetros.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "asignacion")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Asignacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dosimetro_id", nullable = false)
    private Dosimetro dosimetro;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ejecutivo_id", nullable = false)
    private Ejecutivo ejecutivo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tipo_porta_id", nullable = false)
    private TipoPorta tipoPorta;

    // Formato: "1T2025", "2T2026"
    @Column(nullable = false, length = 20)
    private String trimestre;

    @Column(name = "fecha_asignacion", nullable = false)
    private LocalDate fechaAsignacion;

    @Column(name = "link_trello", length = 500)
    private String linkTrello;
}
