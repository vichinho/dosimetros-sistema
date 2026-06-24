package com.dosimetros.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuario")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "rol_id", nullable = false)
    private Rol rol;

    // NULL para admins sin ejecutivo asociado
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ejecutivo_id")
    private Ejecutivo ejecutivo;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;
}
