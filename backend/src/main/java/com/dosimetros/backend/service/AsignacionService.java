package com.dosimetros.backend.service;

import com.dosimetros.backend.dto.asignacion.AsignacionMasivaRequest;
import com.dosimetros.backend.dto.asignacion.AsignacionMasivaResponse;
import com.dosimetros.backend.dto.asignacion.AsignacionRequest;
import com.dosimetros.backend.dto.asignacion.AsignacionResponse;
import com.dosimetros.backend.dto.asignacion.CorreccionMasivaRequest;
import com.dosimetros.backend.dto.asignacion.EditarAsignacionRequest;
import com.dosimetros.backend.dto.asignacion.LoteAsignacionResponse;
import com.dosimetros.backend.dto.asignacion.MisFiltrosResponse;
import com.dosimetros.backend.dto.asignacion.OpcionResponse;
import com.dosimetros.backend.entity.Asignacion;
import com.dosimetros.backend.entity.Cliente;
import com.dosimetros.backend.entity.Dosimetro;
import com.dosimetros.backend.entity.Ejecutivo;
import com.dosimetros.backend.entity.Empresa;
import com.dosimetros.backend.entity.TipoPorta;
import com.dosimetros.backend.exception.ResourceNotFoundException;
import com.dosimetros.backend.repository.AsignacionRepository;
import com.dosimetros.backend.repository.ClienteRepository;
import com.dosimetros.backend.repository.DosimetroRepository;
import com.dosimetros.backend.repository.EjecutivoRepository;
import com.dosimetros.backend.repository.EmpresaRepository;
import com.dosimetros.backend.repository.TipoPortaRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AsignacionService {

    private final AsignacionRepository asignacionRepository;
    private final DosimetroRepository dosimetroRepository;
    private final ClienteRepository clienteRepository;
    private final EjecutivoRepository ejecutivoRepository;
    private final EmpresaRepository empresaRepository;
    private final TipoPortaRepository tipoPortaRepository;

    public AsignacionService(AsignacionRepository asignacionRepository,
                             DosimetroRepository dosimetroRepository,
                             ClienteRepository clienteRepository,
                             EjecutivoRepository ejecutivoRepository,
                             EmpresaRepository empresaRepository,
                             TipoPortaRepository tipoPortaRepository) {
        this.asignacionRepository = asignacionRepository;
        this.dosimetroRepository = dosimetroRepository;
        this.clienteRepository = clienteRepository;
        this.ejecutivoRepository = ejecutivoRepository;
        this.empresaRepository = empresaRepository;
        this.tipoPortaRepository = tipoPortaRepository;
    }

    public List<AsignacionResponse> listarPorDosimetro(Integer dosimetroId) {
        return asignacionRepository.findByDosimetroIdOrderByFechaAsignacionDesc(dosimetroId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // HU #3 / #15: dosímetros asignados a los clientes del ejecutivo logueado.
    public List<AsignacionResponse> listarPorEjecutivo(Integer ejecutivoId) {
        return asignacionRepository
                .findByEjecutivoIdOrderByTrimestreDescFechaAsignacionDesc(ejecutivoId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Asignaciones de un cliente (para el detalle del cliente).
    public List<AsignacionResponse> listarPorCliente(Integer clienteId) {
        return asignacionRepository.findByClienteIdOrderByFechaAsignacionDesc(clienteId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Vista ejecutivo con multifiltros (todos opcionales).
    public List<AsignacionResponse> filtrarPorEjecutivo(
            Integer ejecutivoId, Integer clienteId, String trimestre, java.time.LocalDate fecha,
            Integer tipoPortaId, Integer numeroBandeja, Integer slotBandeja, String link) {
        String tri = (trimestre == null || trimestre.isBlank()) ? null : trimestre.trim();
        String lk = (link == null || link.isBlank()) ? null : link.trim();
        return asignacionRepository.filtrarPorEjecutivo(
                        ejecutivoId, clienteId, tri, fecha, tipoPortaId, numeroBandeja, slotBandeja, lk)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Opciones disponibles para los filtros del ejecutivo.
    public MisFiltrosResponse opcionesFiltroEjecutivo(Integer ejecutivoId) {
        List<OpcionResponse> clientes = asignacionRepository.clientesDeEjecutivo(ejecutivoId).stream()
                .map(o -> new OpcionResponse((Integer) o[0], (String) o[1]))
                .toList();
        List<OpcionResponse> portas = asignacionRepository.portasDeEjecutivo(ejecutivoId).stream()
                .map(o -> new OpcionResponse((Integer) o[0], (String) o[1]))
                .toList();
        return new MisFiltrosResponse(
                asignacionRepository.trimestresDeEjecutivo(ejecutivoId), clientes, portas);
    }

    // HU #15: asignaciones del ejecutivo agrupadas por trimestre y fecha (lotes enviados).
    public List<LoteAsignacionResponse> listarLotesPorEjecutivo(Integer ejecutivoId) {
        List<AsignacionResponse> asignaciones = listarPorEjecutivo(ejecutivoId);

        Map<String, LoteAsignacionResponse> lotes = new LinkedHashMap<>();
        for (AsignacionResponse a : asignaciones) {
            String clave = a.getTrimestre() + "|" + a.getFechaAsignacion();
            LoteAsignacionResponse lote = lotes.computeIfAbsent(clave, k ->
                    new LoteAsignacionResponse(
                            a.getTrimestre(),
                            a.getFechaAsignacion(),
                            0,
                            new ArrayList<>()
                    ));
            lote.getAsignaciones().add(a);
        }

        List<LoteAsignacionResponse> resultado = new ArrayList<>(lotes.values());
        resultado.forEach(lote -> lote.setCantidad(lote.getAsignaciones().size()));
        return resultado;
    }

    @Transactional
    public AsignacionResponse crear(AsignacionRequest request) {
        Dosimetro dosimetro = dosimetroRepository.findById(request.getDosimetroId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Dosímetro no encontrado con id: " + request.getDosimetroId()
                ));

        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cliente no encontrado con id: " + request.getClienteId()
                ));

        Ejecutivo ejecutivo = ejecutivoRepository.findById(request.getEjecutivoId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ejecutivo no encontrado con id: " + request.getEjecutivoId()
                ));

        Empresa empresa = empresaRepository.findById(request.getEmpresaId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Empresa no encontrada con id: " + request.getEmpresaId()
                ));

        TipoPorta tipoPorta = tipoPortaRepository.findById(request.getTipoPortaId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tipo de porta no encontrado con id: " + request.getTipoPortaId()
                ));

        if (!"disponible".equalsIgnoreCase(dosimetro.getEstado())) {
            throw new IllegalArgumentException("El dosímetro no está disponible para asignación");
        }

        if (!tipoPorta.getTipoDosimetro().getId().equals(dosimetro.getTipoDosimetro().getId())) {
            throw new IllegalArgumentException("El tipo de porta no es compatible con el tipo de dosímetro");
        }

        Asignacion asignacion = new Asignacion();
        asignacion.setDosimetro(dosimetro);
        asignacion.setCliente(cliente);
        asignacion.setEjecutivo(ejecutivo);
        asignacion.setEmpresa(empresa);
        asignacion.setTipoPorta(tipoPorta);
        asignacion.setTarea(dosimetro.getTarea());
        asignacion.setNumeroBandeja(dosimetro.getNumeroBandeja());
        asignacion.setSlotBandeja(dosimetro.getSlotBandeja());
        asignacion.setTrimestre(request.getTrimestre());
        asignacion.setFechaAsignacion(
                request.getFechaAsignacion() != null ? request.getFechaAsignacion() : LocalDate.now()
        );
        asignacion.setLinkTrello(request.getLinkTrello());

        Asignacion guardada = asignacionRepository.save(asignacion);

        dosimetro.setEstado("asignado");
        dosimetroRepository.save(dosimetro);

        return toResponse(guardada);
    }

    @Transactional
    public AsignacionMasivaResponse asignarMasivo(AsignacionMasivaRequest request) {
        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cliente no encontrado con id: " + request.getClienteId()
                ));

        Ejecutivo ejecutivo = ejecutivoRepository.findById(request.getEjecutivoId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ejecutivo no encontrado con id: " + request.getEjecutivoId()
                ));

        Empresa empresa = empresaRepository.findById(request.getEmpresaId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Empresa no encontrada con id: " + request.getEmpresaId()
                ));

        TipoPorta tipoPorta = tipoPortaRepository.findById(request.getTipoPortaId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tipo de porta no encontrado con id: " + request.getTipoPortaId()
                ));

        // Solo se eligen dosímetros disponibles dentro de las tareas seleccionadas
        // cuyo tipo de dosímetro sea compatible con el tipo de porta solicitado.
        List<Dosimetro> compatibles = dosimetroRepository.findDisponiblesCompatiblesEnTareas(
                request.getTareaIds(),
                tipoPorta.getTipoDosimetro().getId()
        );

        if (compatibles.size() < request.getCantidad()) {
            throw new IllegalArgumentException(
                    "Stock insuficiente: se solicitaron " + request.getCantidad()
                            + " dosímetros compatibles pero solo hay " + compatibles.size()
                            + " disponibles en las tareas seleccionadas"
            );
        }

        LocalDate fecha = request.getFechaAsignacion() != null
                ? request.getFechaAsignacion()
                : LocalDate.now();

        List<Dosimetro> seleccionados = compatibles.subList(0, request.getCantidad());
        List<AsignacionResponse> asignaciones = new ArrayList<>(seleccionados.size());

        for (Dosimetro dosimetro : seleccionados) {
            Asignacion asignacion = new Asignacion();
            asignacion.setDosimetro(dosimetro);
            asignacion.setCliente(cliente);
            asignacion.setEjecutivo(ejecutivo);
            asignacion.setEmpresa(empresa);
            asignacion.setTipoPorta(tipoPorta);
            asignacion.setTarea(dosimetro.getTarea());
            asignacion.setNumeroBandeja(dosimetro.getNumeroBandeja());
            asignacion.setSlotBandeja(dosimetro.getSlotBandeja());
            asignacion.setTrimestre(request.getTrimestre());
            asignacion.setFechaAsignacion(fecha);
            asignacion.setLinkTrello(request.getLinkTrello());

            Asignacion guardada = asignacionRepository.save(asignacion);

            dosimetro.setEstado("asignado");
            dosimetroRepository.save(dosimetro);

            asignaciones.add(toResponse(guardada));
        }

        return new AsignacionMasivaResponse(
                request.getCantidad(),
                asignaciones.size(),
                asignaciones
        );
    }

    // Exporta a Excel un conjunto de asignaciones por sus ids (resumen de asignación).
    public byte[] exportarPorIds(List<Integer> ids) {
        List<AsignacionResponse> asignaciones = asignacionRepository.findAllById(ids)
                .stream().map(this::toResponse).toList();
        return exportarAsignacionesExcel(asignaciones);
    }

    /**
     * #15: exporta una lista de asignaciones a Excel (.xlsx). Incluye la tarea.
     */
    public byte[] exportarAsignacionesExcel(List<AsignacionResponse> asignaciones) {
        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet("Asignaciones");
            String[] cab = {"N° dosímetro", "Cliente", "Ejecutivo", "Empresa", "Trimestre",
                    "Fecha asignación", "Porta", "Tarea", "Bandeja", "Slot", "Link Trello"};
            Row header = sheet.createRow(0);
            for (int i = 0; i < cab.length; i++) header.createCell(i).setCellValue(cab[i]);

            int r = 1;
            for (AsignacionResponse a : asignaciones) {
                Row row = sheet.createRow(r++);
                if (a.getNumeroDosimetro() != null) row.createCell(0).setCellValue(a.getNumeroDosimetro());
                row.createCell(1).setCellValue(nvl(a.getClienteNombre()));
                row.createCell(2).setCellValue(nvl(a.getEjecutivoNombre()));
                row.createCell(3).setCellValue(nvl(a.getEmpresaNombre()));
                row.createCell(4).setCellValue(nvl(a.getTrimestre()));
                row.createCell(5).setCellValue(a.getFechaAsignacion() != null ? a.getFechaAsignacion().toString() : "");
                row.createCell(6).setCellValue(nvl(a.getTipoPortaNombre()));
                row.createCell(7).setCellValue(nvl(a.getNumeroTarea()));
                if (a.getNumeroBandeja() != null) row.createCell(8).setCellValue(a.getNumeroBandeja());
                if (a.getSlotBandeja() != null) row.createCell(9).setCellValue(a.getSlotBandeja());
                row.createCell(10).setCellValue(nvl(a.getLinkTrello()));
            }
            wb.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error al generar el Excel de asignaciones", e);
        }
    }

    private String nvl(String v) {
        return v == null ? "" : v;
    }

    // #18: asignaciones por estado de envío (enviado=false => pendientes).
    public List<AsignacionResponse> porEstadoEnvio(Integer ejecutivoId, String trimestre, boolean enviado) {
        String tri = (trimestre == null || trimestre.isBlank()) ? null : trimestre.trim();
        return asignacionRepository.porEstadoEnvio(ejecutivoId, tri, enviado)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // #18: marca un conjunto de asignaciones como enviadas (o revierte).
    @Transactional
    public int marcarEnvio(List<Integer> asignacionIds, boolean enviado) {
        List<Asignacion> asignaciones = asignacionRepository.findAllById(asignacionIds);
        for (Asignacion a : asignaciones) {
            a.setEnviado(enviado);
            a.setFechaEnvio(enviado ? LocalDate.now() : null);
        }
        asignacionRepository.saveAll(asignaciones);
        return asignaciones.size();
    }

    // #14: edita una asignación existente del historial (incluido el cliente).
    @Transactional
    public AsignacionResponse editarAsignacion(Integer id, EditarAsignacionRequest req) {
        Asignacion a = asignacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asignación no encontrada con id: " + id));
        Cliente cliente = clienteRepository.findById(req.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + req.getClienteId()));
        Ejecutivo ejecutivo = ejecutivoRepository.findById(req.getEjecutivoId())
                .orElseThrow(() -> new ResourceNotFoundException("Ejecutivo no encontrado con id: " + req.getEjecutivoId()));
        Empresa empresa = empresaRepository.findById(req.getEmpresaId())
                .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada con id: " + req.getEmpresaId()));
        TipoPorta tipoPorta = tipoPortaRepository.findById(req.getTipoPortaId())
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de porta no encontrado con id: " + req.getTipoPortaId()));

        if (!tipoPorta.getTipoDosimetro().getId().equals(a.getDosimetro().getTipoDosimetro().getId())) {
            throw new IllegalArgumentException("El tipo de porta '" + tipoPorta.getNombre()
                    + "' no es compatible con el dosímetro " + a.getDosimetro().getNumero());
        }

        a.setCliente(cliente);
        a.setEjecutivo(ejecutivo);
        a.setEmpresa(empresa);
        a.setTipoPorta(tipoPorta);
        a.setTrimestre(req.getTrimestre());
        a.setLinkTrello(req.getLinkTrello());
        return toResponse(asignacionRepository.save(a));
    }

    // #14 (Correcciones): busca asignaciones con filtros (admin) para corregir en lote.
    public List<AsignacionResponse> buscarAsignaciones(Integer clienteId, Integer ejecutivoId,
                                                       Integer empresaId, String trimestre,
                                                       Integer tipoPortaId, String link) {
        String tri = (trimestre == null || trimestre.isBlank()) ? null : trimestre.trim();
        String lnk = (link == null || link.isBlank()) ? null : link.trim();
        return asignacionRepository
                .filtrarAsignaciones(clienteId, ejecutivoId, empresaId, tri, tipoPortaId, lnk)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * #14 (Correcciones): cambia un mismo campo en varias asignaciones a la vez.
     * campo ∈ {linkTrello, clienteId, ejecutivoId, empresaId, tipoPortaId}.
     */
    @Transactional
    public int correccionMasiva(CorreccionMasivaRequest request) {
        List<Asignacion> asignaciones = asignacionRepository.findAllById(request.getAsignacionIds());
        String campo = request.getCampo();
        String valor = request.getValor();

        // Resolver la entidad destino una sola vez cuando aplica.
        Cliente cliente = null;
        Ejecutivo ejecutivo = null;
        Empresa empresa = null;
        TipoPorta tipoPorta = null;
        switch (campo) {
            case "linkTrello" -> {
                if (valor == null || valor.isBlank()) {
                    throw new IllegalArgumentException("El link de Trello no puede quedar vacío");
                }
            }
            case "clienteId" -> cliente = clienteRepository.findById(Integer.valueOf(valor))
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + valor));
            case "ejecutivoId" -> ejecutivo = ejecutivoRepository.findById(Integer.valueOf(valor))
                    .orElseThrow(() -> new ResourceNotFoundException("Ejecutivo no encontrado con id: " + valor));
            case "empresaId" -> empresa = empresaRepository.findById(Integer.valueOf(valor))
                    .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada con id: " + valor));
            case "tipoPortaId" -> tipoPorta = tipoPortaRepository.findById(Integer.valueOf(valor))
                    .orElseThrow(() -> new ResourceNotFoundException("Tipo de porta no encontrado con id: " + valor));
            default -> throw new IllegalArgumentException("Campo no permitido para corrección: " + campo);
        }

        for (Asignacion a : asignaciones) {
            switch (campo) {
                case "linkTrello" -> a.setLinkTrello(valor.trim());
                case "clienteId" -> a.setCliente(cliente);
                case "ejecutivoId" -> a.setEjecutivo(ejecutivo);
                case "empresaId" -> a.setEmpresa(empresa);
                case "tipoPortaId" -> {
                    if (!tipoPorta.getTipoDosimetro().getId()
                            .equals(a.getDosimetro().getTipoDosimetro().getId())) {
                        throw new IllegalArgumentException(
                                "El tipo de porta '" + tipoPorta.getNombre()
                                        + "' no es compatible con el dosímetro #" + a.getDosimetro().getNumero());
                    }
                    a.setTipoPorta(tipoPorta);
                }
            }
        }
        asignacionRepository.saveAll(asignaciones);
        return asignaciones.size();
    }

    private AsignacionResponse toResponse(Asignacion a) {
        AsignacionResponse r = new AsignacionResponse(
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
        r.setEnviado(a.isEnviado());
        r.setFechaEnvio(a.getFechaEnvio());
        return r;
    }
}