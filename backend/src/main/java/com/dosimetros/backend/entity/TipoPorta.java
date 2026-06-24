package com.dosimetros.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tipo_porta")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoPorta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tipo_dosimetro_id", nullable = false)
    private TipoDosimetro tipoDosimetro;
}
