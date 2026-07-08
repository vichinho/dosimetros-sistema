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
        // Con un trimestre filtrado, además el desglose por mes de ese trimestre.
        if (filtro != null) {
            kpis.setAsignacionesPorMes(asignacionesPorMes(filtro));
        }

        return kpis;
    }

    // Nombres de meses (índice 1-12) para el desglose por mes.
    private static final String[] MESES = {
            "", "Ene", "Feb", "Mar", "Abr", "May", "Jun",
            "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"
    };

    /**
     * Desglosa las asignaciones de un trimestre por sus 3 meses, rellenando
     * con cero los meses sin asignaciones. Formato del trimestre: 'QTYYYY'.
     */
    private List<ConteoClaveResponse> asignacionesPorMes(String trimestre) {
        java.util.Map<Integer, Long> conteos = new java.util.HashMap<>();
        for (Object[] o : asignacionRepository.contarPorMesEnTrimestre(trimestre)) {
            conteos.put(((Number) o[0]).intValue(), ((Number) o[1]).longValue());
        }
        int mesBase = mesBaseDeTrimestre(trimestre);
        if (mesBase == 0) {
            return List.of();
        }
        List<ConteoClaveResponse> filas = new java.util.ArrayList<>(3);
        for (int i = 0; i < 3; i++) {
            int mes = mesBase + i;
            filas.add(new ConteoClaveResponse(MESES[mes], conteos.getOrDefault(mes, 0L)));
        }
        return filas;
    }

    // Primer mes del trimestre (1,4,7,10) a partir de 'QTYYYY'; 0 si no es válido.
    private int mesBaseDeTrimestre(String trimestre) {
        if (trimestre == null || trimestre.length() < 6) {
            return 0;
        }
        int q = Character.getNumericValue(trimestre.charAt(0));
        if (q < 1 || q > 4) {
            return 0;
        }
        return (q - 1) * 3 + 1;
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
