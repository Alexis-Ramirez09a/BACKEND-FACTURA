package com.factura.facturacion.entidades.factura;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.factura.facturacion.entidades.base.EntidadAuditable;
import com.factura.facturacion.entidades.catalogo.ImpuestoTarifa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "facturas_detalle_impuestos")
public class FacturaDetalleImpuesto extends EntidadAuditable {

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "detalle_id")
    private FacturaDetalle detalle;

    @ManyToOne(optional = false)
    @JoinColumn(name = "impuesto_tarifa_id")
    private ImpuestoTarifa impuestoTarifa;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal baseImponible;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal valor;

    @Column(name = "codigo_impuesto", length = 2)
    private String codigoImpuesto; // ejemplo: 2 = IVA

    @Column(name = "codigo_porcentaje", length = 2)
    private String codigoPorcentaje; // ejemplo: 2 = IVA 12%

    @Column(precision = 5, scale = 2)
    private BigDecimal porcentaje; // ejemplo: 12.00

    // Getters y setters

    public FacturaDetalle getDetalle() {
        return detalle;
    }

    public void setDetalle(FacturaDetalle detalle) {
        this.detalle = detalle;
    }

    public ImpuestoTarifa getImpuestoTarifa() {
        return impuestoTarifa;
    }

    public void setImpuestoTarifa(ImpuestoTarifa impuestoTarifa) {
        this.impuestoTarifa = impuestoTarifa;
    }

    public BigDecimal getBaseImponible() {
        return baseImponible;
    }

    public void setBaseImponible(BigDecimal baseImponible) {
        this.baseImponible = baseImponible;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getCodigoImpuesto() {
        return codigoImpuesto;
    }

    public void setCodigoImpuesto(String codigoImpuesto) {
        this.codigoImpuesto = codigoImpuesto;
    }

    public String getCodigoPorcentaje() {
        return codigoPorcentaje;
    }

    public void setCodigoPorcentaje(String codigoPorcentaje) {
        this.codigoPorcentaje = codigoPorcentaje;
    }

    public BigDecimal getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(BigDecimal porcentaje) {
        this.porcentaje = porcentaje;
    }
}
