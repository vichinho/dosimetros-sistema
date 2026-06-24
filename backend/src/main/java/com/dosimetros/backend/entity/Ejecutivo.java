package com.dosimetros.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ejecutivo")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ejecutivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 200)
    private String nombre;

    @Column(length = 200)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;
}
