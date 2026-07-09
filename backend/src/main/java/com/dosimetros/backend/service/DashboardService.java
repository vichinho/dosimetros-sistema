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

        // Al filtrar por un trimestre, se agrega el desglose por mes de ESE
        // trimestre para que el gráfico temporal muestre sus meses (#2/#3).
        if (filtro != null) {
            kpis.setAsignacionesPorMes(mapearMeses(asignacionRepository.contarPorMesEnTrimestre(filtro)));
        }

        return kpis;
    }

    /**
     * Stock histórico (HU #4): dosímetros existentes en el inventario a una
     * fecha dada, desglosados por tipo de porta.
     */
    public StockHistoricoResponse obtenerStockHistorico(LocalDate fecha) {
        LocalDate corte = (fecha == null) ? LocalDate.now() : fecha;
        long total = dosimetroRepository.contarExistentesHasta(corte);
        List<ConteoClaveResponse> porPorta = mapearClave(dosimetroRepository.stockHistoricoPorPorta(corte));
        return new StockHistoricoResponse(corte, total, porPorta);
    }

    private static final String[] MESES = {
            "Ene", "Feb", "Mar", "Abr", "May", "Jun",
            "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"
    };

    private List<ConteoClaveResponse> mapearMeses(List<Object[]> filas) {
        return filas.stream()
                .map(o -> {
                    int mes = ((Number) o[0]).intValue(); // 1..12
                    String etiqueta = (mes >= 1 && mes <= 12) ? MESES[mes - 1] : String.valueOf(mes);
                    return new ConteoClaveResponse(etiqueta, (Long) o[1]);
                })
                .toList();
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
