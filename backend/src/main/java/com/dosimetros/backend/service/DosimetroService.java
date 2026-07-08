package com.dosimetros.backend.service;

import com.dosimetros.backend.dto.asignacion.AsignacionResponse;
import com.dosimetros.backend.dto.dosimetro.ActualizarTipoPortaRangoRequest;
import com.dosimetros.backend.dto.dosimetro.ActualizarTipoPortaRangoResponse;
import com.dosimetros.backend.dto.dosimetro.DosimetroDetalleResponse;
import com.dosimetros.backend.dto.dosimetro.DosimetroRequest;
import com.dosimetros.backend.dto.dosimetro.DosimetroResponse;
import com.dosimetros.backend.dto.dosimetro.DuplicadoResponse;
import com.dosimetros.backend.dto.dosimetro.PortaDisponibleResponse;
import com.dosimetros.backend.dto.tarea.TareaDisponibleResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import com.dosimetros.backend.entity.Asignacion;
import com.dosimetros.backend.entity.Dosimetro;
import com.dosimetros.backend.entity.Tarea;
import com.dosimetros.backend.entity.TipoDosimetro;
import com.dosimetros.backend.entity.TipoPorta;
import com.dosimetros.backend.exception.ResourceNotFoundException;
import com.dosimetros.backend.repository.AsignacionRepository;
import com.dosimetros.backend.repository.DosimetroRepository;
import com.dosimetros.backend.repository.TareaRepository;
import com.dosimetros.backend.repository.TipoDosimetroRepository;
import com.dosimetros.backend.repository.TipoPortaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class DosimetroService {

    private final DosimetroRepository dosimetroRepository;
    private final TipoDosimetroRepository tipoDosimetroRepository;
    private final TipoPortaRepository tipoPortaRepository;
    private final TareaRepository tareaRepository;
    private final AsignacionRepository asignacionRepository;

    public DosimetroService(DosimetroRepository dosimetroRepository,
                            TipoDosimetroRepository tipoDosimetroRepository,
                            TipoPortaRepository tipoPortaRepository,
                            TareaRepository tareaRepository,
                            AsignacionRepository asignacionRepository) {
        this.dosimetroRepository = dosimetroRepository;
        this.tipoDosimetroRepository = tipoDosimetroRepository;
        this.tipoPortaRepository = tipoPortaRepository;
        this.tareaRepository = tareaRepository;
        this.asignacionRepository = asignacionRepository;
    }

    public List<DosimetroResponse> listarDisponibles() {
        return dosimetroRepository.findByEstadoOrderByNumeroAsc("disponible")
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // HU #7: stock filtrado por tipo de dosímetro, estado de armado (tipo de
    // porta, incluida "sin armar") y estado del dosímetro.
    public List<DosimetroResponse> filtrarStock(Integer tipoDosimetroId, Integer tipoPortaId, String estado) {
        String estadoFinal = (estado == null || estado.isBlank()) ? "disponible" : estado;
        return dosimetroRepository.filtrar(tipoDosimetroId, tipoPortaId, estadoFinal)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Tareas que tienen al menos un dosímetro disponible (opcional: de un tipo).
    public List<TareaDisponibleResponse> tareasConDisponibles(Integer tipoDosimetroId) {
        return dosimetroRepository.tareasConDisponibles(tipoDosimetroId).stream()
                .map(o -> new TareaDisponibleResponse((Integer) o[0], (String) o[1], (Long) o[2]))
                .toList();
    }

    // Detalle de stock disponible agrupado por porta (con su tipo de dosímetro).
    public List<PortaDisponibleResponse> detallePortasDisponibles() {
        return dosimetroRepository.detallePortasDisponibles().stream()
                .map(o -> new PortaDisponibleResponse(
                        (Integer) o[0], (String) o[1], (String) o[2], (Long) o[3]))
                .toList();
    }

    // Exporta el stock filtrado a un archivo Excel (.xlsx).
    public byte[] exportarStockExcel(Integer tipoDosimetroId, Integer tipoPortaId, String estado) {
        String estadoFinal = (estado == null || estado.isBlank()) ? null : estado;
        List<Dosimetro> dosimetros = dosimetroRepository.filtrar(tipoDosimetroId, tipoPortaId, estadoFinal);

        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet("Stock");
            String[] cabeceras = {"Número", "Tipo", "Porta", "Tarea", "Bandeja", "Slot", "Estado", "Observación"};
            Row header = sheet.createRow(0);
            for (int i = 0; i < cabeceras.length; i++) {
                header.createCell(i).setCellValue(cabeceras[i]);
            }

            int fila = 1;
            for (Dosimetro d : dosimetros) {
                Row row = sheet.createRow(fila++);
                row.createCell(0).setCellValue(d.getNumero() != null ? d.getNumero() : 0);
                row.createCell(1).setCellValue(d.getTipoDosimetro() != null ? d.getTipoDosimetro().getNombre() : "");
                row.createCell(2).setCellValue(d.getTipoPorta() != null ? d.getTipoPorta().getNombre() : "");
                row.createCell(3).setCellValue(d.getTarea() != null ? d.getTarea().getNumeroTarea() : "");
                if (d.getNumeroBandeja() != null) row.createCell(4).setCellValue(d.getNumeroBandeja());
                if (d.getSlotBandeja() != null) row.createCell(5).setCellValue(d.getSlotBandeja());
                row.createCell(6).setCellValue(d.getEstado() != null ? d.getEstado() : "");
                row.createCell(7).setCellValue(d.getObservacion() != null ? d.getObservacion() : "");
            }
            for (int i = 0; i < cabeceras.length; i++) {
                sheet.autoSizeColumn(i);
            }

            wb.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error al generar el Excel de stock", e);
        }
    }

    public List<DosimetroResponse> buscarPorNumero(Integer numero) {
        return dosimetroRepository.findByNumeroOrderByIdAsc(numero)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Buscar: solo dosímetros que ya fueron asignados (tienen historial de asignación).
    public List<DosimetroDetalleResponse> buscarDetallePorNumero(Integer numero) {
        List<Dosimetro> dosimetros = dosimetroRepository.findByNumeroConAsignacion(numero);
        if (dosimetros.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No se encontraron dosímetros asignados con número: " + numero);
        }
        return dosimetros.stream().map(this::toDetalleResponse).toList();
    }

    public DosimetroResponse obtenerPorId(Integer id) {
        Dosimetro dosimetro = dosimetroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dosímetro no encontrado con id: " + id));
        return toResponse(dosimetro);
    }

    public DosimetroResponse crear(DosimetroRequest request) {
        TipoDosimetro tipoDosimetro = tipoDosimetroRepository.findById(request.getTipoDosimetroId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tipo de dosímetro no encontrado con id: " + request.getTipoDosimetroId()));

        TipoPorta tipoPorta = resolverTipoPorta(request.getTipoPortaId(), tipoDosimetro);
        Tarea tarea = resolverTarea(request, tipoDosimetro);

        Dosimetro dosimetro = new Dosimetro();
        dosimetro.setNumero(request.getNumero());
        dosimetro.setTipoDosimetro(tipoDosimetro);
        dosimetro.setTipoPorta(tipoPorta);
        dosimetro.setTarea(tarea);
        dosimetro.setNumeroBandeja(request.getNumeroBandeja());
        dosimetro.setSlotBandeja(request.getSlotBandeja());
        dosimetro.setEstado(request.getEstado() != null ? request.getEstado() : "disponible");
        dosimetro.setObservacion(request.getObservacion());
        dosimetro.setFechaCreacion(
                tarea != null ? tarea.getFechaCreacion() : java.time.LocalDate.now());

        return toResponse(dosimetroRepository.save(dosimetro));
    }

    public DosimetroResponse actualizar(Integer id, DosimetroRequest request) {
        Dosimetro dosimetro = dosimetroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dosímetro no encontrado con id: " + id));

        TipoDosimetro tipoDosimetro = tipoDosimetroRepository.findById(request.getTipoDosimetroId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tipo de dosímetro no encontrado con id: " + request.getTipoDosimetroId()));

        TipoPorta tipoPorta = resolverTipoPorta(request.getTipoPortaId(), tipoDosimetro);
        Tarea tarea = resolverTarea(request, tipoDosimetro);

        dosimetro.setNumero(request.getNumero());
        dosimetro.setTipoDosimetro(tipoDosimetro);
        dosimetro.setTipoPorta(tipoPorta);
        dosimetro.setTarea(tarea);
        dosimetro.setNumeroBandeja(request.getNumeroBandeja());
        dosimetro.setSlotBandeja(request.getSlotBandeja());
        dosimetro.setEstado(request.getEstado() != null ? request.getEstado() : dosimetro.getEstado());
        dosimetro.setObservacion(request.getObservacion());

        return toResponse(dosimetroRepository.save(dosimetro));
    }

    @Transactional
    public void liberar(Integer id) {
        Dosimetro dosimetro = dosimetroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dosímetro no encontrado con id: " + id));
        if ("baja".equalsIgnoreCase(dosimetro.getEstado())) {
            throw new IllegalArgumentException("No se puede liberar un dosímetro dado de baja");
        }
        dosimetro.setEstado("disponible");
        dosimetroRepository.save(dosimetro);
    }

    // HU #9: lista los dosímetros con número repetido, agrupados por número.
    public List<DuplicadoResponse> listarDuplicados() {
        Map<Integer, List<DosimetroResponse>> porNumero = new LinkedHashMap<>();
        for (Dosimetro d : dosimetroRepository.findDuplicados()) {
            porNumero.computeIfAbsent(d.getNumero(), k -> new ArrayList<>()).add(toResponse(d));
        }
        return porNumero.entrySet().stream()
                .map(e -> new DuplicadoResponse(e.getKey(), e.getValue().size(), e.getValue()))
                .toList();
    }

    // HU #9: marca un dosímetro como stock de emergencia en su observación.
    public DosimetroResponse marcarStockEmergencia(Integer id, String observacion) {
        Dosimetro dosimetro = dosimetroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dosímetro no encontrado con id: " + id));
        String obs = (observacion == null || observacion.isBlank()) ? "stock emergencia" : observacion;
        dosimetro.setObservacion(obs);
        return toResponse(dosimetroRepository.save(dosimetro));
    }

    // Marca un dosímetro como dañado (no podrá asignarse). Debe estar libre.
    public DosimetroResponse marcarDanado(Integer id) {
        Dosimetro dosimetro = dosimetroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dosímetro no encontrado con id: " + id));
        if ("asignado".equalsIgnoreCase(dosimetro.getEstado())) {
            throw new IllegalArgumentException(
                    "El dosímetro está asignado; libéralo antes de marcarlo como dañado");
        }
        dosimetro.setEstado("dañado");
        return toResponse(dosimetroRepository.save(dosimetro));
    }

    // Marca como bueno un dosímetro dañado (vuelve a disponible).
    public DosimetroResponse marcarBueno(Integer id) {
        Dosimetro dosimetro = dosimetroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dosímetro no encontrado con id: " + id));
        if (!"dañado".equalsIgnoreCase(dosimetro.getEstado())) {
            throw new IllegalArgumentException("El dosímetro no está marcado como dañado");
        }
        dosimetro.setEstado("disponible");
        return toResponse(dosimetroRepository.save(dosimetro));
    }

    public void darDeBaja(Integer id, String observacion) {
        Dosimetro dosimetro = dosimetroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dosímetro no encontrado con id: " + id));
        dosimetro.setEstado("baja");
        dosimetro.setObservacion(observacion);
        dosimetroRepository.save(dosimetro);
    }

    /**
     * HU #6 — Actualiza el tipo de porta de todos los dosímetros de una tarea
     * que caigan dentro del rango de bandeja y slot indicados.
     */
    @Transactional
    public ActualizarTipoPortaRangoResponse actualizarTipoPortaPorRango(
            ActualizarTipoPortaRangoRequest request) {

        if (request.getBandejaDesde() > request.getBandejaHasta()) {
            throw new IllegalArgumentException("bandejaDesde no puede ser mayor que bandejaHasta");
        }
        if (request.getSlotDesde() != null && request.getSlotHasta() != null
                && request.getSlotDesde() > request.getSlotHasta()) {
            throw new IllegalArgumentException("slotDesde no puede ser mayor que slotHasta");
        }

        TipoPorta tipoPorta = tipoPortaRepository.findById(request.getTipoPortaId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tipo de porta no encontrado con id: " + request.getTipoPortaId()));

        List<Dosimetro> dosimetros = dosimetroRepository.findByTareaYRangoBandejaSlot(
                request.getTareaId(),
                request.getBandejaDesde(),
                request.getBandejaHasta(),
                request.getSlotDesde(),
                request.getSlotHasta()
        );

        for (Dosimetro d : dosimetros) {
            // Validar compatibilidad porta/dosimetro
            if (!tipoPorta.getTipoDosimetro().getId().equals(d.getTipoDosimetro().getId())) {
                throw new IllegalArgumentException(
                        "El tipo de porta '" + tipoPorta.getNombre() +
                        "' no es compatible con el dosímetro #" + d.getNumero() +
                        " (tipo: " + d.getTipoDosimetro().getNombre() + ")");
            }
            d.setTipoPorta(tipoPorta);
        }

        dosimetroRepository.saveAll(dosimetros);
        return new ActualizarTipoPortaRangoResponse(dosimetros.size());
    }

    // --- helpers privados ---

    private TipoPorta resolverTipoPorta(Integer tipoPortaId, TipoDosimetro tipoDosimetro) {
        if (tipoPortaId == null) return null;
        TipoPorta tp = tipoPortaRepository.findById(tipoPortaId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tipo de porta no encontrado con id: " + tipoPortaId));
        if (!tp.getTipoDosimetro().getId().equals(tipoDosimetro.getId())) {
            throw new IllegalArgumentException("El tipo de porta no es compatible con el tipo de dosímetro");
        }
        return tp;
    }

    private Tarea resolverTarea(DosimetroRequest request, TipoDosimetro tipoDosimetro) {
        if ("OSL".equalsIgnoreCase(tipoDosimetro.getNombre())) {
            request.setTareaId(null);
            request.setNumeroBandeja(null);
            request.setSlotBandeja(null);
            return null;
        }
        if (request.getTareaId() == null) return null;
        return tareaRepository.findById(request.getTareaId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tarea no encontrada con id: " + request.getTareaId()));
    }

    private DosimetroResponse toResponse(Dosimetro d) {
        return new DosimetroResponse(
                d.getId(),
                d.getNumero(),
                d.getTipoDosimetro().getId(),
                d.getTipoDosimetro().getNombre(),
                d.getTipoPorta() != null ? d.getTipoPorta().getId() : null,
                d.getTipoPorta() != null ? d.getTipoPorta().getNombre() : null,
                d.getTarea() != null ? d.getTarea().getId() : null,
                d.getTarea() != null ? d.getTarea().getNumeroTarea() : null,
                d.getNumeroBandeja(),
                d.getSlotBandeja(),
                d.getEstado(),
                d.getObservacion()
        );
    }

    private DosimetroDetalleResponse toDetalleResponse(Dosimetro d) {
        List<AsignacionResponse> asignaciones = asignacionRepository
                .findByDosimetroIdOrderByFechaAsignacionDesc(d.getId())
                .stream()
                .map(this::toAsignacionResponse)
                .toList();

        return new DosimetroDetalleResponse(
                d.getId(),
                d.getNumero(),
                d.getTipoDosimetro().getId(),
                d.getTipoDosimetro().getNombre(),
                d.getTipoPorta() != null ? d.getTipoPorta().getId() : null,
                d.getTipoPorta() != null ? d.getTipoPorta().getNombre() : null,
                d.getTarea() != null ? d.getTarea().getId() : null,
                d.getTarea() != null ? d.getTarea().getNumeroTarea() : null,
                d.getNumeroBandeja(),
                d.getSlotBandeja(),
                d.getEstado(),
                d.getObservacion(),
                asignaciones
        );
    }

    private AsignacionResponse toAsignacionResponse(Asignacion a) {
        return new AsignacionResponse(
                a.getId(),
                a.getDosimetro().getId(),
                a.getDosimetro().getNumero(),
                a.getCliente().getId(),
                a.getCliente().getRazonSocial(),
                a.getEjecutivo().getId(),
                a.getEjecutivo().getNombre(),
                a.getEmpresa().getId(),
                a.getEmpresa().getNombre(),
                a.getTipoPorta().getId(),
                a.getTipoPorta().getNombre(),
                a.getTarea() != null ? a.getTarea().getId() : null,
                a.getTarea() != null ? a.getTarea().getNumeroTarea() : null,
                a.getNumeroBandeja(),
                a.getSlotBandeja(),
                a.getTrimestre(),
                a.getFechaAsignacion(),
                a.getLinkTrello()
        );
    }
}
