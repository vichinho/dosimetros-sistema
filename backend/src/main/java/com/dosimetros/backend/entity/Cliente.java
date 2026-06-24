package com.dosimetros.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cliente")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "razon_social", nullable = false, length = 300)
    private String razonSocial;

    @Column(name = "nombre_corto", length = 200)
    private String nombreCorto;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;
}
