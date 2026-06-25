package com.dosimetros.backend.repository;

import com.dosimetros.backend.entity.Dosimetro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DosimetroRepository extends JpaRepository<Dosimetro, Integer> {

    Optional<Dosimetro> findByNumeroAndEstado(Integer numero, String estado);

    List<Dosimetro> findByEstadoOrderByNumeroAsc(String estado);

    List<Dosimetro> findByNumeroOrderByIdAsc(Integer numero);

    boolean existsByNumeroAndTipoDosimetroIdAndTipoPortaIdAndEstado(Integer numero, Integer tipoDosimetroId, Integer tipoPortaId, String estado);
}