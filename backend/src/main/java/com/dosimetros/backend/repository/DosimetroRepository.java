package com.dosimetros.backend.repository;

import com.dosimetros.backend.entity.Dosimetro;
import com.dosimetros.backend.entity.EstadoDosimetro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DosimetroRepository extends JpaRepository<Dosimetro, Integer> {

    // HU-14: buscar por número físico (puede retornar múltiples por duplicados)
    List<Dosimetro> findByNumero(Integer numero);

    // HU-7: stock disponible filtrado por tipo y estado
    List<Dosimetro> findByTipoDosimetroIdAndEstado(Integer tipoDosimetroId, EstadoDosimetro estado);

    // HU-10: dosímetros disponibles en una tarea con tipo de porta específico
    List<Dosimetro> findByTareaIdAndTipoPortaIdAndEstado(
            Integer tareaId,
            Integer tipoPortaId,
            EstadoDosimetro estado
    );

    // HU-9: detectar números físicos duplicados
    @Query("SELECT d.numero FROM Dosimetro d GROUP BY d.numero HAVING COUNT(d.numero) > 1")
    List<Integer> findNumerosDuplicados();

    // Conteo de disponibles por tipo (para KPIs - HU-17)
    @Query("SELECT d.tipoDosimetro.nombre, COUNT(d) FROM Dosimetro d " +
           "WHERE d.estado = 'disponible' GROUP BY d.tipoDosimetro.nombre")
    List<Object[]> countDisponiblesByTipo();
}
