package com.dosimetros.backend.service;

import com.dosimetros.backend.dto.dashboard.ConteoClaveResponse;
import com.dosimetros.backend.dto.dashboard.ConteoResponse;
import com.dosimetros.backend.dto.dashboard.DashboardKpisResponse;
import com.dosimetros.backend.repository.AsignacionRepository;
import com.dosimetros.backend.repository.DosimetroRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Calcula los KPIs del dashboard (HU #17) mediante agregaciones en BD.
 */
@Service
public class DashboardService {

    private final DosimetroRepository dosimetroRepository;
    private final AsignacionRepository asignacionRepository;

    public DashboardService(DosimetroRepository dosimetroRepository,
                            AsignacionRepository asignacionRepository) {
        this.dosimetroRepository = dosimetroRepository;
        this.asignacionRepository = asignacionRepository;
    }

    public DashboardKpisResponse obtenerKpis() {
        DashboardKpisResponse kpis = new DashboardKpisResponse();

        kpis.setTotalDosimetros(dosimetroRepository.count());
        kpis.setTotalAsignaciones(asignacionRepository.count());

        kpis.setDosimetrosPorEstado(mapearClave(dosimetroRepository.contarPorEstado()));
        kpis.setDosimetrosPorTipo(mapearConteo(dosimetroRepository.contarPorTipoDosimetro()));
        kpis.setAsignacionesPorEmpresa(mapearConteo(asignacionRepository.contarPorEmpresa()));
        kpis.setAsignacionesPorEjecutivo(mapearConteo(asignacionRepository.contarPorEjecutivo()));
        kpis.setAsignacionesPorTrimestre(mapearClave(asignacionRepository.contarPorTrimestre()));

        return kpis;
    }

    private List<ConteoResponse> mapearConteo(List<Object[]> filas) {
        return filas.stream()
                .map(o -> new ConteoResponse((Integer) o[0], (String) o[1], (Long) o[2]))
                .toList();
    }

    private List<ConteoClaveResponse> mapearClave(List<Object[]> filas) {
        return filas.stream()
                .map(o -> new ConteoClaveResponse((String) o[0], (Long) o[1]))
                .toList();
    }
}
