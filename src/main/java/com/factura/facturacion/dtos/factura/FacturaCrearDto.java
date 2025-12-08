package com.factura.facturacion.dtos.factura;

import java.time.LocalDate;
import java.util.List;

public class FacturaCrearDto {

    private Long empresaId;
    private Long establecimientoId;
    private Long puntoEmisionId;
    private Long clienteId;
    private LocalDate fechaEmision;
    private String observacion;
    private String guiaRemision;

    private List<FacturaDetalleCrearDto> detalles;
    private List<FacturaPagoCrearDto> pagos;

    public Long getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(Long empresaId) {
        this.empresaId = empresaId;
    }

    public Long getEstablecimientoId() {
        return establecimientoId;
    }

    public void setEstablecimientoId(Long establecimientoId) {
        this.establecimientoId = establecimientoId;
    }

    public Long getPuntoEmisionId() {
        return puntoEmisionId;
    }

    public void setPuntoEmisionId(Long puntoEmisionId) {
        this.puntoEmisionId = puntoEmisionId;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public LocalDate getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDate fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public String getGuiaRemision() {
        return guiaRemision;
    }

    public void setGuiaRemision(String guiaRemision) {
        this.guiaRemision = guiaRemision;
    }

    public List<FacturaDetalleCrearDto> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<FacturaDetalleCrearDto> detalles) {
        this.detalles = detalles;
    }

    public List<FacturaPagoCrearDto> getPagos() {
        return pagos;
    }

    public void setPagos(List<FacturaPagoCrearDto> pagos) {
        this.pagos = pagos;
    }
}
