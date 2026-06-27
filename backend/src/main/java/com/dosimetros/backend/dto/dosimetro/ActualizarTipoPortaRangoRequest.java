package com.dosimetros.backend.dto.dosimetro;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class ActualizarTipoPortaRangoRequest {

    @NotNull(message = "tareaId es obligatorio")
    private Integer tareaId;

    @NotNull(message = "bandejaDesde es obligatorio")
    @Min(value = 1, message = "bandejaDesde debe ser mayor a 0")
    private Integer bandejaDesde;

    @NotNull(message = "bandejaHasta es obligatorio")
    @Min(value = 1, message = "bandejaHasta debe ser mayor a 0")
    private Integer bandejaHasta;

    // null = no filtrar por slot (aplica a toda la bandeja)
    private Integer slotDesde;
    private Integer slotHasta;

    @NotNull(message = "tipoPortaId es obligatorio")
    private Integer tipoPortaId;

    public ActualizarTipoPortaRangoRequest() {}

    public Integer getTareaId() { return tareaId; }
    public void setTareaId(Integer tareaId) { this.tareaId = tareaId; }

    public Integer getBandejaDesde() { return bandejaDesde; }
    public void setBandejaDesde(Integer bandejaDesde) { this.bandejaDesde = bandejaDesde; }

    public Integer getBandejaHasta() { return bandejaHasta; }
    public void setBandejaHasta(Integer bandejaHasta) { this.bandejaHasta = bandejaHasta; }

    public Integer getSlotDesde() { return slotDesde; }
    public void setSlotDesde(Integer slotDesde) { this.slotDesde = slotDesde; }

    public Integer getSlotHasta() { return slotHasta; }
    public void setSlotHasta(Integer slotHasta) { this.slotHasta = slotHasta; }

    public Integer getTipoPortaId() { return tipoPortaId; }
    public void setTipoPortaId(Integer tipoPortaId) { this.tipoPortaId = tipoPortaId; }
}
