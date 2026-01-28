package com.factura.facturacion.entidades.catalogo;

import java.math.BigDecimal;

import com.factura.facturacion.entidades.base.EntidadAuditable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "impuestos_tarifas")
public class ImpuestoTarifa extends EntidadAuditable {

    @ManyToOne(optional = false)
    @JoinColumn(name = "impuesto_id")
    private Impuesto impuesto; // IVA, ICE, etc.

    @Column(name = "codigo_tarifa", nullable = false, length = 2)
    private String codigoTarifa; // 0, 2, 6, 7, etc. (códigos SRI)

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal porcentaje; // 0.00, 12.00, etc.

    @Column(length = 200)
    private String descripcion;

    @Column(nullable = false)
    private Boolean activo = true;

    // Getters y setters

    public Impuesto getImpuesto() {
        return impuesto;
    }

    public void setImpuesto(Impuesto impuesto) {
        this.impuesto = impuesto;
    }

    public String getCodigoTarifa() {
        return codigoTarifa;
    }

    public void setCodigoTarifa(String codigoTarifa) {
        this.codigoTarifa = codigoTarifa;
    }

    public BigDecimal getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(BigDecimal porcentaje) {
        this.porcentaje = porcentaje;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}

// 📌 ImpuestoTarifa = las tarifas o porcentajes aplicables a un impuesto del
// SRI.
// 📌 Un impuesto (IVA) puede tener varias tarifas (0%, 12%, etc.).
// 📌 Esta info se usa para calcular impuestos en productos y facturas.
// 📌 Es esencial para generar el XML que el SRI valida.