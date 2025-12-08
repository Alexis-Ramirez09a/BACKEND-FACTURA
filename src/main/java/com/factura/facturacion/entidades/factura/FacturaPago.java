package com.factura.facturacion.entidades.factura;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.factura.facturacion.entidades.base.EntidadAuditable;
import com.factura.facturacion.entidades.catalogo.FormaPago;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "facturas_pago")
public class FacturaPago extends EntidadAuditable {

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "factura_id")
    private Factura factura;

    @ManyToOne(optional = false)
    @JoinColumn(name = "forma_pago_id")
    private FormaPago formaPago;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    @Column(nullable = false)
    private Integer plazo = 0; // Para créditos

    @Column(name = "unidad_tiempo", length = 10)
    private String unidadTiempo = "dias"; // dias, meses

    // Getters y setters

    public Factura getFactura() {
        return factura;
    }

    public void setFactura(Factura factura) {
        this.factura = factura;
    }

    public FormaPago getFormaPago() {
        return formaPago;
    }

    public void setFormaPago(FormaPago formaPago) {
        this.formaPago = formaPago;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public Integer getPlazo() {
        return plazo;
    }

    public void setPlazo(Integer plazo) {
        this.plazo = plazo;
    }

    public String getUnidadTiempo() {
        return unidadTiempo;
    }

    public void setUnidadTiempo(String unidadTiempo) {
        this.unidadTiempo = unidadTiempo;
    }
}
