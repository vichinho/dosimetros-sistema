package com.dosimetros.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "tipo_dosimetro")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoDosimetro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 50)
    private String nombre;

    // Relación inversa — no genera columna en BD
    @OneToMany(mappedBy = "tipoDosimetro", fetch = FetchType.LAZY)
    private List<TipoPorta> tiposPorta;
}
