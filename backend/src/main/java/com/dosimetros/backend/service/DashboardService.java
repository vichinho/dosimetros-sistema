package com.dosimetros.backend.service;

import com.dosimetros.backend.dto.dashboard.ConteoClaveResponse;
import com.dosimetros.backend.dto.dashboard.ConteoResponse;
import com.dosimetros.backend.dto.dashboard.DashboardKpisResponse;
import com.dosimetros.backend.dto.dashboard.StockHistoricoResponse;
import com.dosimetros.backend.repository.AsignacionRepository;
import com.dosimetros.backend.repository.DosimetroRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Calcula los KPIs del dashboard (HU #17) mediante agregaciones en BD.
 * Las métricas de asignaciones se pueden filtrar por trimestre.
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

    public DashboardKpisResponse obtenerKpis(String trimestre) {
        String filtro = (trimestre == null || trimestre.isBlank()) ? null : trimestre.trim();

        DashboardKpisResponse kpis = new DashboardKpisResponse();
        kpis.setTrimestre(filtro);
        kpis.setTrimestresDisponibles(asignacionRepository.trimestresDistinct());

        // --- Stock (estado actual, no depende del trimestre) ---
        List<ConteoClaveResponse> porEstado = mapearClave(dosimetroRepository.contarPorEstado());
        kpis.setDosimetrosPorEstado(porEstado);
        kpis.setTotalDosimetros(dosimetroRepository.count());
        kpis.setDisponibles(contarEstado(porEstado, "disponible"));
        kpis.setAsignados(contarEstado(porEstado, "asignado"));
        kpis.setDanados(contarEstado(porEstado, "dañado"));
        kpis.setBaja(contarEstado(porEstado, "baja"));
        kpis.setDosimetrosPorTipo(mapearConteo(dosimetroRepository.contarPorTipoDosimetro()));
        kpis.setDisponiblesPorPorta(mapearConteo(dosimetroRepository.contarDisponiblesPorPorta()));

        // --- Asignaciones (según filtro de trimestre) ---
        kpis.setTotalAsignaciones(asignacionRepository.contarAsignaciones(filtro));
        kpis.setAsignacionesPorEmpresa(mapearConteo(asignacionRepository.contarPorEmpresa(filtro)));
        kpis.setAsignacionesPorEjecutivo(mapearConteo(asignacionRepository.contarPorEjecutivo(filtro)));
        kpis.setAsignacionesPorTipoPorta(mapearConteo(asignacionRepository.contarPorTipoPorta(filtro)));
        kpis.setTopClientes(mapearConteo(asignacionRepository.contarPorCliente(filtro)));
        // La evolución por trimestre siempre muestra todos los trimestres.
        kpis.setAsignacionesPorTrimestre(mapearClave(asignacionRepository.contarPorTrimestre()));

        return kpis;
    }

    /**
     * Stock del inventario a una fecha pasada: cuántos dosímetros existían
     * (habían sido creados) en o antes de esa fecha, desglosado por tipo de
     * porta y por tipo de dosímetro.
     */
    public StockHistoricoResponse stockHistorico(LocalDate fecha) {
        LocalDate f = (fecha == null) ? LocalDate.now() : fecha;
        return new StockHistoricoResponse(
                f,
                dosimetroRepository.contarCreadosHasta(f),
                mapearConteo(dosimetroRepository.contarPorPortaHasta(f)),
                mapearConteo(dosimetroRepository.contarPorTipoHasta(f)));
    }

    private long contarEstado(List<ConteoClaveResponse> porEstado, String estado) {
        return porEstado.stream()
                .filter(e -> estado.equalsIgnoreCase(e.getClave()))
                .map(ConteoClaveResponse::getCantidad)
                .findFirst()
                .orElse(0L);
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
