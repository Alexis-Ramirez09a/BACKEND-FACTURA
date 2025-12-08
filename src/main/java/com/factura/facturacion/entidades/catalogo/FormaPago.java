package com.factura.facturacion.entidades.catalogo;

import com.factura.facturacion.entidades.base.EntidadAuditable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "formas_pago")
public class FormaPago extends EntidadAuditable {

    @Column(name = "codigo_sri", nullable = false, length = 2, unique = true)
    private String codigoSri; // 01, 19, 20, etc.

    @Column(nullable = false, length = 150)
    private String descripcion;

    @Column(nullable = false)
    private Boolean activo = true;

    // Getters y setters

    public String getCodigoSri() {
        return codigoSri;
    }

    public void setCodigoSri(String codigoSri) {
        this.codigoSri = codigoSri;
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
//📌 FormaPago = métodos de pago reconocidos por el SRI.//